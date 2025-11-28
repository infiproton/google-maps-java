package com.infiproton.maps.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.infiproton.maps.dto.ReverseGeocodingResponse;
import com.infiproton.maps.model.LocationUsability;
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

            String accuracy = result.geometry.locationType.toString();

            return ReverseGeocodingResponse.builder()
                    .formattedAddress(result.formattedAddress)
                    .placeId(result.placeId)
                    .accuracy(accuracy)
                    .usability(evaluateLocation(accuracy))
                    .build();

        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException("Reverse geocoding failed:", e);
        }
    }

    private LocationUsability evaluateLocation(String accuracy) {
        if (accuracy == null) {
            return LocationUsability.UNUSABLE;
        }

        return switch (accuracy) {
            case "ROOFTOP" -> LocationUsability.PRECISE_DELIVERY;
            case "RANGE_INTERPOLATED" -> LocationUsability.ROUTING_ONLY;
            case "GEOMETRIC_CENTER", "APPROXIMATE" -> LocationUsability.CITY_LEVEL_ONLY;
            default -> LocationUsability.UNUSABLE;
        };
    }

}
