package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDetailsResponseDTO {
    private String placeId;
    private String name;
    private String formattedAddress;
    private GeoPoint location;
    private String primaryType;
    private Double rating;
    private Integer userRatingCount;
    private String nationalPhoneNumber;
    private String websiteUri;
    private OpeningHours openingHours;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpeningHours {
        private Boolean openNow;
        private List<String> weekdayDescriptions;
    }
}
