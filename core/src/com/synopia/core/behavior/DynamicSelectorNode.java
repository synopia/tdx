package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.ClassGenerator;
import com.synopia.core.behavior.compiler.MethodGenerator;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.BitSet;

import static org.objectweb.asm.commons.GeneratorAdapter.EQ;

/**
 * Created by synopia on 11.01.2015.
 */
public class DynamicSelectorNode extends CompositeNode {
    private BitSet constructed;
    private String[] byteFields;

    @Override
    public void construct() {
        constructed = new BitSet(children.size());
    }

    @Override
    public BehaviorState execute() {
        BehaviorState result;
        for (int i = 0; i < children.size(); i++) {
            BehaviorNode child = children.get(i);
            if (!constructed.get(i)) {
                child.construct();
                constructed.set(i);
            }
            result = child.execute();
            if (result == BehaviorState.RUNNING) {
                return BehaviorState.RUNNING;
            }
            child.destruct();
            constructed.clear(i);
            if (result == BehaviorState.SUCCESS) {
                return BehaviorState.SUCCESS;
            }
        }
        return BehaviorState.FAILURE;
    }

    @Override
    public void destruct() {
    }

    @Override
    public void assembleSetup(ClassGenerator gen) {
        super.assembleSetup(gen);

        int bytes = children.size() / 8;
        if (children.size() % 8 > 0) {
            bytes++;
        }
        byteFields = new String[bytes];
        for (int i = 0; i < bytes; i++) {
            byteFields[i] = gen.generateField(Type.BYTE_TYPE);
        }
    }

    private void loadBitCmp(MethodGenerator gen, int index) {
        int b = index / 8;
        int bit = index % 8;

        gen.loadThis();
        gen.loadField(byteFields[b]);
        gen.push(1 << bit);
        gen.visitInsn(Opcodes.IAND);
        gen.push(1 << bit);
    }

    private void storeBit(MethodGenerator gen, int index) {
        int b = index / 8;
        int bit = index % 8;

        gen.loadThis();
        gen.loadThis();
        gen.loadField(byteFields[b]);
        gen.push(1 << bit);
        gen.visitInsn(Opcodes.IOR);
        gen.storeField(byteFields[b]);
    }

    private void resetBit(MethodGenerator gen, int index) {
        int b = index / 8;
        int bit = index % 8;

        gen.loadThis();
        gen.loadThis();
        gen.loadField(byteFields[b]);
        gen.push(0xff & (~(1 << bit)));
        gen.visitInsn(Opcodes.IAND);
        gen.storeField(byteFields[b]);
    }

    @Override
    public void assembleExecute(MethodGenerator gen) {
        Label exit = gen.newLabel();
        Label exitSuccess = gen.newLabel();

        for (int i = 0; i < children.size(); i++) {
            Label skip = gen.newLabel();
            BehaviorNode child = children.get(i);

            loadBitCmp(gen, i);
            gen.ifICmp(EQ, skip);
            child.assembleConstruct(gen);
            storeBit(gen, i);

            gen.mark(skip);
            child.assembleExecute(gen);

            gen.dup();
            gen.push(BehaviorState.RUNNING.ordinal());
            gen.ifICmp(EQ, exit);

            child.assembleDestruct(gen);
            resetBit(gen, i);

            gen.push(BehaviorState.SUCCESS.ordinal());
            gen.ifICmp(EQ, exitSuccess);
        }

        gen.push(BehaviorState.FAILURE.ordinal());
        gen.goTo(exit);

        gen.mark(exitSuccess);
        gen.push(BehaviorState.SUCCESS.ordinal());

        gen.mark(exit);
    }
}
