package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;

public record RouteRequest(GeoPoint origin, GeoPoint destination,
                           String travelMode, Boolean alternativeRoutes) {
}
