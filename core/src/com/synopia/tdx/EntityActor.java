package com.synopia.tdx;

import com.badlogic.ashley.core.Entity;
import com.google.common.collect.Maps;
import com.synopia.core.behavior.Actor;

import java.util.Map;

/**
 * Created by synopia on 11.01.2015.
 */
public class EntityActor implements Actor<Entity> {
    private final Map<Integer, Object> dataMap = Maps.newHashMap();
    private final Entity entity;
    private float delta;

    public EntityActor(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public <T> T getValue(int id) {
        return (T) dataMap.get(id);
    }

    public void setValue(int id, Object obj) {
        dataMap.put(id, obj);
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }
}
