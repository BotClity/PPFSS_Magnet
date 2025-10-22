// PPFSS_Libs Plugin
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.libs.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.ppfss.libs.adapter.ComponentAdapter;
import com.ppfss.libs.adapter.MaterialAdapter;
import com.ppfss.libs.adapter.MessageAdapter;
import com.ppfss.libs.adapter.ParticleAdapter;
import com.ppfss.libs.message.Message;
import com.ppfss.libs.utils.LogUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class YamlConfigLoader {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .registerTypeAdapter(Message.class, new MessageAdapter())
            .registerTypeAdapter(Component.class, new ComponentAdapter())
            .registerTypeAdapter(Material.class, new MaterialAdapter())
            .registerTypeAdapter(Particle.class, new ParticleAdapter())
            .create();
    private final Yaml yaml;
    private final Plugin plugin;
    private final TypeToken<Map<String, Object>> mapToken = new TypeToken<>() {
    };
    private final Map<String, YamlConfig> cacheConfigs = new ConcurrentHashMap<>();

    public YamlConfigLoader(Plugin plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdirs();

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        this.yaml = new Yaml(options);
    }

    public <T extends YamlConfig> T loadConfig(String name, Class<T> type) {
        name = name.endsWith(".yml") ? name : name + ".yml";
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()){
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, name);

        if (!file.exists()) {
            try(InputStream in = plugin.getResource(name)) {

                if (in == null){
                    T instance = loadFromInstance(file, type);
                    cacheConfigs.put(name, instance);
                    return instance;
                }

                Files.copy(in, file.toPath());

            } catch (Exception exception) {
                LogUtils.error("Can't copy " + file.getName(), exception);
                throw new RuntimeException("Can't copy " + file.getName(), exception);
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        Map<String, Object> data = convertSectionToMap(config);

        String json = gson.toJson(data);

        T instance = gson.fromJson(json, type);

        instance.setFile(file);
        instance.setConfigLoader(this);

        boolean updated = applyDefaultsFromClass(instance, config);
        if (updated) {
            saveConfig(instance);
            LogUtils.info("Updated config with new defaults: " + file.getName());
        }

        cacheConfigs.put(name, instance);
        return instance;
    }

    private boolean applyDefaultsFromClass(Object instance, YamlConfiguration config) {
        boolean updated = false;
        Class<?> clazz = instance.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) continue;
            field.setAccessible(true);
            String path = field.getName();

            if (!config.contains(path)) {
                try {
                    Object value = field.get(instance);
                    if (value != null) {
                        updated = true;
                    }
                } catch (IllegalAccessException e) {
                    LogUtils.error("Failed to read field " + path + " in " + clazz.getSimpleName(), e);
                }
            }
        }

        return updated;
    }


    private Map<String, Object> convertSectionToMap(ConfigurationSection section) {
        Map<String, Object> result = new HashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                result.put(key, convertSectionToMap((ConfigurationSection) value));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    public void saveAll(){
        cacheConfigs.values().forEach(this::saveConfig);
    }

    public void saveConfig(YamlConfig instance) {
        YamlConfiguration config = new YamlConfiguration();
        File file = instance.getFile();

        try {
            config.load(file);
        } catch (Exception exception) {
            LogUtils.info("Create config file " + file.getName());
            try {
                file.createNewFile();
            }catch (Exception ex){
                throw new RuntimeException("Can't create file: " + file.getName(), ex);
            }

        }

        String json = gson.toJson(instance);

        Map<String, Object> map = gson.fromJson(json, mapToken.getType());

        map.forEach(config::set);

        try {
            config.save(file);
        } catch (Exception exception) {
            LogUtils.error("Error while saving config " + file.getName(), exception);
            throw new RuntimeException("Error while saving config " + file.getName(), exception);
        }
    }

    private <T extends YamlConfig> T loadFromInstance(File file, Class<T> type) {
        if (!hasEmptyConstructor(type)) {
            LogUtils.error("There's no empty constructor found for " + type.getName());
            throw new RuntimeException("No empty constructor found");
        }
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            T instance = constructor.newInstance();

            instance.setFile(file);
            instance.setConfigLoader(this);

            saveConfig(instance);

            return instance;

        } catch (Exception exception) {
            LogUtils.error("Can't save " + type.getName(), exception);
            throw new RuntimeException("Cannot load " + type.getName(), exception);
        }
    }

    private <T> T createInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasEmptyConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            return Modifier.isPublic(constructor.getModifiers());
        } catch (Exception exception) {
            return false;
        }
    }


    public <T> T load(File file, Class<T> type) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> map = yaml.load(reader);

            String json = gson.toJson(map);

            return gson.fromJson(json, type);
        }
    }

    public JsonElement loadJson(@NotNull File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> map = yaml.load(reader);
            String json = gson.toJson(map);
            return gson.fromJson(json, JsonElement.class);
        }
    }
}
