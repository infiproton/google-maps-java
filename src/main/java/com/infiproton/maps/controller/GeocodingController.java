package com.infiproton.maps.controller;

import com.infiproton.maps.dto.GeocodeRequest;
import com.infiproton.maps.dto.GeocodeResponse;
import com.infiproton.maps.service.GeocodingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class GeocodingController {

    private final GeocodingService  geocodingService;

    @PostMapping("/geocode")
    public ResponseEntity<GeocodeResponse> geocode(@RequestBody GeocodeRequest request) {
        GeocodeResponse response = geocodingService.geocode(request.getAddress());

        return ResponseEntity.ok(response);
    }
}
