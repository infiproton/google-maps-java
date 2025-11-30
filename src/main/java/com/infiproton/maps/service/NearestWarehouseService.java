package com.infiproton.maps.service;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.dto.NearestWarehouseResponse;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;
import com.infiproton.maps.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NearestWarehouseService {

    public NearestWarehouseResponse findNearestWarehouse(GeoPoint customerLocation) {
        List<Warehouse> warehouses = MockSpatialData.WAREHOUSES;

        Warehouse nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            double distance = DistanceCalculator.distanceInKm(
                    customerLocation,
                    warehouse.location()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearest = warehouse;
            }
        }
        return new NearestWarehouseResponse(nearest, minDistance);
    }
}
