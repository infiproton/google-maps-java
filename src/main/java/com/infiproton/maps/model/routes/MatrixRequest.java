package com.infiproton.maps.model.routes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatrixRequest {
    private List<WaypointWrapper> origins;
    private List<WaypointWrapper> destinations;
    private String travelMode;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaypointWrapper {
        private Waypoint waypoint;
    }
}
