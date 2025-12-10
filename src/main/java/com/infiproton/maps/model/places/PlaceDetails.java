package com.infiproton.maps.model.places;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceDetails {

    private String id;
    private DisplayName displayName;
    private String formattedAddress;
    private Location location;
    private String primaryType;
    private Double rating;
    private Integer userRatingCount;
    private String nationalPhoneNumber;
    private String websiteUri;
    private CurrentOpeningHours currentOpeningHours;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DisplayName {
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private double latitude;
        private double longitude;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentOpeningHours {
        private Boolean openNow;
        private List<String> weekdayDescriptions;
    }
}
