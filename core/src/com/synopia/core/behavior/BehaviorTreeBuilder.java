package com.synopia.core.behavior;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by synopia on 11.01.2015.
 */
public class BehaviorTreeBuilder implements JsonDeserializer<BehaviorNode> {
    private Map<String, Class<? extends Action>> actions = Maps.newHashMap();
    private int nextId = 1;

    public void registerAction(String name, Class<? extends Action> action) {
        actions.put(name, action);
    }

    @Override
    public BehaviorNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BehaviorNode node;
        if (json.isJsonPrimitive()) {
            node = getLeafNode(json);
        } else {
            node = getCompositeNode(json, context);
        }
        return createNode(node);
    }

    public BehaviorNode createNode(BehaviorNode node) {
        return node;
    }

    private BehaviorNode getCompositeNode(JsonElement json, JsonDeserializationContext context) {
        String type;
        JsonObject obj = json.getAsJsonObject();
        Map.Entry<String, JsonElement> entry = obj.entrySet().iterator().next();
        type = entry.getKey();
        json = entry.getValue();

        CompositeNode compositeNode = null;
        ActionNode actionNode = null;
        switch (type) {
            case "sequence":
                compositeNode = new SequenceNode();
                break;
            case "selector":
                compositeNode = new SelectorNode();
                break;
            case "dynamic":
                compositeNode = new DynamicSelectorNode();
                break;
            case "parallel":
                compositeNode = new ParallelNode();
                break;
            default:
                if (actions.containsKey(type)) {
                    actionNode = new ActionNode();
                    Action action = context.deserialize(json, actions.get(type));
                    if (action.getId() == 0) {
                        action.setId(nextId);
                        nextId++;
                    }
                    actionNode.setAction(action);
                } else {
                    throw new IllegalArgumentException("Unknown behavior node type " + type);
                }
        }
        if (compositeNode != null) {
            List<BehaviorNode> children = context.deserialize(json, new TypeToken<List<BehaviorNode>>() {
            }.getType());
            compositeNode.children.addAll(children);
            return compositeNode;
        } else {
            return actionNode;
        }
    }

    private BehaviorNode getLeafNode(JsonElement json) {
        String type;
        type = json.getAsString();
        switch (type) {
            case "failure":
                return new FailureNode();
            case "success":
                return new SuccessNode();
            case "running":
                return new RunningNode();
            default:
                throw new IllegalArgumentException("Unknown behavior node type " + type);
        }
    }

}
