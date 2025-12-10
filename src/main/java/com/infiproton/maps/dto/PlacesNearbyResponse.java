package com.infiproton.maps.dto;

import com.infiproton.maps.model.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlacesNearbyResponse {
    private List<PlaceSummary> places;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaceSummary {
        private String placeId;
        private String name;
        private GeoPoint location;
        private String primaryType;
        private Double rating;
        private Boolean openNow;
    }

}
