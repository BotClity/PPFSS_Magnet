// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.config;

import com.google.gson.annotations.SerializedName;
import com.ppfss.libs.config.YamlConfig;
import com.ppfss.libs.config.YamlConfigLoader;
import com.ppfss.libs.message.Message;
import lombok.Getter;

@Getter
public class MessageConfig extends YamlConfig {
    private static MessageConfig instance;

    @SerializedName("magnet-activated")
    Message magnetActivated = new Message("<dark_green>[PPFSS]<green>Магнит активирован");

    @SerializedName("magnet-deactivated")
    Message magnetDeactivated = new Message("<dark_red>[PPFSS]<red>Магнит деактивирован");

    @SerializedName("no-permission")
    Message noPermission = new Message("<dark_red>[PPFSS]<red>Недостаточно прав!");

    @SerializedName("player-not-found")
    Message playerNotFound = new Message("<dark_red>[PPFSS]<red>Игрок <player> не найден!");

    @SerializedName("not-number")
    Message notNumber = new Message("<dark_red>[PPFSS]<red>Вы ввели не число!");

    @SerializedName("not-enough-space")
    Message notEnoughSpace = new Message("<dark_red>[PPFSS]<red>Недостаточно места!");

    @SerializedName("magnet-given")
    Message magnetGiven = new Message("<dark_green>[PPFSS]<green>Магнит выдан");

    @SerializedName("no-item-in-hand")
    Message noItemInHand = new Message("<dark_red>[PPFSS]<red>Нету предмета в основной руке!");

    @SerializedName("magnet-enchanted")
    Message magnetEnchanted = new Message("<dark_green>[PPFSS]<green>Магнит зачарован");

    @SerializedName("plugin-reloaded")
    Message pluginReloaded = new Message("<green>Конфиги плагина перезагружены");

    @SerializedName("plugin-reload-usage")
    Message pluginReloadUsage = new Message("<yellow>/magnet reload <white>- перезагружает конфиги");

    public static void load(YamlConfigLoader loader) {
        instance = loader.loadConfig("messages", MessageConfig.class);
    }

    public static MessageConfig getInstance() {
        if (instance == null) throw new RuntimeException("Config has not been initialized yet");
        return instance;
    }
}
