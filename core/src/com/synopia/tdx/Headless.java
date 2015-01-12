package com.synopia.tdx;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.synopia.tdx.components.PlugComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.systems.MapSystem;
import com.synopia.tdx.systems.MouseSystem;
import com.synopia.tdx.systems.ParticleSystem;
import com.synopia.tdx.systems.PlugSystem;
import com.synopia.tdx.systems.RenderingSystem;
import org.nustaq.serialization.FSTObjectInput;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by synopia on 12.01.2015.
 */
public class Headless {
    public static void main(String[] args) {
        Reflections reflections;
        Injector injector;
        reflections = new Reflections();
        injector = new Injector();
        World world = new World(injector, reflections) {
            @Override
            public void load() {

                load(Headless.class.getResourceAsStream("/damage.json"));
                load(Headless.class.getResourceAsStream("/towers.json"));
                load(Headless.class.getResourceAsStream("/map.json"));
                load(Headless.class.getResourceAsStream("/units.json"));
                load(Headless.class.getResourceAsStream("/waves.json"));

//                damageMatrix = createInstance(new TypeToken<Map<String, Map<String, Double>>>() {
//                }.getType(), "damage");
            }

            @Override
            public TextureRegion getTexture(String name) {
                return null;
            }
        };

        injector.add(Random.class, new Random());
        injector.add(Engine.class, world.getEngine());
        injector.add(World.class, world);

        world.registerSystems(Arrays.asList(RenderingSystem.class, MouseSystem.class, ParticleSystem.class));
        world.registerComponents();

        world.load();
        world.loadMap("metroid");

        world.createEntity("guard_tower");
        world.createEntity("frost_tower");
        world.createEntity("lightning_tower");
        world.createEntity("speed_caster");
        world.createEntity("hell_caster");

        injector.get(MapSystem.class).update(0.1f);
        injector.get(PlugSystem.class).update(0.1f);
        try {
            FSTObjectInput input = new FSTObjectInput(new FileInputStream("test.bin"));
            List<GameScreen.Record> recorder = (List<GameScreen.Record>) input.readObject();
            for (GameScreen.Record record : recorder) {
                Entity entity = injector.get(World.class).createEntity(record.entity);
                injector.get(PlugSystem.class).plug(record.pos, 1, 1, entity);
                entity.remove(PlugComponent.class);
                TransformComponent transform = new TransformComponent();
                entity.add(transform);
                transform.pos.set(record.pos.getX(), record.pos.getY(), -1);
            }

            for (int i = 0; i < 60 * 60 * 1; i++) {
                if (i % (60 * 30) == 0) {
                    Entity wave = world.createEntity("startWave");
                }
                world.getEngine().update(1.f / 60.f);
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
