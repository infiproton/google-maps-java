package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.Data;

@Data
public class RestaurantOrderRequest {
    private String placeId;
    private GeoPoint customerLocation;
}
