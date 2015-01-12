package com.synopia.tdx.components.damage;

import com.synopia.core.behavior.Action;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 12.01.2015.
 */
public abstract class BaseAction implements Action<EntityActor> {
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
