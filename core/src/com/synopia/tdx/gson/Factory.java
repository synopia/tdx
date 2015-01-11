package com.synopia.tdx.gson;

import com.google.gson.JsonElement;

import java.lang.reflect.Type;

/**
 * Created by synopia on 09.01.2015.
 */
public class Factory<T> {
    private JsonElement json;
    private Type type;
    private GsonWorld world;

    public Factory(GsonWorld world, JsonElement json, Type type) {
        this.world = world;
        this.json = json;
        this.type = type;
    }

    public T create() {
        return world.createInstance(type, json);
    }
}
