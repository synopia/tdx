package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.synopia.tdx.components.MovementComponent;
import com.synopia.tdx.systems.EffectSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by synopia on 09.01.2015.
 */
public class Slowdown implements Effect {
    public float factor;
    public float duration;

    private ComponentMapper<MovementComponent> mc = ComponentMapper.getFor(MovementComponent.class);
    private float time;
    private Logger logger = LoggerFactory.getLogger(Slowdown.class);

    @Override
    public void bind(EffectSystem effectSystem, Entity target) {
        mc.get(target).maxSpeed *= factor;
        logger.debug("{} slowed down by {}", target, factor);
    }

    @Override
    public void unbind(EffectSystem effectSystem, Entity target) {
        logger.debug("{} slowdown over", target);
        mc.get(target).maxSpeed /= factor;
    }

    @Override
    public boolean update(EffectSystem effectSystem, Entity target, float dt) {
        time += dt;
        return time<duration;
    }
}
