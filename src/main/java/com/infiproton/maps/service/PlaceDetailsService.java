package com.infiproton.maps.service;

import com.infiproton.maps.dto.PlaceDetailsResponseDTO;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.places.PlaceDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PlaceDetailsService {

    private static final String PLACE_DETAILS_BASE_URL = "https://places.googleapis.com/v1/places/";
    private final RestTemplate restTemplate;

    @Value("${maps.api.key}")
    private String mapsApiKey;

    public PlaceDetailsResponseDTO getDetails(String placeId) {
        PlaceDetails placeDetails = callPlacesDetailsApi(placeId);
        return mapToResponse(placeDetails);
    }

    private PlaceDetails callPlacesDetailsApi(String placeId) {
        String url = PLACE_DETAILS_BASE_URL + placeId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Goog-Api-Key", mapsApiKey);
        headers.set("X-Goog-FieldMask", "id," +
                "displayName," +
                "formattedAddress," +
                "location," +
                "primaryType," +
                "rating," +
                "userRatingCount," +
                "nationalPhoneNumber," +
                "websiteUri," +
                "currentOpeningHours.openNow," +
                "currentOpeningHours.weekdayDescriptions");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<PlaceDetails> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, entity, PlaceDetails.class);

            return responseEntity.getBody();
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Place Details API call failed", ex);
        }
    }

    private PlaceDetailsResponseDTO mapToResponse(PlaceDetails details) {
        if (details == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Place not found");
        }
        GeoPoint location = null;
        if (details.getLocation() != null) {
            location = new GeoPoint(details.getLocation().getLatitude(), details.getLocation().getLongitude());
        }
        String name = null;
        if (details.getDisplayName() != null) {
            name = details.getDisplayName().getText();
        }
        PlaceDetailsResponseDTO.OpeningHours openingHours = null;
        if (details.getCurrentOpeningHours() != null) {
            openingHours = new PlaceDetailsResponseDTO.OpeningHours(
                    details.getCurrentOpeningHours().getOpenNow(),
                    details.getCurrentOpeningHours().getWeekdayDescriptions()
            );
        }
        return new PlaceDetailsResponseDTO(
                details.getId(),
                name,
                details.getFormattedAddress(),
                location,
                details.getPrimaryType(),
                details.getRating(),
                details.getUserRatingCount(),
                details.getNationalPhoneNumber(),
                details.getWebsiteUri(),
                openingHours
        );
    }
}
