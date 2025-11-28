package com.infiproton.maps.controller;

import com.infiproton.maps.dto.GeocodeRequest;
import com.infiproton.maps.dto.GeocodeResponse;
import com.infiproton.maps.dto.ReverseGeocodingRequest;
import com.infiproton.maps.dto.ReverseGeocodingResponse;
import com.infiproton.maps.service.GeocodingService;
import com.infiproton.maps.service.ReverseGeocodeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class GeocodingController {

    private final GeocodingService  geocodingService;
    private final ReverseGeocodeService reverseGeocodeService;

    @PostMapping("/reverse-geocode")
    public ResponseEntity<ReverseGeocodingResponse> reverseGeocode(@RequestBody ReverseGeocodingRequest request) {
        ReverseGeocodingResponse response = reverseGeocodeService.reverseGeocode(request.getLat(), request.getLng());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build() ;
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/geocode/bulk",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<byte[]> bulkGeocode(@RequestPart("file") MultipartFile file) throws IOException {
        byte[] cleanedCsv = geocodingService.bulkGeocode(file.getInputStream());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cleaned_addresses.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(cleanedCsv);
    }

    @PostMapping("/geocode")
    public ResponseEntity<GeocodeResponse> geocode(@RequestBody GeocodeRequest request) {
        GeocodeResponse response = geocodingService.geocode(request.getAddress());

        return ResponseEntity.ok(response);
    }
}
