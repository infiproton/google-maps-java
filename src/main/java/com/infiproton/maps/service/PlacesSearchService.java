package com.infiproton.maps.service;

import com.infiproton.maps.dto.PlacesSearchRequest;
import com.infiproton.maps.dto.PlacesSearchResponseDTO;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.places.PlacesSearchResponse;
import com.infiproton.maps.model.places.PlacesTextSearchRequestBody;
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
public class PlacesSearchService {
    private static final String PLACES_SEARCH_TEXT_URL = "https://places.googleapis.com/v1/places:searchText";
    private final RestTemplate restTemplate;

    @Value("${maps.api.key}")
    private String mapsApiKey;

    public PlacesSearchResponseDTO search(PlacesSearchRequest request) {
        // 1. prepare request
        PlacesTextSearchRequestBody body = toTextSearchBody(request);

        // 2. call api
        PlacesSearchResponse apiResponse = callPlacesApi(body);

        // 3. map response
        return mapToResponse(apiResponse);
    }

    private PlacesSearchResponse callPlacesApi(PlacesTextSearchRequestBody body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", mapsApiKey);
        headers.set("X-Goog-FieldMask", "places.id," +
                "places.displayName," +
                "places.formattedAddress," +
                "places.location," +
                "places.primaryType," +
                "places.rating," +
                "places.currentOpeningHours.openNow," +
                "nextPageToken");

        HttpEntity<PlacesTextSearchRequestBody> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<PlacesSearchResponse> responseEntity = restTemplate.exchange(
                    PLACES_SEARCH_TEXT_URL,
                    HttpMethod.POST,
                    entity,
                    PlacesSearchResponse.class
            );
            return responseEntity.getBody();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Places Text Search API call failed", ex);
        }
    }

    private PlacesTextSearchRequestBody toTextSearchBody(PlacesSearchRequest request) {
        PlacesTextSearchRequestBody body = new PlacesTextSearchRequestBody();
        body.setTextQuery(request.getQuery());

        // add pagination
        if (request.getPageSize() != null && request.getPageSize() > 0) {
            body.setPageSize(request.getPageSize());
        }
        if (request.getPageToken() != null && !request.getPageToken().isBlank()) {
            body.setPageToken(request.getPageToken());
        }

        return body;
    }

    private PlacesSearchResponseDTO mapToResponse(PlacesSearchResponse apiResponse) {
        List<PlacesSearchResponseDTO.PlaceSummary> summaries = apiResponse.getPlaces()
                        .stream()
                        .map(this::mapPlace)
                        .toList();

        return new PlacesSearchResponseDTO(summaries, apiResponse.getNextPageToken());
    }

    private PlacesSearchResponseDTO.PlaceSummary mapPlace(PlacesSearchResponse.Place place) {

        GeoPoint location = null;
        if (place.getLocation() != null) {
            location = new GeoPoint(place.getLocation().getLatitude(), place.getLocation().getLongitude());
        }

        Boolean openNow = null;
        if (place.getCurrentOpeningHours() != null) {
            openNow = place.getCurrentOpeningHours().getOpenNow();
        }
        String name = null;
        if (place.getDisplayName() != null) {
            name = place.getDisplayName().getText();
        }
        String formattedAddress = place.getFormattedAddress();

        return new PlacesSearchResponseDTO.PlaceSummary(
                place.getId(),
                name,
                formattedAddress,
                location,
                place.getPrimaryType(),
                place.getRating(),
                openNow
        );

    }
}
