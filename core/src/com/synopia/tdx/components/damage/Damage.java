package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.systems.EffectSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by synopia on 07.01.2015.
 */
public class Damage implements Effect{
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    public float baseDamage;
    public String diceDamage;
    public String damageType;
    private Logger logger = LoggerFactory.getLogger(Damage.class);

    @Override
    public boolean update(EffectSystem effectSystem, Entity target, float dt) {
        float finalDamage = baseDamage + effectSystem.rollDice(diceDamage);

        HealthComponent health = hm.get(target);
        health.hitPoints -= finalDamage;

        logger.debug("{} damaged inflicted to {}", finalDamage, target);
        return false;
    }

    @Override
    public void bind(EffectSystem effectSystem, Entity target) {
    }

    @Override
    public void unbind(EffectSystem effectSystem, Entity target) {
    }
}
