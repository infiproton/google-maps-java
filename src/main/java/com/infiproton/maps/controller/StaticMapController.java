package com.infiproton.maps.controller;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.dto.NearestWarehouseResponse;
import com.infiproton.maps.dto.RouteRequest;
import com.infiproton.maps.dto.RouteResponse;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;
import com.infiproton.maps.service.NearestWarehouseService;
import com.infiproton.maps.service.RouteService;
import com.infiproton.maps.service.StaticMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/static-map")
@RequiredArgsConstructor
class StaticMapController {

    private final StaticMapService staticMapService;
    private final NearestWarehouseService nearestWarehouseService;
    private final RouteService routeService;

    @GetMapping(value = "/delivery-route")
    public ResponseEntity<byte[]> getDeliveryRouteMap() {
        GeoPoint customerLocation = MockSpatialData.CUSTOMER;

        NearestWarehouseResponse nearestWarehouseResult = nearestWarehouseService.findNearestWarehouse(customerLocation);
        Warehouse nearestWarehouse = nearestWarehouseResult.warehouse();

        RouteResponse routeResponse = routeService.getRoute(new RouteRequest(
                nearestWarehouse.location(),
                customerLocation,
                "DRIVE",
                false,
                null));

        String polyline = routeResponse.getPolyline();

        byte[] img = staticMapService.generateDeliveryRouteMapImage(MockSpatialData.CUSTOMER,
                nearestWarehouse, "blue", "red", routeResponse.getPolyline());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(img.length);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/drivers")
    public ResponseEntity<byte[]> getDriversMap() {
        byte[] img = staticMapService.generateCustomerAndDriversMapImage(MockSpatialData.CUSTOMER,
                MockSpatialData.DRIVERS, "blue", "red");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(img.length);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }


    @GetMapping(value = "/customer")
    public ResponseEntity<byte[]> getCustomerMap() {

        byte[] img = staticMapService.generateCustomerMapImage(MockSpatialData.CUSTOMER, "blue");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(img.length);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }
}
