package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.synopia.tdx.components.EffectComponent;
import com.synopia.tdx.components.damage.Damage;
import com.synopia.tdx.components.FireTargetComponent;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.components.damage.Effect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Random;

/**
 * Created by synopia on 09.01.2015.
 */
public class EffectSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<EffectComponent> em;
    @Inject
    private ComponentMapper<FireTargetComponent> fm;
    @Inject
    private ComponentMapper<HealthComponent> hm;
    @Inject
    private Random random;
    @Inject
    private Engine engine;
    private Logger logger = LoggerFactory.getLogger(EffectSystem.class);

    public EffectSystem() {
        super(Family.getFor(EffectComponent.class, FireTargetComponent.class));
        logger.info("EffectSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EffectComponent effectComponent = em.get(entity);

        Entity target = fm.get(entity).target;
        if( !effectComponent.started ) {
            effectComponent.start.bind(this, target);
        }
        boolean keep = effectComponent.start.update(this, target, deltaTime);

        if( !keep || hm.get(target).hitPoints<=0) {
            effectComponent.start.unbind(this, target);
            engine.removeEntity(entity);
        }
    }


    public int rollDice(String dice) {
        String[] parts = dice.toLowerCase().split("d");
        int numberOfDice = Integer.parseInt(parts[0]);
        int diceSides = Integer.parseInt(parts[1]);
        int result = 0;
        for (int i = 0; i < numberOfDice; i++) {
            result += random.nextInt(diceSides) + 1;
        }
        return result;
    }
}
