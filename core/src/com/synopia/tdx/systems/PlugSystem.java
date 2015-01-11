package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.common.collect.Lists;
import com.synopia.tdx.BlockPosition;
import com.synopia.tdx.World;
import com.synopia.tdx.components.MapComponent;
import com.synopia.tdx.components.NameComponent;
import com.synopia.tdx.components.PlugComponent;
import com.synopia.tdx.components.SlotComponent;
import com.synopia.tdx.components.TransformComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by synopia on 09.01.2015.
 */
public class PlugSystem extends IteratingSystem{
    @Inject
    private ComponentMapper<PlugComponent> pm;
    @Inject
    private ComponentMapper<SlotComponent> sm;
    @Inject
    private MouseSystem mouseSystem;
    @Inject
    private Engine engine;
    @Inject
    private ComponentMapper<TransformComponent> tm;
    @Inject
    private ComponentMapper<MapComponent> mm;
    @Inject
    private ComponentMapper<NameComponent> nm;
    @Inject
    private World world;
    @Inject
    private Skin skin;

    private Logger logger = LoggerFactory.getLogger(PlugSystem.class);
    private Entity activeEntity;
    private MapComponent map;
    private Signal<BlockPosition> entityPlaced;
    private List<Entity> plugs = Lists.newArrayList();
    private List<Entity> lastPlugs = Lists.newArrayList();
    private HorizontalGroup palette;

    public PlugSystem() {
        super(Family.getFor(PlugComponent.class, NameComponent.class));

        entityPlaced = new Signal<>();
        logger.info("PlugSystem started");
        palette = new HorizontalGroup();
    }

    @Override
    public void update(float deltaTime) {
        if( map==null ) {
            Entity mapEntity = engine.getEntitiesFor(Family.getFor(MapComponent.class)).get(0);
            map = mm.get(mapEntity);
            mouseSystem.getBlockPositionChanged().add((signal, pos) -> {
                if (activeEntity != null ) {
                    TransformComponent transform;
                    if( !tm.has(activeEntity) ) {
                        transform = new TransformComponent();
                        activeEntity.add(transform);
                    } else {
                        transform = tm.get(activeEntity);
                    }
                    transform.pos.set(pos.getX(), pos.getY() , -1);

                    PlugComponent plugComponent = pm.get(activeEntity);
                    int sx = (int) plugComponent.size.x;
                    int sy = (int) plugComponent.size.y;

                    plugComponent.plugable = isPlugable(pos, sx, sy);
                }
            });
            mouseSystem.getBlockPositionClicked().add((signal, pos) -> {
                if (activeEntity != null) {
                    PlugComponent plugComponent = pm.get(activeEntity);
                    int sx = (int) plugComponent.size.x;
                    int sy = (int) plugComponent.size.y;
                    if (plug(mouseSystem.getMouseBlock(), sx, sy, activeEntity)) {
                        logger.info("Entity {} placed at {},{}", activeEntity, sx, sy);
                        activeEntity.remove(PlugComponent.class);
                        activeEntity = world.createEntity(nm.get(activeEntity).internalName);
                        entityPlaced.dispatch(pos);
                        mouseSystem.getBlockPositionChanged().dispatch(pos);
                    }
                }
            });
        }
        plugs.clear();
        super.update(deltaTime);

        if( !plugs.equals(lastPlugs)) {
            palette.clear();
            for (Entity plug : plugs) {
                TextButton button = new TextButton(nm.get(plug).internalName, skin);
                palette.addActor(button);

                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        setActiveEntity(plug);
                        mouseSystem.getBlockPositionChanged().dispatch(mouseSystem.getMouseBlock());
                    }
                });
            }
            lastPlugs = Lists.newArrayList(plugs);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        plugs.add(entity);
    }

    private boolean plug(BlockPosition pos, int sx, int sy, Entity plug) {
        if( isPlugable(pos, sx, sy) ) {
            for (int y = 0; y < sy; y++) {
                for (int x = 0; x < sx; x++) {
                    Entity block = map.getBlockEntity(pos.getX() + x, pos.getY() + y);
                    sm.get(block).plugged = plug;
                }
            }
            pm.get(plug).plugged = true;
            return true;
        }
        return false;
    }

    private boolean isPlugable(BlockPosition pos, int sx, int sy) {
        if( map==null ) {
            return false;
        }
        for (int y = 0; y < sy; y++) {
            for (int x = 0; x < sx; x++) {
                Entity block = map.getBlockEntity(pos.getX()+x, pos.getY()+y);
                if( block==null ) {
                    return false;
                }

                SlotComponent slot = block.getComponent(SlotComponent.class);
                if( slot==null ) {
                    return false;
                }

                if( slot.plugged!=null ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setActiveEntity(Entity activeEntity) {
        this.activeEntity = activeEntity;
    }

    public Signal<BlockPosition> getEntityPlaced() {
        return entityPlaced;
    }

    public HorizontalGroup getPalette() {
        return palette;
    }
}

