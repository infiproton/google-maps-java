package com.infiproton.maps.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.infiproton.maps.dto.ReverseGeocodingResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@AllArgsConstructor
public class ReverseGeocodeService {
    private final GeoApiContext geoApiContext;

    public ReverseGeocodingResponse reverseGeocode(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);

        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, location).await();
            if (results == null || results.length == 0) {
                return null;
            }
            GeocodingResult result = results[0];
            return ReverseGeocodingResponse.builder()
                    .formattedAddress(result.formattedAddress)
                    .placeId(result.placeId)
                    .accuracy(result.geometry.locationType.toString())
                    .build();

        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException("Reverse geocoding failed:", e);
        }
    }

}
