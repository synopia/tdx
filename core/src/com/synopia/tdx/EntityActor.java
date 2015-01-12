package com.synopia.tdx;

import com.badlogic.ashley.core.Entity;
import com.google.common.collect.Maps;
import com.synopia.core.behavior.Actor;
import com.synopia.core.behavior.DefaultActor;

import java.util.Map;

/**
 * Created by synopia on 11.01.2015.
 */
public class EntityActor extends DefaultActor<Entity> {
    private final World world;
    private final Entity entity;

    public EntityActor(World world, Entity entity) {
        this.world = world;
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public World getWorld() {
        return world;
    }
}
