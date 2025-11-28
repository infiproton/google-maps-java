package com.infiproton.maps.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GeocodeCacheStore {

    private final Map<String, CachedGeocodeEntry> cache = new ConcurrentHashMap<>();

    public CachedGeocodeEntry get(String key) {
        return cache.get(key);
    }

    public void put(String key, CachedGeocodeEntry entry) {
        cache.put(key, entry);
    }

}
