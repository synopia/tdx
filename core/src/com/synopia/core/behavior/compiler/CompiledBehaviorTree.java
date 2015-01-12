package com.synopia.core.behavior.compiler;

import com.google.common.collect.Maps;
import com.synopia.core.behavior.Action;
import com.synopia.core.behavior.ActionNode;
import com.synopia.core.behavior.Actor;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.BehaviorState;
import com.synopia.core.behavior.BehaviorTree;
import com.synopia.core.behavior.CompositeNode;

import java.util.Map;

/**
 * Created by synopia on 11.01.2015.
 */
public abstract class CompiledBehaviorTree implements BehaviorTree {
    private Map<Integer, Action> actionMap = Maps.newHashMap();
    private BehaviorState result = BehaviorState.UNDEFINED;
    public Actor actor;

    @Override
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public Actor getActor() {
        return actor;
    }

    public void bind(BehaviorNode node) {
        if (node instanceof ActionNode) {
            ActionNode actionNode = (ActionNode) node;
            actionMap.put(actionNode.getAction().getId(), actionNode.getAction());
        } else if (node instanceof CompositeNode) {
            CompositeNode compositeNode = (CompositeNode) node;
            for (BehaviorNode behaviorNode : compositeNode.getChildren()) {
                bind(behaviorNode);
            }
        }
    }

    public abstract int run(int state);

    @Override
    public BehaviorState step() {
        result = BehaviorState.values()[run(result.ordinal())];
        return result;
    }

    public void setAction(int id, Action action) {
        actionMap.put(id, action);
    }

    public Action getAction(int id) {
        return actionMap.get(id);
    }

    public Map<Integer, Action> getActionMap() {
        return actionMap;
    }
}
