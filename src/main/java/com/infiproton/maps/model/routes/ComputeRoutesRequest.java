package com.infiproton.maps.model.routes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputeRoutesRequest {
    private Waypoint origin;
    private Waypoint destination;
    private String travelMode;         // e.g., "DRIVE"
    private String routingPreference;  // e.g., "TRAFFIC_AWARE"

    private Boolean computeAlternativeRoutes;

    private List<Waypoint> intermediates;
}
