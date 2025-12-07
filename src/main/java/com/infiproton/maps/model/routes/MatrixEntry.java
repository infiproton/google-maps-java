package com.infiproton.maps.model.routes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatrixEntry {
    private Integer originIndex;
    private Integer destinationIndex;

    private Long distanceMeters;
    private String duration;    // e.g. "1470s"
    private String condition;   // e.g. "ROUTE_EXISTS"
}
