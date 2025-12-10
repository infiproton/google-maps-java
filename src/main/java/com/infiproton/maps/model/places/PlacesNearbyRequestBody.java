package com.infiproton.maps.model.places;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacesNearbyRequestBody {
    private List<String> includedTypes;
    private Integer maxResultCount;
    private LocationRestriction locationRestriction;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationRestriction {
        private Circle circle;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Circle {
        private LatLng center;
        private double radius;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatLng {
        private double latitude;
        private double longitude;
    }
}
