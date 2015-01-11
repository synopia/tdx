package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by synopia on 06.01.2015.
 */
public class TransformComponent extends Component {
    public final Vector3 pos = new Vector3();
    public final Vector2 scale = new Vector2(1f, 1f);
    public float rotation;
}
