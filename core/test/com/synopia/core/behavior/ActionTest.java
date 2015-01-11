package com.synopia.core.behavior;

import com.google.gson.GsonBuilder;
import com.synopia.core.behavior.compiler.Assembler;
import com.synopia.core.behavior.compiler.CompiledBehaviorTree;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by synopia on 11.01.2015.
 */
public class ActionTest {
    public static class MyActor implements Actor {
        public float current;

    }

    public static class Delay implements Action<MyActor> {
        private float time;
        private int id;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void construct(MyActor actor) {

        }

        @Override
        public boolean prune(MyActor actor) {
            return false;
        }

        @Override
        public BehaviorState modify(MyActor actor, BehaviorState result) {
            if (actor.current > time) {
                return BehaviorState.SUCCESS;
            }
            actor.current += 1.f / 60.f;
            return BehaviorState.RUNNING;
        }

        @Override
        public void destruct(MyActor actor) {

        }
    }

    public static class MyAction implements Action {
        private int id;

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void construct(Actor actor) {
            System.out.println("construct called");
        }

        @Override
        public boolean prune(Actor actor) {
            return false;
        }

        @Override
        public BehaviorState modify(Actor actor, BehaviorState result) {
            System.out.println(actor + " execute " + result);
            return BehaviorState.SUCCESS;
        }

        @Override
        public void destruct(Actor actor) {
            System.out.println("destruct called");

        }
    }

    @Test
    public void testIt() throws IOException, InterruptedException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        BehaviorTreeBuilder builder = new BehaviorTreeBuilder();
        gsonBuilder.registerTypeAdapter(BehaviorNode.class, builder);
        builder.registerAction("x", MyAction.class);
        builder.registerAction("delay", Delay.class);
        BehaviorNode node = gsonBuilder.create().fromJson("{ sequence: [{delay:{time:1 }}, {x:{}}]}", BehaviorNode.class);

        Assembler asm = new Assembler("Test");
        asm.generateMethod(node);
        CompiledBehaviorTree instance = asm.createInstance();
        instance.setActor(new MyActor());
        System.out.println(instance.getActor());
        instance.bind(node);
        for (int i = 0; i < 100; i++) {
            instance.step();
            Thread.sleep(1000 / 60);
        }
    }
}
