// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TasksSettings {
    @SerializedName("magnet-period")
    private int magnetPeriod;

    @SerializedName("performance-period")
    private int performancePeriod;

    @SerializedName("cpu-limit")
    private double cpuLimit;

    @SerializedName("ram-limit")
    private double ramLimit;
}
