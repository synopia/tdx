package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.BehaviorTree;
import com.synopia.core.behavior.compiler.CompiledBehaviorTree;
import com.synopia.tdx.EntityActor;

/**
 * Created by synopia on 11.01.2015.
 */
public class ActorComponent extends Component {
    public EntityActor actor;
    public CompiledBehaviorTree tree;
    public BehaviorTree nodeTree;
}
