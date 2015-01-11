package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Created by synopia on 07.01.2015.
 */
public class WeaponComponent extends Component {
    public float range;
    public float cooldown;
    public ProjectileComponent projectile;
    public Entity target;

    public float elapsedTime;
}
