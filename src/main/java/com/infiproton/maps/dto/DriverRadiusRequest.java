package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;

public record DriverRadiusRequest(GeoPoint pickup, double radiusKm) {
}
