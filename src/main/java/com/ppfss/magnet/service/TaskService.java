// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.service;

import com.ppfss.magnet.task.MagnetTask;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskService {
    private final MagnetTask magnetTask;
    private final Plugin plugin;

    public TaskService(final MagnetTask magnetTask, Plugin plugin) {
        this.magnetTask = magnetTask;
        this.plugin = plugin;
    }

    public void startTasks() {
        magnetTask.runTaskTimer(plugin, magnetTask.getDelay(), magnetTask.getPeriod());
    }
}
