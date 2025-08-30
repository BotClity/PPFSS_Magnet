package com.ppfss.magnet;

import com.ppfss.magnet.command.magnet.MagnetCommand;
import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.config.MessageConfig;
import com.ppfss.magnet.config.YamlConfigLoader;
import com.ppfss.magnet.listener.MagnetListener;
import com.ppfss.magnet.service.MagnetService;
import com.ppfss.magnet.utils.LogUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PPFSS_Magnet extends JavaPlugin {

    @Override
    public void onEnable() {
        LogUtils.init(this);
        YamlConfigLoader configLoader = new YamlConfigLoader(this);

        registerConfigs(configLoader);

        MagnetService service = new MagnetService(this);

        new MagnetCommand(this, service);

        registerListeners(service);

        Metrics metrics = new Metrics(this, 23867);
        metrics.addCustomChart(new SingleLineChart("active_magnets", () -> service.getActiveMagnets().size()));
    }

    @Override
    public void onDisable() {
        Config.getInstance().save();
        MessageConfig.getInstance().save();
    }

    private void registerListeners(MagnetService service) {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MagnetListener(service, this), this);
    }

    private void registerConfigs(YamlConfigLoader configLoader) {
        Config.load(configLoader);
        MessageConfig.load(configLoader);
    }
}
