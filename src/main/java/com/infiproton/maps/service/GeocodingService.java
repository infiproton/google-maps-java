package com.infiproton.maps.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.infiproton.maps.dto.GeocodeResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class GeocodingService {
    private final GeoApiContext geoApiContext;

    public GeocodeResponse geocode(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();

            if (results == null || results.length == 0) {
                return null;
            }

            GeocodingResult result = results[0];

            GeocodeResponse response = new GeocodeResponse();
            response.setFormattedAddress(result.formattedAddress);
            response.setLatitude(result.geometry.location.lat);
            response.setLongitude(result.geometry.location.lng);
            response.setPlaceId(result.placeId);
            response.setPartialMatch(result.partialMatch);

            if(result.types != null) {
                List<String> types = Arrays.stream(result.types)
                        .map(AddressType::toCanonicalLiteral)
                        .toList();
                response.setTypes(types);
            }
            return response;
        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException("Failed to call Geocoding API", e);
        }
    }

}
