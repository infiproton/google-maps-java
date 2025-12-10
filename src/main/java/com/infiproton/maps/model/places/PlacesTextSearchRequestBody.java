package com.infiproton.maps.model.places;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacesTextSearchRequestBody {
    private String textQuery;
    private Integer pageSize;
    private String pageToken;
}
