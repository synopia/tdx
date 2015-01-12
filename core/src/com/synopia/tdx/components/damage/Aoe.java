package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.synopia.core.behavior.BaseAction;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.tdx.EntityActor;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by synopia on 12.01.2015.
 */
public class Aoe extends BaseAction<EntityActor> {
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private List<Float> ranges;
    private List<Float> factors;
    public float baseDamage;
    public String damageType;
    private Logger logger = LoggerFactory.getLogger(Aoe.class);

    @Override
    public void construct(EntityActor actor) {
        Map<Entity, Float> entities = actor.getWorld().findEntities(actor.getEntity().getComponent(TransformComponent.class).pos, ranges.get(0));
        for (Map.Entry<Entity, Float> entry : entities.entrySet()) {
            Entity target = entry.getKey();
            Float dist = entry.getValue();
            float finalDamage = getFactor(dist) * baseDamage;
            HealthComponent health = hm.get(target);
            health.hitPoints -= finalDamage;

            logger.debug("{} aoe damaged inflicted to {}", finalDamage, target);
        }
    }

    private float getFactor(float range) {
        for (int i = 0; i < ranges.size(); i++) {
            if (range <= ranges.get(i)) {
                return factors.get(i);
            }
        }
        return 0;
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

    }
}
