package com.infiproton.maps.controller;

import com.infiproton.maps.dto.DriverEtaResponse;
import com.infiproton.maps.dto.DriverRadiusRequest;
import com.infiproton.maps.dto.DriversNearbyRequest;
import com.infiproton.maps.model.DriverDistance;
import com.infiproton.maps.service.DriverRadiusService;
import com.infiproton.maps.service.RouteMatrixService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class DriverController {
    private final DriverRadiusService driverRadiusService;
    private final RouteMatrixService routeMatrixService;

    @PostMapping("/drivers/within-radius")
    public List<DriverDistance> getDriversWithinRadius(@RequestBody DriverRadiusRequest request) {
        return driverRadiusService.findDriversWithinRadius(
                request.pickup(),
                request.radiusKm()
        );
    }

    @PostMapping("/drivers/nearby")
    public ResponseEntity<List<DriverEtaResponse>> nearby(@RequestBody DriversNearbyRequest req) {
        List<DriverEtaResponse> results = routeMatrixService.findDriversByEta(req);
        return ResponseEntity.ok(results);
    }
}
