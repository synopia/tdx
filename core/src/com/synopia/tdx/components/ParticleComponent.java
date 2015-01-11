package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Created by synopia on 09.01.2015.
 */
public class ParticleComponent extends Component {
    public String name;
    public transient ParticleEffect effect;
    public transient boolean isCompleted;
}
