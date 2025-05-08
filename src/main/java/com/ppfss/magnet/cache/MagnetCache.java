// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.cache;

import com.ppfss.magnet.domain.MagnetData;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MagnetCache {
    private final Map<UUID, MagnetData> activeMagnets = new ConcurrentHashMap<>();

    public void addActiveMagnet(UUID playerUUID, MagnetData magnet) {
        activeMagnets.put(playerUUID, magnet);
    }

    public MagnetData getActiveMagnet(UUID playerUUID) {
        return activeMagnets.get(playerUUID);
    }

    public MagnetData removeActiveMagnet(UUID playerUUID) {
        return activeMagnets.remove(playerUUID);
    }

    public boolean containsActiveMagnet(UUID playerUUID) {
        return activeMagnets.containsKey(playerUUID);
    }

    public List<MagnetData> getAllActiveMagnets() {
        return new ArrayList<>(activeMagnets.values());
    }

    public List<UUID> getActivePlayers() {
        return new ArrayList<>(activeMagnets.keySet());
    }
}
