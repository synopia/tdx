package com.synopia.tdx.components.damage;

import com.synopia.core.behavior.Action;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 11.01.2015.
 */
public class Delay implements Action<EntityActor> {
    private float duration;
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void construct(EntityActor actor) {
        actor.setValue(id, duration);
    }

    @Override
    public boolean prune(EntityActor actor) {
        return false;
    }

    @Override
    public BehaviorState modify(EntityActor actor, BehaviorState result) {
        float time = actor.getValue(id);
        time -= actor.getDelta();
        actor.setValue(id, time);
        return time <= 0 ? BehaviorState.SUCCESS : BehaviorState.RUNNING;
    }

    @Override
    public void destruct(EntityActor actor) {

    }
}
