package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.ComponentMapper;
import com.synopia.core.behavior.Action;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.tdx.EntityActor;
import com.synopia.tdx.components.MovementComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by synopia on 09.01.2015.
 */
public class Slowdown implements Action<EntityActor> {
    public float factor;
    public float duration;

    private ComponentMapper<MovementComponent> mc = ComponentMapper.getFor(MovementComponent.class);
    private Logger logger = LoggerFactory.getLogger(Slowdown.class);
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
        mc.get(actor.getEntity()).maxSpeed *= factor;
        logger.debug("{} slowed down by {}", actor.getEntity(), factor);
    }

    @Override
    public boolean prune(EntityActor actor) {
        return false;
    }

    @Override
    public BehaviorState modify(EntityActor actor, BehaviorState result) {
        return BehaviorState.SUCCESS;
    }

    @Override
    public void destruct(EntityActor actor) {
        logger.debug("{} slowdown finished", actor.getEntity());
        mc.get(actor.getEntity()).maxSpeed /= factor;

    }
}
