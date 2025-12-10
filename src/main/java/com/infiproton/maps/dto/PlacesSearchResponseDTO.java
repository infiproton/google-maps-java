package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlacesSearchResponseDTO {
    private List<PlaceSummary> places;
    private String nextPageToken;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaceSummary {
        private String placeId;
        private String name;
        private String formattedAddress;
        private GeoPoint location;
        private String primaryType;
        private Double rating;
        private Boolean openNow;
    }
}
