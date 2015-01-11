package com.synopia.tdx;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.synopia.tdx.systems.PlugSystem;

import java.util.Random;

/**
 * Created by synopia on 06.01.2015.
 */
public class GameScreen extends ScreenAdapter {
    private TDGame game;
    private World world;
    private final Stage stage;

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
        Entity wave = world.createEntity("startWave");
    }

    private void createHUD() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.top().add(game.injector.get(PlugSystem.class).getPalette());
    }

    @Override
    public void render(float delta) {
        world.getEngine().update(delta);
        stage.act(delta);
        stage.draw();
    }
}
