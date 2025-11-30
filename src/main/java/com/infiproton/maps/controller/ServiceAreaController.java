package com.infiproton.maps.controller;

import com.infiproton.maps.dto.ServiceAreaValidationRequest;
import com.infiproton.maps.dto.ServiceAreaValidationResponse;
import com.infiproton.maps.model.ServiceAreaStatus;
import com.infiproton.maps.service.ServiceAreaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class ServiceAreaController {
    private final ServiceAreaService serviceAreaService;

    @PostMapping("/service-area/validate")
    public ServiceAreaValidationResponse validateServiceArea(@RequestBody ServiceAreaValidationRequest request) {
        ServiceAreaStatus status = serviceAreaService.validateLocation(request.location());
        String message = (status == ServiceAreaStatus.INSIDE_SERVICE_AREA)
                ? "Location is inside the configured service area."
                : "Location is outside the configured service area.";
        return new ServiceAreaValidationResponse(status, message);
    }


}
