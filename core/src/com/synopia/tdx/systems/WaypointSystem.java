package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.synopia.tdx.components.MoveAlongComponent;
import com.synopia.tdx.components.MovementComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by synopia on 08.01.2015.
 */
public class WaypointSystem extends IteratingSystem {
    @Inject
    private ComponentMapper<MovementComponent> mm;
    @Inject
    private ComponentMapper<MoveAlongComponent> am;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private MapSystem mapSystem;
    private Logger logger = LoggerFactory.getLogger(WaypointSystem.class);

    public WaypointSystem() {
        super(Family.getFor(MoveAlongComponent.class));
        logger.info("WaypointSystem started");
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MoveAlongComponent moveAlong = am.get(entity);
        if (moveAlong.finished) {
            return;
        }
        MovementComponent movement = mm.get(entity);
        if (movement.target != null) {
            if (movement.targetReached) {
                movement.target = null;
                moveAlong.current = findNextWaypoint(moveAlong.waypoints, moveAlong.current);
                moveAlong.finished = moveAlong.current == null;
                logger.debug("{} reached waypoint. Next waypoint is {}", entity, moveAlong.current);
            }
        } else {
            movement.target = mapSystem.getWaypoint(moveAlong.current);
            if (movement.target != null) {
                logger.debug("Waypoint {} assigned to {}", moveAlong.current, entity);
                movement.targetReached = false;
            }
        }
    }


    private String findNextWaypoint(List<String> waypoints, String current) {
        if (current == null) {
            return waypoints.get(0);
        }
        int index = waypoints.indexOf(current) + 1;
        if (index < waypoints.size()) {
            return waypoints.get(index);
        }
        return null;
    }
}
