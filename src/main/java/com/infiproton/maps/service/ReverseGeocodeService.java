package com.infiproton.maps.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.infiproton.maps.cache.CachedGeocodeEntry;
import com.infiproton.maps.cache.GeocodeCacheStore;
import com.infiproton.maps.dto.ReverseGeocodingResponse;
import com.infiproton.maps.model.LocationUsability;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;


@Service
@AllArgsConstructor
public class ReverseGeocodeService {
    private final GeoApiContext geoApiContext;
    private final GeocodeCacheStore cacheStore;

    public ReverseGeocodingResponse reverseGeocode(double rawLat, double rawLng) {
        double lat = normalize(rawLat);
        double lng = normalize(rawLng);

        String cacheKey = buildCacheKey(lat, lng);

        CachedGeocodeEntry cached = cacheStore.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return (ReverseGeocodingResponse) cached.getResponse();
        }

        LatLng location = new LatLng(lat, lng);
        try {
            GeocodingResult[] results = GeocodingApi.reverseGeocode(geoApiContext, location).await();
            if (results == null || results.length == 0) {
                return null;
            }
            GeocodingResult result = results[0];

            String accuracy = result.geometry.locationType.toString();

            ReverseGeocodingResponse response = ReverseGeocodingResponse.builder()
                    .formattedAddress(result.formattedAddress)
                    .placeId(result.placeId)
                    .accuracy(accuracy)
                    .usability(evaluateLocation(accuracy))
                    .build();

            CachedGeocodeEntry entry = new CachedGeocodeEntry();
            entry.setResponse(response);
            entry.setExpiresAt(Instant.now().plusSeconds(3600));
            cacheStore.put(cacheKey, entry);

            return response;

        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException("Reverse geocoding failed:", e);
        }
    }

    private double normalize(double value) {
        return Math.round(value * 1_00000d) / 1_00000d;
    }

    private String buildCacheKey(double lat, double lng) {
        return lat + ":" + lng;
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
