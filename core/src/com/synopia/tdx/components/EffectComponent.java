package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.google.gson.JsonElement;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.compiler.Assembler;
import com.synopia.core.behavior.compiler.CompiledBehaviorTree;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 07.01.2015.
 */
public class EffectComponent extends Component {
    public JsonElement test;
    public BehaviorNode start;
    public Assembler treeAssembler;
}
