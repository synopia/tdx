package com.synopia.core.behavior;

import com.synopia.core.behavior.compiler.ClassGenerator;
import com.synopia.core.behavior.compiler.MethodGenerator;

/**
 * Created by synopia on 11.01.2015.
 */
public interface AssemblingBehaviorNode {
    void assembleSetup(ClassGenerator gen);

    void assembleTeardown(ClassGenerator gen);

    void assembleConstruct(MethodGenerator gen);

    void assembleExecute(MethodGenerator gen);

    void assembleDestruct(MethodGenerator gen);
}
