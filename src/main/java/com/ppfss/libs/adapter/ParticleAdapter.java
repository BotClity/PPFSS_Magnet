// PPFSS_Libs Plugin
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.libs.adapter;

import com.google.gson.*;
import org.bukkit.Particle;

import java.lang.reflect.Type;

public class ParticleAdapter implements JsonSerializer<Particle>, JsonDeserializer<Particle> {
    @Override
    public Particle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            return null;
        }
        return Particle.valueOf(json.getAsString());
    }

    @Override
    public JsonElement serialize(Particle src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name());
    }
}
