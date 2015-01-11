package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.synopia.tdx.World;
import com.synopia.tdx.components.ParticleComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by synopia on 09.01.2015.
 */
public class ParticleSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<ParticleComponent> pm;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private World world;
    @Inject
    private SpriteBatch spriteBatch;
    @Inject
    private RenderingSystem renderingSystem;
    private Matrix4 matrix4;
    private Logger logger = LoggerFactory.getLogger(ParticleSystem.class);

    public ParticleSystem() {
        super(Family.getFor(ParticleComponent.class, TransformComponent.class));
        matrix4 = new Matrix4();
        logger.info("ParticleSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParticleComponent particle = pm.get(entity);
        if (particle.effect == null) {
            particle.effect = world.getParticleEffect(particle.name);
            particle.effect.start();
        }
        if (particle.effect != null) {
            TransformComponent transform = tm.get(entity);

            matrix4.idt();
            matrix4.translate(transform.pos.x, transform.pos.y, -2);
            matrix4.scale(1.f / 46f, 1.f / 46f, 1);
            spriteBatch.setTransformMatrix(matrix4);

            particle.effect.draw(spriteBatch, deltaTime);

            if (particle.effect.isComplete()) {
                particle.isCompleted = true;
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        spriteBatch.begin();
        super.update(deltaTime);
        spriteBatch.end();
    }
}
