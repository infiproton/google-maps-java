package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;

import java.util.List;

public record RouteRequest(GeoPoint origin, GeoPoint destination,
                           String travelMode, Boolean alternativeRoutes,
                           List<GeoPoint> waypoints) {
}
