package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.synopia.tdx.BlockPosition;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by synopia on 07.01.2015.
 */
public class MouseSystem extends EntitySystem {
    @Inject
    private RenderingSystem renderingSystem;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private InputMultiplexer inputMultiplexer;

    private int lastMouseX;
    private int lastMouseY;
    private BlockPosition lastMouse;
    private Signal<BlockPosition> blockPositionChanged;
    private Signal<BlockPosition> blockPositionClicked;
    private Logger logger = LoggerFactory.getLogger(MouseSystem.class);
    private Entity mouse;

    public MouseSystem() {
        blockPositionChanged = new Signal<>();
        blockPositionClicked = new Signal<>();

        logger.info("MouseSystem started");
    }


    @Override
    public void addedToEngine(Engine engine) {
        mouse = new Entity();
        mouse.add(new TransformComponent());
        engine.addEntity(mouse);

        inputMultiplexer.addProcessor(new InputAdapter() {
            int button;

            @Override
            public boolean scrolled(int amount) {
                renderingSystem.getCamera().zoom += amount / 20.f;
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                mouseMoved(screenX, screenY);
                if (button == Input.Buttons.LEFT) {
                    blockPositionClicked.dispatch(lastMouse);
                    return true;
                }
                this.button = button;
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (button == Input.Buttons.RIGHT) {
                    renderingSystem.getCamera().translate(-(screenX - lastMouseX) / 5.f, (screenY - lastMouseY) / 5.f);
                }
                mouseMoved(screenX, screenY);
                return button == Input.Buttons.RIGHT;
            }

            @Override
            public boolean mouseMoved(int x, int y) {
                Vector3 position = renderingSystem.getCamera().unproject(new Vector3(x, y, 0));
                tm.get(mouse).pos.set(position);
                BlockPosition currMouse = new BlockPosition((int) position.x, (int) position.y);
                if (!currMouse.equals(lastMouse)) {
                    blockPositionChanged.dispatch(currMouse);
                    lastMouse = currMouse;
                }
                lastMouseX = x;
                lastMouseY = y;
                return true;
            }
        });
    }

    public Signal<BlockPosition> getBlockPositionChanged() {
        return blockPositionChanged;
    }

    public Signal<BlockPosition> getBlockPositionClicked() {
        return blockPositionClicked;
    }

    public Entity getMouse() {
        return mouse;
    }

    public BlockPosition getMouseBlock() {
        return lastMouse;
    }
}
