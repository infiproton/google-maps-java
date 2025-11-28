package com.infiproton.maps.cache;

import com.infiproton.maps.dto.GeocodeResponse;
import lombok.Data;

import java.time.Instant;

@Data
public class CachedGeocodeEntry {
    private GeocodeResponse response;
    private Instant expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}
