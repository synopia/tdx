package com.synopia.tdx.components.damage;

import com.synopia.core.behavior.BaseAction;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 11.01.2015.
 */
public class Delay extends BaseAction<EntityActor> {
    private float duration;

    @Override
    public void construct(EntityActor actor) {
        actor.setValue(getId(), duration);
    }

    @Override
    public boolean prune(EntityActor actor) {
        return false;
    }

    @Override
    public BehaviorState modify(EntityActor actor, BehaviorState result) {
        float time = actor.getValue(getId());
        time -= actor.getDelta();
        actor.setValue(getId(), time);
        return time <= 0 ? BehaviorState.SUCCESS : BehaviorState.RUNNING;
    }

    @Override
    public void destruct(EntityActor actor) {

    }
}
