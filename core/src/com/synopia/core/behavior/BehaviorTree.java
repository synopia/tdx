package com.synopia.core.behavior;

/**
 * Created by synopia on 12.01.2015.
 */
public interface BehaviorTree {
    BehaviorState step();

    void setActor(Actor actor);

    Actor getActor();
}
