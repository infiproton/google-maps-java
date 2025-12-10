package com.infiproton.maps.service;

import com.infiproton.maps.dto.PlacesNearbyRequest;
import com.infiproton.maps.dto.PlacesNearbyResponse;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.places.PlacesNearbyRequestBody;
import com.infiproton.maps.model.places.PlacesSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacesNearbyService {
    private static final String PLACES_NEARBY_URL = "https://places.googleapis.com/v1/places:searchNearby";
    private final RestTemplate restTemplate;
    @Value("${maps.api.key}")
    private String mapsApiKey;

    public PlacesNearbyResponse searchNearby(PlacesNearbyRequest request) {
        // 1. Build places API Request body
        PlacesNearbyRequestBody body = buildPlacesNearbyBody(request);

        // 2. Call Places API
        PlacesSearchResponse apiResponse = callPlacesApi(body);

        // 3. Process Response
        return mapToResponse(apiResponse);
    }

    private PlacesNearbyResponse mapToResponse(PlacesSearchResponse apiResponse) {
        List<PlacesNearbyResponse.PlaceSummary> summaries = apiResponse.getPlaces()
                .stream()
                .map(this::mapPlace)
                .toList();
        return new PlacesNearbyResponse(summaries);
    }

    private PlacesNearbyResponse.PlaceSummary mapPlace(PlacesSearchResponse.Place place) {
        var location = place.getLocation() != null
                ? new GeoPoint(place.getLocation().getLatitude(), place.getLocation().getLongitude())
                : null;

        String name = place.getDisplayName() != null
                ? place.getDisplayName().getText()
                : null;

        Boolean openNow = place.getCurrentOpeningHours() != null
                ? place.getCurrentOpeningHours().getOpenNow()
                : null;

        return new PlacesNearbyResponse.PlaceSummary(
                place.getId(),
                name,
                location,
                place.getPrimaryType(),
                place.getRating(),
                openNow
        );
    }

    private PlacesSearchResponse callPlacesApi(PlacesNearbyRequestBody body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", mapsApiKey);
        headers.set("X-Goog-FieldMask", "places.id," +
                "places.displayName," +
                "places.location," +
                "places.primaryType," +
                "places.rating," +
                "places.currentOpeningHours.openNow");

        HttpEntity<PlacesNearbyRequestBody> entity = new HttpEntity<>(body, headers);
        try {
            return restTemplate.exchange(PLACES_NEARBY_URL, HttpMethod.POST, entity, PlacesSearchResponse.class).getBody();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Places Nearby API call failed", ex);
        }
    }

    private PlacesNearbyRequestBody buildPlacesNearbyBody(PlacesNearbyRequest request) {
        GeoPoint center = request.getLocation();
        int radius = request.getRadiusMeters() != null ? request.getRadiusMeters() : 2000;
        String type = request.getType();

        PlacesNearbyRequestBody.LatLng latLng = new PlacesNearbyRequestBody.LatLng(center.lat(), center.lng());
        PlacesNearbyRequestBody.Circle circle = new PlacesNearbyRequestBody.Circle(latLng, radius);

        PlacesNearbyRequestBody.LocationRestriction locationRestriction =
                new PlacesNearbyRequestBody.LocationRestriction(circle);

        PlacesNearbyRequestBody body = new PlacesNearbyRequestBody();
        body.setIncludedTypes(List.of(type));
        body.setMaxResultCount(20);
        body.setLocationRestriction(locationRestriction);
        return body;
    }

}
