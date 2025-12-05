package com.infiproton.maps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RouteResponse {
    private long distanceMeters;
    private long durationSeconds;
    private String polyline;
}
