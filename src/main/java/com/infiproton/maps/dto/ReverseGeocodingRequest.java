package com.infiproton.maps.dto;

import lombok.Data;

@Data
public class ReverseGeocodingRequest {
    private Double lat;
    private Double lng;
}
