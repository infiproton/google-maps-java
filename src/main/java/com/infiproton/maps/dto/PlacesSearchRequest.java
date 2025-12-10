package com.infiproton.maps.dto;

import lombok.Data;

@Data
public class PlacesSearchRequest {
    private String query;

    private Integer pageSize;
    private String pageToken;
}
