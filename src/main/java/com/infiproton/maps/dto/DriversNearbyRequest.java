package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;

public record DriversNearbyRequest(GeoPoint customerLocation) {
}
