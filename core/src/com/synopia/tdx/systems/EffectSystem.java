package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.google.common.collect.Maps;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.core.behavior.BehaviorTree;
import com.synopia.core.behavior.DefaultBehaviorTree;
import com.synopia.core.behavior.compiler.Assembler;
import com.synopia.tdx.EntityActor;
import com.synopia.tdx.World;
import com.synopia.tdx.components.ActorComponent;
import com.synopia.tdx.components.EffectComponent;
import com.synopia.tdx.components.FireTargetComponent;
import com.synopia.tdx.components.HealthComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
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
    private ComponentMapper<ActorComponent> am;
    @Inject
    private Random random;
    @Inject
    private Engine engine;
    @Inject
    private World world;
    private Map<BehaviorNode, Assembler> trees = Maps.newHashMap();

    private Logger logger = LoggerFactory.getLogger(EffectSystem.class);

    public EffectSystem() {
        super(Family.getFor(EffectComponent.class, FireTargetComponent.class));
        logger.info("EffectSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EffectComponent effectComponent = em.get(entity);
        Entity target = fm.get(entity).target;

        if (effectComponent.treeAssembler == null) {
            Assembler asm = trees.get(effectComponent.start);
            if (asm == null) {
                asm = new Assembler("Test" + trees.size(), effectComponent.start);
                trees.put(effectComponent.start, asm);
                logger.debug("Effect {} constructed a behavior tree for {}", entity, target);
            }
            effectComponent.treeAssembler = asm;
        }

        ActorComponent actorComponent = am.get(entity);
        if (actorComponent == null) {
            actorComponent = new ActorComponent();
        }
        if (actorComponent.actor == null) {
            EntityActor actor = new EntityActor(world, target);
//            actorComponent.tree = effectComponent.treeAssembler.createInstance();
//            actorComponent.tree.bind(effectComponent.start);
//            actorComponent.tree.setActor(actor);
            actorComponent.tree = new DefaultBehaviorTree(effectComponent.start, actor);
            actorComponent.actor = actor;
            entity.add(actorComponent);
            logger.debug("Effect {} create for {}", entity, target);
        }

        actorComponent.actor.setDelta(deltaTime);

        BehaviorState result = actorComponent.tree.step();

        if (result != BehaviorState.RUNNING || hm.get(target).hitPoints <= 0) {
            logger.debug("Effect {} finished", entity);
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
