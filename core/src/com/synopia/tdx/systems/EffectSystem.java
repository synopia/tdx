package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.core.behavior.compiler.Assembler;
import com.synopia.tdx.EntityActor;
import com.synopia.tdx.components.EffectComponent;
import com.synopia.tdx.components.FireTargetComponent;
import com.synopia.tdx.components.HealthComponent;
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

        if (effectComponent.tree == null) {
            Assembler asm = new Assembler("Test");
            asm.generateMethod(effectComponent.start);
            effectComponent.tree = asm.createInstance();
            effectComponent.tree.bind(effectComponent.start);

            EntityActor actor = new EntityActor(target);
            effectComponent.tree.setActor(actor);
            effectComponent.actor = actor;
        }

        effectComponent.actor.setDelta(deltaTime);

        BehaviorState result = effectComponent.tree.step();

        if (result != BehaviorState.RUNNING || hm.get(target).hitPoints <= 0) {
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
