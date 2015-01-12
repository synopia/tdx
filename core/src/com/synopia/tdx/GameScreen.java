package com.synopia.tdx;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synopia.tdx.components.NameComponent;
import com.synopia.tdx.components.PlugComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.systems.MouseSystem;
import com.synopia.tdx.systems.PlugSystem;
import org.nustaq.offheap.FSTBinaryOffheapMap;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.nustaq.serialization.serializers.FSTArrayListSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by synopia on 06.01.2015.
 */
public class GameScreen extends ScreenAdapter {
    private TDGame game;
    private World world;
    private final Stage stage;
    private List<Record> recorder;
    private Entity wave;

    public GameScreen(TDGame game) {
        this.game = game;

        world = new World(game.injector, game.reflections);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
        stage = new Stage();

        inputMultiplexer.addProcessor(stage);
        game.injector.add(InputMultiplexer.class, inputMultiplexer);


        game.injector.add(Random.class, new Random());
        game.injector.add(Engine.class, world.getEngine());
        game.injector.add(World.class, world);
        game.injector.add(Skin.class, new Skin(Gdx.files.internal("uiskin.json")));

        world.registerSystems();
        world.registerComponents();

        world.load();
        world.loadMap("metroid");
        createHUD();

        world.createEntity("guard_tower");
        world.createEntity("frost_tower");
        world.createEntity("lightning_tower");
        world.createEntity("speed_caster");
        world.createEntity("hell_caster");


        try {
            FSTObjectInput input = new FSTObjectInput(new FileInputStream("test.bin"));
            recorder = (List<Record>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            recorder = Lists.newArrayList();
        }

        game.injector.get(PlugSystem.class).getEntityPlaced().add((signal, pos) -> {
            recorder.add(new Record(Gdx.graphics.getFrameId(), game.injector.get(PlugSystem.class).getActiveEntity().getComponent(NameComponent.class).internalName, pos));
        });


    }

    @Override
    public void render(float delta) {
        world.getEngine().update(delta);
        stage.act(delta);
        stage.draw();

        if (wave == null) {
            wave = world.createEntity("startWave");
            for (Record record : recorder) {
                Entity entity = game.injector.get(World.class).createEntity(record.entity);
                game.injector.get(PlugSystem.class).plug(record.pos, 1, 1, entity);
                entity.remove(PlugComponent.class);
                TransformComponent transform = new TransformComponent();
                entity.add(transform);
                transform.pos.set(record.pos.getX(), record.pos.getY(), -1);
            }
        }

    }

    public static class Record implements Serializable {
        public long frameId;
        public String entity;
        public BlockPosition pos;

        public Record(long frameId, String entity, BlockPosition pos) {
            this.frameId = frameId;
            this.entity = entity;
            this.pos = pos;
        }
    }

    private void createHUD() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.top().add(game.injector.get(PlugSystem.class).getPalette());
        TextButton actor = new TextButton("Save", game.injector.get(Skin.class));
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    FileOutputStream record = new FileOutputStream("test.bin");
                    FSTObjectOutput out = new FSTObjectOutput(record);
                    out.writeObject(recorder);
                    out.flush();
                    record.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        table.bottom().add(actor);
    }
}
