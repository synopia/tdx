package com.synopia.tdx.components.damage;

import com.badlogic.ashley.core.Entity;
import com.synopia.tdx.systems.EffectSystem;

/**
 * Created by synopia on 09.01.2015.
 */
public class Buff implements Effect {
    public float duration;
    public int ticks;
    public Effect effect;

    private float timeToTick;
    private Effect activeEffect;
    private int currentTick;

    @Override
    public void bind(EffectSystem effectSystem, Entity target) {
    }

    @Override
    public boolean update(EffectSystem effectSystem, Entity target, float dt) {
        if( timeToTick<=0 ) {
            currentTick++;
            if( currentTick>=ticks ) {
                if( activeEffect!=null ) {
                    activeEffect.unbind(effectSystem, target);
                    activeEffect = null;
                }
                return false;
            }
            if( activeEffect==null ) {
                activeEffect = effect;
                activeEffect.bind(effectSystem, target);
            } else {
                activeEffect.unbind(effectSystem, target);
            }
            timeToTick = duration/ticks;
        }
        if( activeEffect!=null ) {
            boolean keep = activeEffect.update(effectSystem, target, dt);
            if( !keep ) {
                activeEffect.unbind(effectSystem, target);
                activeEffect = null;
            }
        }
        timeToTick -= dt;
        return true;
    }

    @Override
    public void unbind(EffectSystem effectSystem, Entity target) {
        effect.unbind(effectSystem, target);
    }
}
