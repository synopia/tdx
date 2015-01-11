package com.synopia.core.behavior;

/**
 * Created by synopia on 11.01.2015.
 */
public interface BehaviorNode extends AssemblingBehaviorNode {
    void insertChild(int index, BehaviorNode child);

    void replaceChild(int index, BehaviorNode child);

    BehaviorNode removeChild(int index);

    BehaviorNode getChild(int index);

    int getChildrenCount();

    int getMaxChildren();

    void construct();

    BehaviorState execute();

    void destruct();
}
