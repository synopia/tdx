package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.ComponentMapper;
import com.synopia.core.behavior.BaseAction;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.tdx.EntityActor;
import com.synopia.tdx.components.HealthComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by synopia on 07.01.2015.
 */
public class Damage extends BaseAction<EntityActor> {
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    public String diceDamage;
    public float baseDamage;
    public String damageType;
    private Logger logger = LoggerFactory.getLogger(Damage.class);

    @Override
    public void construct(EntityActor actor) {
        float finalDamage = baseDamage;//+ effectSystem.rollDice(diceDamage);

        HealthComponent health = hm.get(actor.getEntity());
        health.hitPoints -= finalDamage;

        logger.debug("{} damaged inflicted to {}", finalDamage, actor);
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
