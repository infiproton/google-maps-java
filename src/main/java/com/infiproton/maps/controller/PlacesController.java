package com.infiproton.maps.controller;

import com.infiproton.maps.dto.PlacesNearbyRequest;
import com.infiproton.maps.dto.PlacesNearbyResponse;
import com.infiproton.maps.dto.PlacesSearchRequest;
import com.infiproton.maps.dto.PlacesSearchResponseDTO;
import com.infiproton.maps.service.PlacesNearbyService;
import com.infiproton.maps.service.PlacesSearchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class PlacesController {

    private final PlacesNearbyService placesNearbyService;
    private final PlacesSearchService placesSearchService;

    @PostMapping("/places/nearby")
    public PlacesNearbyResponse getNearbyPlaces(@RequestBody PlacesNearbyRequest request) {
        return placesNearbyService.searchNearby(request);
    }

    @PostMapping("/places/search")
    public PlacesSearchResponseDTO searchPlaces(@RequestBody PlacesSearchRequest request) {
        return placesSearchService.search(request);
    }
}
