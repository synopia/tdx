package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.Entity;
import com.synopia.tdx.systems.EffectSystem;

import java.util.Iterator;
import java.util.List;

/**
 * Created by synopia on 10.01.2015.
 */
public class Parallel implements Effect {
    public List<Effect> effects;

    @Override
    public void bind(EffectSystem effectSystem, Entity target) {
        for (Effect effect : effects) {
            effect.bind(effectSystem, target);
        }
    }

    @Override
    public boolean update(EffectSystem effectSystem, Entity target, float dt) {
        Iterator<Effect> it = effects.iterator();
        boolean active = false;
        while (it.hasNext()) {
            Effect next = it.next();
            boolean keep = next.update(effectSystem, target, dt);
            if( !keep ) {
                next.unbind(effectSystem, target);
                it.remove();
            } else {
                active = true;
            }
        }
        return active;
    }

    @Override
    public void unbind(EffectSystem effectSystem, Entity target) {
        for (Effect effect : effects) {
            effect.unbind(effectSystem, target);
        }
    }
}
