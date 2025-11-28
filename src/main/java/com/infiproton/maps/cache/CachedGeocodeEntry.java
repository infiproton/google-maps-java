package com.infiproton.maps.cache;

import lombok.Data;

import java.time.Instant;

@Data
public class CachedGeocodeEntry {
    private Object response;
    private Instant expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}
