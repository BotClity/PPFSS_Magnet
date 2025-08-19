// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.config;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
public abstract class YamlConfig {
    private transient YamlConfigLoader configLoader = null;
    private transient File file;

    public YamlConfig() {}

    public void save(){
        if (configLoader == null) return;
        configLoader.saveConfig(this);
    }
}
