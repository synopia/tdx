package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.synopia.tdx.World;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.components.TransformComponent;

import javax.inject.Inject;

/**
 * Created by synopia on 09.01.2015.
 */
public class HealthSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private ComponentMapper<HealthComponent> hm;
    @Inject
    private SpriteBatch spriteBatch;
    @Inject
    private World world;
    @Inject
    private Engine engine;

    private Matrix4 matrix4;

    public HealthSystem() {
        super(Family.getFor(HealthComponent.class, TransformComponent.class));
        matrix4 = new Matrix4();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureRegion tex = world.getTexture("healthbarback");
        TransformComponent t = tm.get(entity);
        HealthComponent health = hm.get(entity);
        float width = tex.getRegionWidth();
        float height = tex.getRegionHeight();
        float originX = width * 0.5f;
        float originY = height * 0.5f;

        spriteBatch.draw(tex, t.pos.x - originX, t.pos.y - originY + 0.5f, originX, originY, width, height, t.scale.x * RenderingSystem.PIXELS_TO_METERS, t.scale.y * RenderingSystem.PIXELS_TO_METERS, 0);
        tex = world.getTexture("healthbarfront");
        width *= health.hitPoints / health.maxHitPoints;
        spriteBatch.draw(tex, t.pos.x - originX, t.pos.y - originY + 0.5f, originX, originY, width, height, t.scale.x * RenderingSystem.PIXELS_TO_METERS, t.scale.y * RenderingSystem.PIXELS_TO_METERS, 0);

        if (health.hitPoints <= 0) {
            engine.removeEntity(entity);
        }
    }

    @Override
    public void update(float deltaTime) {
        spriteBatch.begin();
        spriteBatch.setTransformMatrix(matrix4);
        super.update(deltaTime);
        spriteBatch.end();
    }
}
