package com.infiproton.maps.service;

import com.google.maps.addressvalidation.v1.AddressValidationClient;
import com.google.maps.addressvalidation.v1.ValidateAddressRequest;
import com.google.maps.addressvalidation.v1.ValidateAddressResponse;
import com.google.type.PostalAddress;
import com.infiproton.maps.dto.AddressValidationResponse;
import org.springframework.stereotype.Service;

@Service
public class AddressValidationService {
    private final AddressValidationClient client;

    public AddressValidationService(AddressValidationClient client) {
        this.client = client;
    }

    public AddressValidationResponse validate(String rawAddress) {
        PostalAddress postalAddress = PostalAddress.newBuilder().addAddressLines(rawAddress).build();
        ValidateAddressRequest req = ValidateAddressRequest.newBuilder().setAddress(postalAddress).build();

        ValidateAddressResponse resp = client.validateAddress(req);

        var result = resp.getResult();
        String formatted = result.getAddress().getFormattedAddress();
        boolean valid = result.getVerdict().getAddressComplete();

        return new AddressValidationResponse(valid, formatted);
    }
}
