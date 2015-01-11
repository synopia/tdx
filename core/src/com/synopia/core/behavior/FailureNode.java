package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.MethodGenerator;

/**
 * Created by synopia on 11.01.2015.
 */
public class FailureNode extends LeafNode {
    @Override
    public void construct() {

    }

    @Override
    public BehaviorState execute() {
        return BehaviorState.FAILURE;
    }

    @Override
    public void destruct() {

    }

    @Override
    public void assembleExecute(MethodGenerator gen) {
        gen.push(BehaviorState.FAILURE.ordinal());
    }
}
