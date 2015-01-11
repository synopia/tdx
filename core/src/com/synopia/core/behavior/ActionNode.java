package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.ClassGenerator;
import com.synopia.core.behavior.compiler.MethodGenerator;

/**
 * Created by synopia on 11.01.2015.
 */
public class ActionNode implements BehaviorNode {
    protected Action action;

    public ActionNode() {
    }

    public ActionNode(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public void construct() {
        if (action != null) {
            action.construct(null);
        }
    }

    @Override
    public BehaviorState execute() {
        if (action != null) {
            return action.modify(null, BehaviorState.UNDEFINED);
        }
        return BehaviorState.UNDEFINED;
    }

    @Override
    public void destruct() {
        if (action != null) {
            action.destruct(null);
        }
    }


    @Override
    public void insertChild(int index, BehaviorNode child) {
        throw new IllegalArgumentException("ActionNodes cant have any children");
    }

    @Override
    public void replaceChild(int index, BehaviorNode child) {
        throw new IllegalArgumentException("ActionNodes cant have any children");
    }

    @Override
    public BehaviorNode removeChild(int index) {
        throw new IllegalArgumentException("ActionNodes cant have any children");
    }

    @Override
    public BehaviorNode getChild(int index) {
        throw new IllegalArgumentException("ActionNodes cant have any children");
    }

    @Override
    public int getChildrenCount() {
        return 0;
    }

    @Override
    public int getMaxChildren() {
        return 0;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void assembleSetup(ClassGenerator gen) {

    }

    @Override
    public void assembleTeardown(ClassGenerator gen) {

    }

    @Override
    public void assembleConstruct(MethodGenerator gen) {
        gen.invokeAction(action.getId(), "void construct(com.synopia.core.behavior.Actor)");
    }

    @Override
    public void assembleExecute(MethodGenerator gen) {
        gen.invokeAction(action.getId(), "com.synopia.core.behavior.BehaviorState modify(com.synopia.core.behavior.Actor, com.synopia.core.behavior.BehaviorState)", BehaviorState.RUNNING);
    }

    @Override
    public void assembleDestruct(MethodGenerator gen) {
        gen.invokeAction(action.getId(), "void destruct(com.synopia.core.behavior.Actor)");
    }
}
