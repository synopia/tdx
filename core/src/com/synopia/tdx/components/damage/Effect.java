package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.Entity;
import com.synopia.tdx.systems.EffectSystem;

/**
 * Created by synopia on 09.01.2015.
 */
public interface Effect {
    void bind(EffectSystem effectSystem, Entity target);
    boolean update(EffectSystem effectSystem, Entity target, float dt);
    void unbind(EffectSystem effectSystem, Entity target);
}
