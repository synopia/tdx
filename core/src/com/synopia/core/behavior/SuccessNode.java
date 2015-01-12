package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.MethodGenerator;

/**
 * Created by synopia on 11.01.2015.
 */
public class SuccessNode extends LeafNode {
    @Override
    public BehaviorNode deepCopy() {
        return new SuccessNode();
    }

    @Override
    public void construct(Actor actor) {

    }

    @Override
    public BehaviorState execute(Actor actor) {
        return BehaviorState.SUCCESS;
    }

    @Override
    public void destruct(Actor actor) {

    }

    @Override
    public void assembleExecute(MethodGenerator gen) {
        gen.push(BehaviorState.SUCCESS.ordinal());
    }

}
