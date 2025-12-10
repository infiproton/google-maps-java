package com.infiproton.maps.controller;

import com.infiproton.maps.dto.*;
import com.infiproton.maps.service.PlaceDetailsService;
import com.infiproton.maps.service.PlacesNearbyService;
import com.infiproton.maps.service.PlacesSearchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class PlacesController {

    private final PlacesNearbyService placesNearbyService;
    private final PlacesSearchService placesSearchService;
    private final PlaceDetailsService  placeDetailsService;

    @PostMapping("/places/nearby")
    public PlacesNearbyResponse getNearbyPlaces(@RequestBody PlacesNearbyRequest request) {
        return placesNearbyService.searchNearby(request);
    }

    @PostMapping("/places/search")
    public PlacesSearchResponseDTO searchPlaces(@RequestBody PlacesSearchRequest request) {
        return placesSearchService.search(request);
    }

    @GetMapping("/places/details")
    public PlaceDetailsResponseDTO getPlaceDetails(@RequestParam("placeId") String placeId) {
        return placeDetailsService.getDetails(placeId);
    }
}
