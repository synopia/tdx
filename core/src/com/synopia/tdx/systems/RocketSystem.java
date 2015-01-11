package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.synopia.tdx.World;
import com.synopia.tdx.components.FireTargetComponent;
import com.synopia.tdx.components.MovementComponent;
import com.synopia.tdx.components.ParticleComponent;
import com.synopia.tdx.components.RocketComponent;
import com.synopia.tdx.components.TextureComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by synopia on 08.01.2015.
 */
public class RocketSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<RocketComponent> rm;
    @Inject
    private ComponentMapper<ParticleComponent> pm;
    @Inject
    private ComponentMapper<TextureComponent> texM;
    @Inject
    private ComponentMapper<MovementComponent> mm;
    @Inject
    private ComponentMapper<FireTargetComponent> tarM;
    @Inject
    private ComponentMapper<TransformComponent> transM;
    @Inject
    private World world;
    @Inject
    private Engine engine;

    private Logger logger = LoggerFactory.getLogger(RocketSystem.class);

    public RocketSystem() {
        super(Family.getFor(RocketComponent.class, TransformComponent.class));
        logger.info("RocketSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (texM.get(entity) == null) {
            TextureComponent texture = new TextureComponent();
            texture.region = world.getTexture("rocket");
            entity.add(texture);
        }
        RocketComponent rocket = rm.get(entity);

        MovementComponent movement = mm.get(entity);
        if (movement == null) {
            movement = new MovementComponent();
            movement.accel = rocket.accel;
            movement.maxSpeed = rocket.maxSpeed;
            movement.type = MovementComponent.Type.DIRECT;
            movement.target = tarM.get(entity).target;
            entity.add(movement);
        } else {
            if (movement.targetReached) {
                ParticleComponent particle = pm.get(entity);
                if (particle == null) {
                    logger.info("Rocket hit {}", movement.target);
                    particle = new ParticleComponent();
                    particle.name = rocket.particle.name;
                    entity.add(particle);
                    Entity effectEntity = new Entity();
                    engine.addEntity(effectEntity);
                    effectEntity.add(rocket.effect.create());
                    effectEntity.add(tarM.get(entity));
                    effectEntity.add(transM.get(entity));
                } else if (particle.isCompleted) {
                    engine.removeEntity(entity);
                }
            }
        }
    }
}
