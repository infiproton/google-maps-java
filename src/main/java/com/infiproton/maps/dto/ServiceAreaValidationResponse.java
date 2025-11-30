package com.infiproton.maps.dto;

import com.infiproton.maps.model.ServiceAreaStatus;

public record ServiceAreaValidationResponse(ServiceAreaStatus status, String message) {
}
