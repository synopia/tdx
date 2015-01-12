package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.synopia.tdx.Benchmark;
import com.synopia.tdx.BlockPosition;
import com.synopia.tdx.World;
import com.synopia.tdx.components.MovementComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by synopia on 08.01.2015.
 */
public class MovementSystem extends IteratingSystem {
    public static final float REACHED_DIST = 0.1f;
    private Logger logger = LoggerFactory.getLogger(MovementSystem.class);
    @Inject
    private ComponentMapper<MovementComponent> mm;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    private Vector2 accel;
    private Vector2 pos;
    private final Vector2 temp = new Vector2();
    private final Vector2 dist = new Vector2();
    @Inject
    private MouseSystem mouseSystem;
    @Inject
    private MapSystem mapSystem;
    @Inject
    private LineOfSight losSystem;
    @Inject
    private World world;

    public MovementSystem() {
        super(Family.getFor(MovementComponent.class, TransformComponent.class));
        accel = new Vector2();
        pos = new Vector2();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MovementComponent movement = mm.get(entity);
        TransformComponent transform = tm.get(entity);

        if (movement.target == null) {
            return;
        }
        TransformComponent targetTransform = tm.get(movement.target);

        if (movement.type == MovementComponent.Type.DIRECT) {
            processDirect(movement, transform, targetTransform.pos.x, targetTransform.pos.y, deltaTime);
        } else if (movement.type == MovementComponent.Type.PATH) {
            if (movement.path == null) {
                Vector3 targetPos = targetTransform.pos;
                movement.path = Benchmark.bench(() -> mapSystem.findPath((int) transform.pos.x, (int) transform.pos.y, (int) targetPos.x, (int) targetPos.y));
                logger.debug("{} requested path, took {} ms", entity, Benchmark.lastTimeMs);
                movement.pathPosition = 0;
                movement.targetReached = true;
                if (movement.path != null) {
                    BlockPosition target = movement.path.get(0);
                    movement.target = mapSystem.getBlock(target.getX(), target.getY());

                }
            }
            targetTransform = tm.get(movement.target);
            if (movement.path != null && movement.pathPosition < movement.path.size()) {
                processDirect(movement, transform, targetTransform.pos.x, targetTransform.pos.y, deltaTime);
                if (movement.targetReached) {
                    if (movement.pathPosition < movement.path.size() - 1) {
                        movement.pathPosition = getNextPathStep(movement.path, movement.pathPosition);
                        BlockPosition target = movement.path.get(movement.pathPosition);
                        movement.target = mapSystem.getBlock(target.getX(), target.getY());
                        movement.targetReached = false;
                    } else {
                        movement.path = null;
                    }
                }
            }
        }
    }

    private int getNextPathStep(List<BlockPosition> path, int currentPos) {
        BlockPosition current = path.get(currentPos);
        while (currentPos < path.size() - 1 && losSystem.inSight(current, path.get(currentPos + 1))) {
            currentPos++;
        }
        return currentPos;
    }

    private void processDirect(MovementComponent movement, TransformComponent transform, float targetX, float targetY, float deltaTime) {
        dist.set(targetX - transform.pos.x, targetY - transform.pos.y);
        float len = dist.len();
        if (len < REACHED_DIST) {
            movement.targetReached = true;
            movement.speed.set(0, 0);
        } else {
            movement.targetReached = false;
            float currSpeed = movement.speed.len();
            float timeToTarget = len / currSpeed;
            if (currSpeed == 0) {
                timeToTarget = 10000;
            }
            float a = currSpeed * currSpeed / len;

            if (a > movement.accel) {
                // s = a/2 * t*t + v*t -> a = 2*(s-v*t)/t/t;
                temp.set(movement.speed).scl(timeToTarget);
                accel.set(dist).sub(temp).scl(2.f / timeToTarget / timeToTarget);
                movement.breaking = true;
            } else {
                accel.set(dist);
                movement.breaking = false;
            }
            accel.clamp(-movement.accel, movement.accel);
            // v = a*t
            movement.speed.mulAdd(accel, deltaTime);
            movement.speed.clamp(0, movement.maxSpeed);

            // s = v*t
            pos.set(transform.pos.x, transform.pos.y);
            pos.mulAdd(movement.speed, deltaTime);

            transform.rotation = movement.speed.angleRad();

            transform.pos.x = pos.x;
            transform.pos.y = pos.y;
        }
    }
}
