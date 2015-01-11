package com.synopia.tdx.gson;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by synopia on 07.01.2015.
 */
public class InheritanceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private Map<String, Class> typeMap;

    public InheritanceAdapter(Map<String, Class> typeMap) {
        this.typeMap = typeMap;
    }

    public InheritanceAdapter(Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Supply pairs of <String,Class>!");
        }
        typeMap = Maps.newHashMap();
        for (int i = 0; i < args.length / 2; i++) {
            String name = (String) args[i * 2];
            Class type = (Class) args[i * 2 + 1];
            typeMap.put(name, type);
        }
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String typeName = object.getAsJsonPrimitive("type").getAsString();
        try {
            Class<?> type;
            if (typeMap == null) {
                type = Class.forName(typeName);
            } else {
                type = typeMap.get(typeName);
            }
            return context.deserialize(json, type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = context.serialize(src).getAsJsonObject();
        String typeName = null;
        if (typeMap == null) {
            typeName = src.getClass().getCanonicalName();
        } else {
            for (Map.Entry<String, Class> entry : typeMap.entrySet()) {
                if (entry.getValue() == src.getClass()) {
                    typeName = entry.getKey();
                    break;
                }
            }
        }
        result.addProperty("type", typeName);
        return result;
    }
}
