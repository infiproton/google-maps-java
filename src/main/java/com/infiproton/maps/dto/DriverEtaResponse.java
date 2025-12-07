package com.infiproton.maps.dto;

import com.infiproton.maps.model.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverEtaResponse {
    private Driver driver;
    private long etaSeconds;
    private long distanceMeters;
    private String condition;
}
