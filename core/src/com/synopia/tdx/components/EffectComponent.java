package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.compiler.CompiledBehaviorTree;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 07.01.2015.
 */
public class EffectComponent extends Component {
    public BehaviorNode start;
    public boolean started;
    public CompiledBehaviorTree tree;
    public EntityActor actor;

}
