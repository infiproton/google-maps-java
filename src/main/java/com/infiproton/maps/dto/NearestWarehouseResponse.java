package com.infiproton.maps.dto;

import com.infiproton.maps.model.Warehouse;

public record NearestWarehouseResponse(Warehouse warehouse, double distanceKm) {
}
