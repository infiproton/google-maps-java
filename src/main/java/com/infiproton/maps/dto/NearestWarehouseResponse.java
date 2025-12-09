package com.infiproton.maps.dto;

import com.infiproton.maps.model.Warehouse;

public record NearestWarehouseResponse(Warehouse warehouse, long etaSeconds, long distanceMeters, String condition) {
}
