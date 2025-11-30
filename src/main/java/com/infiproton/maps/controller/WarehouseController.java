package com.infiproton.maps.controller;

import com.infiproton.maps.dto.NearestWarehouseRequest;
import com.infiproton.maps.dto.NearestWarehouseResponse;
import com.infiproton.maps.service.NearestWarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class WarehouseController {
    private final NearestWarehouseService nearestWarehouseService;

    @PostMapping("/warehouses/nearest")
    public NearestWarehouseResponse findNearestWarehouse(@RequestBody NearestWarehouseRequest request) {
        return nearestWarehouseService.findNearestWarehouse(request.location());
    }
}
