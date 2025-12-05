package com.infiproton.maps.controller;

import com.infiproton.maps.dto.RouteRequest;
import com.infiproton.maps.dto.RouteResponse;
import com.infiproton.maps.service.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class RouteController {
    private final RouteService routeService;

    @PostMapping("/route")
    public RouteResponse getRoute(@RequestBody RouteRequest request) {
        return routeService.getRoute(request);
    }

}
