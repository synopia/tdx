package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.synopia.tdx.World;
import com.synopia.tdx.components.MoveAlongComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.components.WaveComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by synopia on 09.01.2015.
 */
public class WaveSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<WaveComponent> wm;
    @Inject
    private World world;
    @Inject
    private MapSystem mapSystem;
    private Logger logger = LoggerFactory.getLogger(WaveSystem.class);

    public WaveSystem() {
        super(Family.getFor(WaveComponent.class));
        logger.info("WaveSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WaveComponent wave = wm.get(entity);
        if (wave.time > wave.delay) {
            if (!wave.spawning) {
                logger.info("Wave {}x {} started", wave.count, wave.unit);
                wave.spawning = true;
            }
            if (wave.spawned < wave.count) {
                int progress = (int) (wave.count * (wave.time - wave.delay) / wave.duration);
                int toSpawn = progress - wave.spawned;
                for (int i = 0; i < toSpawn; i++) {
                    for (List<String> path : wave.paths) {
                        Entity unit = world.createEntity(wave.unit);
                        MoveAlongComponent moveAlong = new MoveAlongComponent();
                        moveAlong.waypoints = path;
                        moveAlong.current = path.get(1);
                        unit.add(moveAlong);
                        TransformComponent transform = new TransformComponent();
                        transform.pos.set(mapSystem.getWaypoint(path.get(0)).getComponent(TransformComponent.class).pos);
                        unit.add(transform);
                    }
                }
                wave.spawned += toSpawn;
            } else {
                if (!wave.allSpawned) {
                    logger.info("{} {} spawned in {} s", wave.spawned, wave.unit, wave.duration);
                    wave.allSpawned = true;
                }
            }
        }
        wave.time += deltaTime;
    }
}
