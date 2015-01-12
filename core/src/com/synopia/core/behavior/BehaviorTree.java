package com.synopia.core.behavior;

/**
 * Created by synopia on 12.01.2015.
 */
public class BehaviorTree {
    private BehaviorNode root;
    private BehaviorState state = BehaviorState.UNDEFINED;

    public BehaviorTree(BehaviorNode root) {
        this.root = root;
    }

    public BehaviorState step(Actor actor) {
        if (state != BehaviorState.RUNNING) {
            root.construct(actor);
        }
        state = root.execute(actor);
        if (state != BehaviorState.RUNNING) {
            root.destruct(actor);
        }

        return state;
    }
}
