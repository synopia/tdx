package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.ClassGenerator;
import com.synopia.core.behavior.compiler.MethodGenerator;

/**
 * Created by synopia on 11.01.2015.
 */
public abstract class LeafNode implements BehaviorNode {
    @Override
    public void insertChild(int index, BehaviorNode child) {
        throw new IllegalArgumentException("Leaf nodes does not accept children");
    }

    @Override
    public void replaceChild(int index, BehaviorNode child) {
        throw new IllegalArgumentException("Leaf nodes does not accept children");
    }

    @Override
    public BehaviorNode removeChild(int index) {
        throw new IllegalArgumentException("Leaf nodes does not accept children");
    }

    @Override
    public BehaviorNode getChild(int index) {
        throw new IllegalArgumentException("Leaf nodes does not accept children");
    }

    @Override
    public int getChildrenCount() {
        return 0;
    }

    @Override
    public int getMaxChildren() {
        return 0;
    }

    @Override
    public void assembleSetup(ClassGenerator gen) {

    }

    @Override
    public void assembleTeardown(ClassGenerator gen) {

    }

    @Override
    public void assembleConstruct(MethodGenerator gen) {

    }

    @Override
    public void assembleExecute(MethodGenerator gen) {

    }

    @Override
    public void assembleDestruct(MethodGenerator gen) {

    }

    @Override
    public void construct(Actor actor) {

    }

    @Override
    public BehaviorState execute(Actor actor) {
        return null;
    }

    @Override
    public void destruct(Actor actor) {

    }
}
