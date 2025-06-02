// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.service;

import com.ppfss.magnet.task.MagnetTask;
import com.ppfss.magnet.task.PerformanceTask;
import org.bukkit.plugin.Plugin;

public class TaskService {
    private final MagnetTask magnetTask;
    private final PerformanceTask performanceTask;
    private final Plugin plugin;

    public TaskService(final MagnetTask magnetTask, PerformanceTask performanceTask, Plugin plugin) {
        this.magnetTask = magnetTask;
        this.plugin = plugin;
        this.performanceTask = performanceTask;
    }

    public void startTasks() {
        magnetTask.runTaskTimer(plugin, magnetTask.getDelay(), magnetTask.getPeriod());
        performanceTask.runTaskTimer(plugin, magnetTask.getDelay(), magnetTask.getPeriod());
    }
}
