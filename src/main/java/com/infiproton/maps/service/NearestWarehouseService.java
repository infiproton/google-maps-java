package com.infiproton.maps.service;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.dto.NearestWarehouseResponse;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;
import com.infiproton.maps.model.routes.MatrixEntry;
import com.infiproton.maps.model.routes.MatrixRequest;
import com.infiproton.maps.model.routes.Waypoint;
import com.infiproton.maps.util.DistanceCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NearestWarehouseService {

    private final RouteMatrixService routeMatrixService;

    public NearestWarehouseResponse findNearestWarehouse(GeoPoint customerLocation) {
        List<Warehouse> warehouses = MockSpatialData.WAREHOUSES;

        // 1. Find nearest by ETA via matrix
        NearestWarehouseResponse viaMatrix = findByEta(warehouses, customerLocation);
        if (viaMatrix != null) {
            return viaMatrix;
        }

        // 2. Fallback to geometry-based selection
        return findByGeometryFallback(warehouses, customerLocation);
    }

    private NearestWarehouseResponse findByEta(List<Warehouse> warehouses, GeoPoint customerLocation) {
        MatrixRequest requestBody = buildMatrixRequest(warehouses, customerLocation);

        // call matrix api
        List<MatrixEntry> entries = routeMatrixService.callMatrixApi(requestBody);
        if (entries.isEmpty()) {
            return null;
        }

        Warehouse bestWarehouse = null;
        long bestEta = Long.MAX_VALUE;
        long bestDistanceMeters = Long.MAX_VALUE;
        String bestCondition = "ROUTE_EXISTS";

        for (MatrixEntry e : entries) {
            Integer originIndex = e.getOriginIndex();
            String condition = e.getCondition();

            long etaSeconds = RouteService.parseDurationToSeconds(e.getDuration());
            long distanceMeters = e.getDistanceMeters() != null ? e.getDistanceMeters() : 0L;
            if (etaSeconds < bestEta || (etaSeconds == bestEta && distanceMeters < bestDistanceMeters)) {
                bestEta = etaSeconds;
                bestDistanceMeters = distanceMeters;
                bestWarehouse = warehouses.get(originIndex);
                bestCondition = (condition != null) ? condition : "ROUTE_EXISTS";
            }
        }
        return new NearestWarehouseResponse(
                bestWarehouse,
                bestEta,
                bestDistanceMeters,
                bestCondition
        );
    }

    private MatrixRequest buildMatrixRequest(List<Warehouse> warehouses,
                                             GeoPoint customerLocation) {
        List<MatrixRequest.WaypointWrapper> origins = warehouses.stream()
                .map(w -> {
                    Waypoint.LatLng ll = new Waypoint.LatLng(w.location().lat(), w.location().lng());
                    Waypoint.Location loc = new Waypoint.Location(ll);
                    return new MatrixRequest.WaypointWrapper(new Waypoint(loc));
                })
                .toList();

        Waypoint.LatLng destLatLng = new Waypoint.LatLng(customerLocation.lat(), customerLocation.lng());
        Waypoint.Location destLoc = new Waypoint.Location(destLatLng);
        MatrixRequest.WaypointWrapper destination = new MatrixRequest.WaypointWrapper(new Waypoint(destLoc));
        return new MatrixRequest(origins, List.of(destination), "DRIVE");
    }

    private NearestWarehouseResponse findByGeometryFallback(List<Warehouse> warehouses, GeoPoint customerLocation) {
        Warehouse nearest = null;
        double minDistanceKm = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            double distanceKm = DistanceCalculator.distanceInKm(customerLocation, warehouse.location());
            if (distanceKm < minDistanceKm) {
                minDistanceKm = distanceKm;
                nearest = warehouse;
            }
        }
        if (nearest == null) {
            return null;
        }
        long distanceMeters = Math.round(minDistanceKm * 1000);

        // assume a speed to calculate eta
        double fallbackSpeedMetersPerSecond = 5.0;
        long etaSeconds = distanceMeters > 0
                ? Math.round(distanceMeters / fallbackSpeedMetersPerSecond)
                : 0L;
        return new NearestWarehouseResponse(nearest, etaSeconds,
                distanceMeters, "GEOMETRY_BASED"
        );
    }

}
