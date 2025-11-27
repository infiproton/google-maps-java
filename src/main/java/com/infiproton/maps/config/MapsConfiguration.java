package com.infiproton.maps.config;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.rpc.FixedHeaderProvider;
import com.google.maps.GeoApiContext;
import com.google.maps.addressvalidation.v1.AddressValidationClient;
import com.google.maps.addressvalidation.v1.AddressValidationSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
class MapsConfiguration {

    @Value("${maps.api.key:}")
    private String mapsApiKey;

    @Bean(destroyMethod = "shutdown")
    public GeoApiContext getGeoApiContext() {
        if (mapsApiKey == null || mapsApiKey.isBlank()) {
            throw new IllegalStateException("MAPS_API_KEY not set. Set env var MAPS_API_KEY before running.");
        }
        return new GeoApiContext.Builder().apiKey(mapsApiKey).build();
    }

    @Bean
    public AddressValidationClient addressValidationClient() throws Exception {
        if (mapsApiKey == null || mapsApiKey.isBlank()) {
            throw new IllegalStateException("MAPS_API_KEY not set. Set env var MAPS_API_KEY before running.");
        }

        var headers = Map.of("x-goog-api-key", mapsApiKey);

        AddressValidationSettings settings = AddressValidationSettings.newBuilder()
                .setHeaderProvider(FixedHeaderProvider.create(headers))
                .setCredentialsProvider(NoCredentialsProvider.create())
                .build();
        return AddressValidationClient.create(settings);
    }

}
