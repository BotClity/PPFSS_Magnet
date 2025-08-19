// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.adapter;

import com.google.gson.*;
import org.bukkit.Material;

import java.lang.reflect.Type;

public class MaterialAdapter implements JsonSerializer<Material>, JsonDeserializer<Material> {

    @Override
    public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()){
            return null;
        }

        return Material.getMaterial(json.getAsJsonPrimitive().getAsString().toUpperCase());
    }

    @Override
    public JsonElement serialize(Material src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }
}
