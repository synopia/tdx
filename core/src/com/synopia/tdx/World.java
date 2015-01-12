package com.synopia.tdx;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.collect.Maps;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.synopia.core.behavior.BehaviorNode;
import com.synopia.core.behavior.BehaviorTreeBuilder;
import com.synopia.tdx.components.BulletComponent;
import com.synopia.tdx.components.HealthComponent;
import com.synopia.tdx.components.LaserComponent;
import com.synopia.tdx.components.ProjectileComponent;
import com.synopia.tdx.components.RocketComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.components.damage.Aoe;
import com.synopia.tdx.components.damage.Damage;
import com.synopia.tdx.components.damage.Delay;
import com.synopia.tdx.components.damage.Slowdown;
import com.synopia.tdx.gson.GsonWorld;
import com.synopia.tdx.gson.InheritanceAdapter;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.Map;

/**
 * Created by synopia on 07.01.2015.
 */
public class World extends GsonWorld {
    private Map<String, Map<String, Double>> damageMatrix;
    private TextureAtlas atlas;
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    private final Vector3 dist = new Vector3();

    public World(Injector injector, Reflections reflections) {
        super(injector, reflections);
        registerGsonAdapter(TextureRegion.class, new TypeAdapter<TextureRegion>() {
            @Override
            public void write(JsonWriter out, TextureRegion value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public TextureRegion read(JsonReader in) throws IOException {
                return getTexture(in.nextString());
            }
        });

        registerGsonAdapter(ProjectileComponent.class, new InheritanceAdapter<>("rocket", RocketComponent.class, "bullet", BulletComponent.class, "laser", LaserComponent.class));

        BehaviorTreeBuilder behaviorTreeBuilder = new BehaviorTreeBuilder();
        behaviorTreeBuilder.registerAction("direct", Damage.class);
        behaviorTreeBuilder.registerAction("slowdown", Slowdown.class);
        behaviorTreeBuilder.registerAction("delay", Delay.class);
        behaviorTreeBuilder.registerAction("aoe", Aoe.class);
        registerGsonAdapter(BehaviorNode.class, behaviorTreeBuilder);
    }

    public void load() {
        atlas = new TextureAtlas(Gdx.files.internal("tiles.txt"));

        load(Gdx.files.internal("damage.json").read());
        load(Gdx.files.internal("towers.json").read());
        load(Gdx.files.internal("map.json").read());
        load(Gdx.files.internal("units.json").read());
        load(Gdx.files.internal("waves.json").read());

        damageMatrix = createInstance(new TypeToken<Map<String, Map<String, Double>>>() {
        }.getType(), "damage");
    }

    public void loadMap(String name) {
        createEntity(name);
    }

    public TextureRegion getTexture(String name) {
        return atlas.findRegion(name);
    }

    public ParticleEffect getParticleEffect(String name) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal("explosion.p"), atlas);
        return effect;
    }

    public Map<Entity, Float> findEntities(Vector3 pos, float range) {
        ImmutableArray<Entity> targets = engine.getEntitiesFor(Family.getFor(TransformComponent.class, HealthComponent.class));
        Map<Entity, Float> result = Maps.newHashMap();
        for (int i = 0; i < targets.size(); i++) {
            Entity target = targets.get(i);
            dist.set(tm.get(target).pos);
            dist.sub(pos);
            float v = dist.len();
            if (v < range) {
                result.put(target, v);
            }
        }
        return result;
    }
}
