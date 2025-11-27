package com.infiproton.maps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressValidationResponse {
    private Boolean valid;
    private String formattedAddress;
}
