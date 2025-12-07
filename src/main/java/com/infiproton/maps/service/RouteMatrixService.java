package com.infiproton.maps.service;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.dto.DriverEtaResponse;
import com.infiproton.maps.dto.DriversNearbyRequest;
import com.infiproton.maps.model.Driver;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.routes.MatrixEntry;
import com.infiproton.maps.model.routes.MatrixRequest;
import com.infiproton.maps.model.routes.Waypoint;
import com.infiproton.maps.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteMatrixService {
    private final String mapsApiKey;
    private final RestTemplate restTemplate;
    private final String COMPUTE_MATRIX_URL = "https://routes.googleapis.com/distanceMatrix/v2:computeRouteMatrix";

    private static final double PREFILTER_KM = 3.0d;

    public RouteMatrixService(@Value("${maps.api.key}") String mapsApiKey, RestTemplate restTemplate) {
        this.mapsApiKey = mapsApiKey;
        this.restTemplate = restTemplate;
    }

    public List<DriverEtaResponse> findDriversByEta(DriversNearbyRequest req) {
        GeoPoint customer = req.customerLocation();

        // 1. Prefilter drivers within 3km using Haversine
        List<Driver> candidates = prefilterDrivers(customer);
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Build matrix request (origins = prefiltered drivers, 1 destination = customer)
        MatrixRequest matrixRequest = buildMatrixRequest(candidates, customer);

        // 3) Call matrix API
        List<MatrixEntry> entries = callMatrixApi(matrixRequest);

        // 4) Map entries -> DriverEtaResponse
        List<DriverEtaResponse> results = parseMatrixEntries(entries, candidates);

        // 5) Sort by ETA
        return results.stream()
                .sorted(Comparator.comparingLong(DriverEtaResponse::getEtaSeconds))
                .collect(Collectors.toList());
    }

    private List<Driver> prefilterDrivers(GeoPoint customer) {
        return MockSpatialData.DRIVERS.stream()
                .filter(d -> {
                    double distKm = DistanceCalculator.distanceInKm(d.location(), customer);
                    return distKm <= PREFILTER_KM;
                })
                .collect(Collectors.toList());
    }

    private MatrixRequest buildMatrixRequest(List<Driver> drivers, GeoPoint customer) {
        List<MatrixRequest.WaypointWrapper> origins = drivers.stream()
                .map(d -> {
                    Waypoint.LatLng ll = new Waypoint.LatLng(d.location().lat(), d.location().lng());
                    Waypoint.Location loc = new Waypoint.Location(ll);
                    return new MatrixRequest.WaypointWrapper(new Waypoint(loc));
                }).collect(Collectors.toList());

        Waypoint.LatLng destLatLng = new Waypoint.LatLng(customer.lat(), customer.lng());
        Waypoint.Location destLoc = new Waypoint.Location(destLatLng);
        MatrixRequest.WaypointWrapper destination = new MatrixRequest.WaypointWrapper(new Waypoint(destLoc));

        return new MatrixRequest(origins, List.of(destination), "DRIVE");
    }

    private List<MatrixEntry> callMatrixApi(MatrixRequest body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", mapsApiKey);
        headers.set("X-Goog-FieldMask", "originIndex,destinationIndex,distanceMeters,duration,status,condition");

        HttpEntity<MatrixRequest> entity = new HttpEntity<>(body, headers);
        ResponseEntity<MatrixEntry[]> respEntity;
        try {
            respEntity = restTemplate.exchange(
                    COMPUTE_MATRIX_URL,
                    HttpMethod.POST,
                    entity,
                    MatrixEntry[].class
            );
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Matrix API call failed: " + ex.getMessage(), ex);
        }

        MatrixEntry[] arr = respEntity.getBody();
        if (arr == null || arr.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(arr);
    }

    private List<DriverEtaResponse> parseMatrixEntries(List<MatrixEntry> entries,
                                                       List<Driver> candidates) {
        List<DriverEtaResponse> out = new ArrayList<>();
        for (MatrixEntry e : entries) {
            Integer originIndex = e.getOriginIndex();
            Driver driver = candidates.get(originIndex);

            long distance = e.getDistanceMeters() != null ? e.getDistanceMeters() : 0L;
            long etaSeconds = RouteService.parseDurationToSeconds(e.getDuration());
            String condition = e.getCondition();

            out.add(new DriverEtaResponse(driver, etaSeconds, distance, condition));
        }
        return out;
    }
}
