package com.infiproton.maps.service;

import com.infiproton.maps.dto.*;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.ServiceAreaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderPlanningService {
    private final PlaceDetailsService placeDetailsService;
    private final ServiceAreaService serviceAreaService;
    private final RouteService routeService;

    public RestaurantOrderResponse planRestaurantOrder(RestaurantOrderRequest request) {
        String placeId = request.getPlaceId().trim();
        GeoPoint customerLocation = request.getCustomerLocation();

        // 1. Fetch Restaurant details
        PlaceDetailsResponseDTO placeDetails = placeDetailsService.getDetails(placeId);
        GeoPoint restaurantLocation = placeDetails.getLocation();

        // 2. service area validation
        ServiceAreaStatus serviceAreaStatus = serviceAreaService.validateLocation(customerLocation);
        if(serviceAreaStatus.equals(ServiceAreaStatus.OUTSIDE_SERVICE_AREA)) {
            return RestaurantOrderResponse.builder()
                    .placeId(placeId)
                    .restaurantLocation(restaurantLocation)
                    .restaurantName(placeDetails.getName())
                    .restaurantAddress(placeDetails.getFormattedAddress())
                    .customerInsideServiceArea(false)
                    .build();
        }

        // 3. compute route (from restaurant to customer)
        RouteResponse routeResponse = routeService.getRoute(new RouteRequest(restaurantLocation,
                customerLocation, "DRIVE", false, null));

        return RestaurantOrderResponse.builder()
                .placeId(placeId)
                .restaurantLocation(restaurantLocation)
                .restaurantName(placeDetails.getName())
                .restaurantAddress(placeDetails.getFormattedAddress())
                .customerInsideServiceArea(true)
                .routeDistanceInMeters(routeResponse.getDistanceMeters())
                .routeDurationSeconds(routeResponse.getDurationSeconds())
                .build();
    }


}
