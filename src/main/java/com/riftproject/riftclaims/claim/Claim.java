package com.riftproject.riftclaims.claim;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

// Класс самого привата. Теперь он очень простой: только владелец и регион.
public class Claim implements ConfigurationSerializable {

    private final UUID owner;
    private final Region region;

    public Claim(UUID owner, Region region) {
        this.owner = owner;
        this.region = region;
    }

    public UUID getOwner() {
        return owner;
    }

    public Region getRegion() {
        return region;
    }

    // Конструктор для загрузки из конфига
    public Claim(Map<String, Object> map) {
        this.owner = UUID.fromString((String) map.get("owner"));
        this.region = (Region) map.get("region");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("owner", owner.toString());
        map.put("region", region);
        return map;
    }
}