// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.task;

import com.sun.management.OperatingSystemMXBean;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.management.ManagementFactory;

public class PerformanceTask extends BukkitRunnable {
    private final MagnetTask magnetTask;
    private final long delay = 0;
    private final long period;
    private final double cpuPercentage;
    private final double ramPercentage;
    private final OperatingSystemMXBean osBean;

    public PerformanceTask(MagnetTask magnetTask, long period, double cpuPercentage, double ramPercentage) {
        this.magnetTask = magnetTask;
        this.period = period;
        this.cpuPercentage = cpuPercentage;
        this.ramPercentage = ramPercentage;
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public void run() {
        double cpuUsage = osBean.getCpuLoad() * 100;

        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double ramUsage = (double) usedMemory / maxMemory * 100;

        if (cpuUsage > cpuPercentage || ramUsage > ramPercentage) {
            magnetTask.setStopped(true);
            return;
        }
        magnetTask.setStopped(false);
    }
}
