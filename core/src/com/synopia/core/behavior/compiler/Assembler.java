package com.synopia.core.behavior.compiler;

import com.synopia.core.behavior.AssemblingBehaviorNode;
import com.synopia.core.behavior.BehaviorState;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

/**
 * Created by synopia on 11.01.2015.
 */
public class Assembler {

    private final String className;
    private final ClassGenerator generator;
    private final ClassWriter classWriter;

    public Assembler(String className) {
        this.className = className;
        classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        generator = new ClassGenerator(classWriter, className);

        generator.generateMethod("void <init>()", (gen) -> {
            gen.loadThis();
            gen.invokeConstructor(Type.getType(CompiledBehaviorTree.class), Method.getMethod("void <init>()"));
            gen.returnValue();
        });
    }

    public byte[] getBytecode() {
        return classWriter.toByteArray();
    }

    public CompiledBehaviorTree createInstance() {
        MyClassLoader loader = new MyClassLoader(this.getClass().getClassLoader());
        Class type = loader.defineClass(className, getBytecode());
        try {
            return (CompiledBehaviorTree) type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateMethod(AssemblingBehaviorNode node) {
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
