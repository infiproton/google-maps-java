package com.infiproton.maps.model.routes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComputeRoutesResponse {
    private List<Route> routes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        private Long distanceMeters;
        private String duration; // e.g., "720s" or "PT12M"
        private Polyline polyline;

        private List<Leg> legs;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        private List<Step> steps;
        private Long distanceMeters;
        private String duration;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
        private NavigationInstruction navigationInstruction;
        private Long distanceMeters;
        private String staticDuration; // sometimes returned as "720s" or similar
        private Polyline polyline;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NavigationInstruction {
        private String instructions;
        private String maneuver;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Polyline {
        private String encodedPolyline;
    }
}
