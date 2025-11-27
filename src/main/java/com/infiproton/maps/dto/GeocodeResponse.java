package com.infiproton.maps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResponse {
    private String formattedAddress;
    private double latitude;
    private double longitude;
    private String placeId;
    private List<String> types;
    private boolean partialMatch;
}
