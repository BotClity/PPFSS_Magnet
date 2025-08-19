// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.config;


import com.google.gson.annotations.SerializedName;
import com.ppfss.magnet.model.DefaultItemData;
import com.ppfss.magnet.model.ParticleData;
import com.ppfss.magnet.model.TasksSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Config extends YamlConfig {
    private static Config instance;

    @SerializedName("permission-required")
    boolean permissionRequired;
    @SerializedName("particles")
    ParticleData particleData;
    @SerializedName("tasks")
    TasksSettings tasksSettings;
    @SerializedName("default-item")
    DefaultItemData defaultItemData;

    public static void load(YamlConfigLoader configLoader) {
        instance = configLoader.loadConfig("config", Config.class);
    }

    public static Config getInstance() {
        if (instance == null) throw new RuntimeException("Config didn't initialize yet!");
        return instance;
    }
}