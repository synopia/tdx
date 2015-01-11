package com.synopia.core.behavior;

/**
 * Created by synopia on 11.01.2015.
 */
public class DecoratorNode extends ActionNode {
    private BehaviorNode child;

    @Override
    public void construct() {
        if (action != null) {
            action.construct(null);
        }
        child.construct();
    }

    @Override
    public BehaviorState execute() {
        if (action != null) {
            if (action.prune(null)) {
                return action.modify(null, BehaviorState.UNDEFINED);
            }
            return action.modify(null, child.execute());
        }

        return child.execute();
    }

    @Override
    public void destruct() {
        if (action != null) {
            action.destruct(null);
        }
        child.destruct();
    }

    @Override
    public void insertChild(int index, BehaviorNode child) {
        replaceChild(index, child);
    }

    @Override
    public void replaceChild(int index, BehaviorNode child) {
        if (index != 0) {
            throw new IllegalArgumentException("Decorator accepts only one child!");
        }
        this.child = child;
    }

    @Override
    public BehaviorNode removeChild(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Decorator accepts only one child!");
        }
        return child;
    }

    @Override
    public BehaviorNode getChild(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Decorator accepts only one child!");
        }
        return child;
    }

    @Override
    public int getChildrenCount() {
        return child != null ? 1 : 0;
    }

    @Override
    public int getMaxChildren() {
        return 1;
    }
}
