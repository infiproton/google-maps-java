package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantOrderResponse {
    // Restaurant info
    private String placeId;
    private String restaurantName;
    private GeoPoint restaurantLocation;
    private String restaurantAddress;

    // Serviceability
    private Boolean customerInsideServiceArea;

    // Route (restaurant → customer)
    private Long routeDistanceInMeters;
    private Long routeDurationSeconds;
}
