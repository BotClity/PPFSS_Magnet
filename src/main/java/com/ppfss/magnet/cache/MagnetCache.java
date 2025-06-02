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
    private final Map<UUID, Integer> activeMagnets = new ConcurrentHashMap<>();
    private final Map<Integer, MagnetData> levelList = new ConcurrentHashMap<>();

    public void addMagnetLevel(int level, MagnetData magnetData) {
        levelList.put(level, magnetData);
    }

    public MagnetData getMagnetLevel(int level) {return levelList.get(level);}

    public void addActiveMagnet(UUID playerUUID, int level) {
        activeMagnets.put(playerUUID, level);
    }

    public Integer getActiveMagnet(UUID playerUUID) {
        return activeMagnets.get(playerUUID);
    }

    public void removeActiveMagnet(UUID playerUUID) {
        if (playerUUID == null)return;
        activeMagnets.remove(playerUUID);
    }

    public boolean containsActiveMagnet(UUID playerUUID) {
        return activeMagnets.containsKey(playerUUID);
    }


    public List<UUID> getActivePlayers() {
        return new ArrayList<>(activeMagnets.keySet());
    }
}
