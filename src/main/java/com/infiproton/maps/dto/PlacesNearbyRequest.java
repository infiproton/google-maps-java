package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.Data;

@Data
public class PlacesNearbyRequest {
    private GeoPoint location;
    private Integer radiusMeters;
    private String type;
}
