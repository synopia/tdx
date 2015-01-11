package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.SerializedName;
import com.synopia.tdx.BlockPosition;

import java.util.List;

/**
 * Created by synopia on 06.01.2015.
 */
public class MovementComponent extends Component {
    public enum Type {
        @SerializedName("direct")
        DIRECT,
        @SerializedName("path")
        PATH
    }

    public float maxSpeed;
    public float accel;
    public Type type;

    public Entity target;
    public boolean targetReached;
    public final Vector2 speed = new Vector2();
    public boolean breaking;

    public List<BlockPosition> path;
    public int pathPosition;
}
