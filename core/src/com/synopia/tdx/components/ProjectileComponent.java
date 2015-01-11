package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.synopia.tdx.gson.Factory;

/**
 * Created by synopia on 07.01.2015.
 */
public class ProjectileComponent extends Component {
    public Factory<EffectComponent> effect;
    public ParticleComponent particle;
}
