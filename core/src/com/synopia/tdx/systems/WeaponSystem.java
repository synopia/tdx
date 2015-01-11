package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.synopia.tdx.World;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.components.FireTargetComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.components.WeaponComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by synopia on 07.01.2015.
 */
public class WeaponSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<WeaponComponent> wm;
    @Inject
    private ComponentMapper<HealthComponent> hm;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private ComponentMapper<FireTargetComponent> tarM;
    @Inject
    private Engine engine;
    @Inject
    private World world;
    @Inject
    private Logger logger = LoggerFactory.getLogger(WeaponSystem.class);

    public WeaponSystem() {
        super(Family.getFor(WeaponComponent.class, TransformComponent.class));
        logger.info("WeaponSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WeaponComponent weapon = wm.get(entity);
        TransformComponent transform = tm.get(entity);

        if( weapon.target!=null ) {
            if( isValidTarget(weapon, weapon.target, transform.pos)==null ) {
                weapon.target = null;
            }
        }
        if( weapon.target==null ) {
            weapon.target = findTargetNearest(weapon, transform.pos);
            if (weapon.target != null) {
                logger.info("{} found target {}", entity, weapon.target);
            }
        }
        if( weapon.target!=null && weapon.elapsedTime>=weapon.cooldown ) {
            logger.info("{} fires to {}", entity, weapon.target);
            weapon.elapsedTime -= weapon.cooldown;
            if( weapon.elapsedTime>weapon.cooldown ) {
                weapon.elapsedTime = 0;
            }
            Entity projectile = new Entity();
            projectile.add(weapon.projectile);
            TransformComponent transformComponent = new TransformComponent();
            transformComponent.pos.set(transform.pos);
            transformComponent.rotation = transform.rotation;
            transformComponent.scale.set(transform.scale);
            projectile.add(transformComponent);
            FireTargetComponent target = new FireTargetComponent();
            target.target = weapon.target;
            projectile.add(target);
            engine.addEntity(projectile);
        }
        weapon.elapsedTime += deltaTime;
    }

    private Float isValidTarget(WeaponComponent weapon, Entity target, Vector3 pos) {
        Vector3 dist = tm.get(target).pos.cpy().sub(pos);
        float len = dist.len2();
        if( hm.get(target).hitPoints > 0 && len <=weapon.range*weapon.range ) {
            return len;
        }
        return null;
    }

    private Entity findTargetNearest(WeaponComponent weapon, Vector3 pos) {
        float nearestDist = weapon.range * weapon.range;
        Entity nearest = null;
        ImmutableArray<Entity> targets = engine.getEntitiesFor(Family.getFor(TransformComponent.class, HealthComponent.class));
        for (int i = 0; i < targets.size(); i++) {
            Entity entity = targets.get(i);
            Float dist = isValidTarget(weapon, entity, pos);
            if (dist!=null && dist< nearestDist) {
                nearestDist = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

}
