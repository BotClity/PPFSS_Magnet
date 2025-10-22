// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.config;


import com.google.gson.annotations.SerializedName;
import com.ppfss.libs.config.YamlConfig;
import com.ppfss.libs.config.YamlConfigLoader;
import com.ppfss.libs.message.Message;
import com.ppfss.magnet.model.DefaultItemData;
import com.ppfss.magnet.model.ParticleData;
import com.ppfss.magnet.model.TasksSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Particle;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Config extends YamlConfig {
    private static Config instance;

    @SerializedName("permission-required")
    boolean permissionRequired = false;


    @SerializedName("particles")
    ParticleData particleData = new  ParticleData(
            true,
            Particle.END_ROD
    );


    @SerializedName("tasks")
    TasksSettings tasksSettings =  new TasksSettings(
            10, 1200, 90, 90
    );


    @SerializedName("default-item")
    DefaultItemData defaultItemData = new DefaultItemData(
            new Message("<red>Маг<blue>нит"),
            new Message(
                    "<light_purple>Обладает возможностью притягивать предметы",
                    "<light_purple>Радиус: <white><radius> бл.",
                    "<light_purple>Сила: <white><strength>",
                    "<light_purple>Лимит: <white><limit>",
                    "<white>",
                    "<gray>©PPFSS"
            ),
            Material.PLAYER_HEAD,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJhOGViYzRjNmE4MTczMDk0NzQ5OWJmN2UxZDVlNzNmZWQ2YzFiYjJjMDUxZTk2ZDM1ZWIxNmQyNDYxMGU3In19fQ=="

    );

    public static void load(YamlConfigLoader configLoader) {
        instance = configLoader.loadConfig("config", Config.class);
    }

    public static Config getInstance() {
        if (instance == null) throw new RuntimeException("Config didn't initialize yet!");
        return instance;
    }
}