package com.infiproton.maps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReverseGeocodingResponse {
    private String formattedAddress;
    private String placeId;
    private String accuracy;
}
