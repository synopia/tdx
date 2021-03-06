package com.synopia.core.behavior.compiler;

import com.synopia.core.behavior.Actor;
import com.synopia.core.behavior.AssemblingBehaviorNode;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.BehaviorState;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by synopia on 11.01.2015.
 */
public class Assembler {

    private final String className;
    private final ClassGenerator generator;
    private final ClassWriter classWriter;
    private MyClassLoader loader;
    private Class type;
    private BehaviorNode node;

    public Assembler(String className, BehaviorNode node) {
        this.className = className;
        this.node = node;
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        generator = new ClassGenerator(classWriter, className);

        generator.generateMethod("void <init>()", (gen) -> {
            gen.loadThis();
            gen.invokeConstructor(Type.getType(CompiledBehaviorTree.class), Method.getMethod("void <init>()"));
            gen.returnValue();
        });

        generateMethod(node);
    }

    public byte[] getBytecode() {
        return classWriter.toByteArray();
    }

    public CompiledBehaviorTree createInstance(Actor actor) {
        if (loader == null) {
            loader = new MyClassLoader(this.getClass().getClassLoader());
            type = loader.defineClass(className, getBytecode());
        }
        try {
            CompiledBehaviorTree tree = (CompiledBehaviorTree) type.newInstance();
            tree.bind(node);
            tree.setActor(actor);
            return tree;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void generateMethod(AssemblingBehaviorNode node) {
        node.assembleSetup(generator);
        generator.generateMethod("int run(int)", (gen) -> {
            gen.loadArg(0);
            gen.push(BehaviorState.RUNNING.ordinal());
            Label skip = gen.newLabel();
            gen.ifICmp(GeneratorAdapter.EQ, skip);
            node.assembleConstruct(gen);
            gen.mark(skip);

            node.assembleExecute(gen);

            gen.dup();
            gen.push(BehaviorState.RUNNING.ordinal());
            skip = gen.newLabel();
            gen.ifICmp(GeneratorAdapter.EQ, skip);
            node.assembleDestruct(gen);

            gen.mark(skip);
            gen.returnValue();
        });
        node.assembleTeardown(generator);
    }

    private static class MyClassLoader extends ClassLoader {
        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class defineClass(String name, byte[] byteCode) {
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}
