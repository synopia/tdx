package com.synopia.core.behavior;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.synopia.core.behavior.compiler.Assembler;
import com.synopia.core.behavior.compiler.CompiledBehaviorTree;
import org.junit.Assert;
import org.junit.Before;

import java.util.List;

/**
 * Created by synopia on 11.01.2015.
 */
public class CountTest {
    private List<Integer> constructCalled = Lists.newArrayList();
    private List<Integer> destructCalled = Lists.newArrayList();
    private List<Integer> executeCalled = Lists.newArrayList();
    private GsonBuilder gsonBuilder;
    public int nextId;

    private class CountDelegate extends DelegateNode {
        private int id;

        public CountDelegate(BehaviorNode delegate) {
            super(delegate);
            id = nextId;
            nextId++;
        }

        @Override
        public BehaviorState execute(Actor actor) {
            executeCalled.add(id);
            return super.execute(actor);
        }

        @Override
        public void construct(Actor actor) {
            super.construct(actor);
            constructCalled.add(id);
        }

        @Override
        public void destruct(Actor actor) {
            destructCalled.add(id);
            super.destruct(actor);
        }
    }

    @Before
    public void init() {
        constructCalled.clear();
        destructCalled.clear();
        executeCalled.clear();
        nextId = 1;

        gsonBuilder = new GsonBuilder();
        BehaviorTreeBuilder builder = new BehaviorTreeBuilder() {
            @Override
            public BehaviorNode createNode(BehaviorNode node) {
                return new CountDelegate(node);
            }
        };
        gsonBuilder.registerTypeAdapter(BehaviorNode.class, builder);
//        gsonBuilder.registerTypeAdapter(Action.class, new InheritanceAdapter<Action>("delay", Delay.class));
    }

    public void assertBT(String tree, List<BehaviorState> result, List<Integer> executed) {
        assertBT(tree, result, executed, false);
        constructCalled.clear();
        destructCalled.clear();
        executeCalled.clear();
        nextId = 1;
        assertBT(tree, result, executed, true);
    }

    public void assertBT(String tree, List<BehaviorState> result, List<Integer> executed, boolean step) {
        BehaviorNode node = fromJson(tree);
        Assembler asm = new Assembler("Test");
        asm.generateMethod(node);
        CompiledBehaviorTree cbt = asm.createInstance();

        node.construct(null);
        List<BehaviorState> actualStates = Lists.newArrayList();
        List<BehaviorState> cbtStates = Lists.newArrayList();
        for (int i = 0; i < result.size(); i++) {
            BehaviorState state = node.execute(null);
            actualStates.add(state);
            cbtStates.add(step ? cbt.step() : BehaviorState.values()[cbt.run(0)]);
        }
        node.destruct(null);

        Assert.assertEquals(result, actualStates);
        Assert.assertEquals(result, cbtStates);
        Assert.assertEquals(executed, executeCalled);
    }

    public BehaviorNode fromJson(String json) {
        return gsonBuilder.create().fromJson(json, BehaviorNode.class);
    }
}
