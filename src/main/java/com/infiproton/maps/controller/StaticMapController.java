package com.infiproton.maps.controller;

import com.infiproton.maps.data.MockSpatialData;
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

    @GetMapping(value = "/customer")
    public ResponseEntity<byte[]> getCustomerMap() {

        byte[] img = staticMapService.generateCustomerMapImage(MockSpatialData.CUSTOMER, "blue");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(img.length);
        return new ResponseEntity<>(img, headers, HttpStatus.OK);
    }
}
