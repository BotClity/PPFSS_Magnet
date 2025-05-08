package com.ppfss.magnet;

import com.ppfss.magnet.cache.MagnetCache;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class PPFSS_Magnet extends JavaPlugin {
    private Metrics metrics;


    @Override
    public void onEnable() {
        // Plugin startup logic
        registerMetrics();

        MagnetCache cache = new MagnetCache();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerMetrics(){
        metrics = new Metrics(this, 23867);
    }
}
