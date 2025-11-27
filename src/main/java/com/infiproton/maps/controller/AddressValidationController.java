package com.infiproton.maps.controller;

import com.infiproton.maps.dto.AddressValidateRequest;
import com.infiproton.maps.dto.AddressValidationResponse;
import com.infiproton.maps.service.AddressValidationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
class AddressValidationController {
    private final AddressValidationService service;

    AddressValidationController(AddressValidationService service) {
        this.service = service;
    }

    @PostMapping("/validate-address")
    public ResponseEntity<AddressValidationResponse> validate(@Valid @RequestBody AddressValidateRequest request) {
        AddressValidationResponse result = service.validate(request.getAddress());
        return ResponseEntity.ok(result);
    }
}
