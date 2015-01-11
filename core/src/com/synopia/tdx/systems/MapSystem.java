package com.synopia.tdx.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.synopia.core.pathfinder.AStar;
import com.synopia.core.pathfinder.BitMap;
import com.synopia.tdx.BlockPosition;
import com.synopia.tdx.World;
import com.synopia.tdx.components.BlockComponent;
import com.synopia.tdx.components.MapComponent;
import com.synopia.tdx.components.TransformComponent;
import com.synopia.tdx.components.WaypointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by synopia on 07.01.2015.
 */
public class MapSystem extends EntitySystem {
    @Inject
    private ComponentMapper<MapComponent> mm;
    @Inject
    private ComponentMapper<BlockComponent> bm;
    @Inject
    private Engine engine;
    @Inject
    private World world;

    @Inject
    private Random random;
    private BitMap bitMap;
    private MapComponent map;
    private Entity mapEntity;
    private AStar pathfinder;

    private Map<String, List<Entity>> waypoints = Maps.newHashMap();

    private Logger logger = LoggerFactory.getLogger(MapSystem.class);

    public MapSystem() {
        super();
        logger.info("MapSystem started");
    }

    @Override
    public void update(float deltaTime) {
        if( mapEntity==null ) {
            mapEntity = engine.getEntitiesFor(Family.getFor(MapComponent.class)).first();
            map = mm.get(mapEntity);
        }
        if( mapEntity!=null ) {
            if (map.map == null) {
                initialize();
            }
        }

    }

    private void initialize() {
        int height = map.data.size();
        int width = map.data.get(0).length();

        map.map = new Entity[width*map.mapScale*height*map.mapScale];

        map.width = width * map.mapScale;
        map.height = height * map.mapScale;

        logger.info("loading map ({}x{})", map.width, map.height);

        Map<String, WaypointComponent> wpComponents = Maps.newHashMap();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int sx = 0; sx < map.mapScale; sx++) {
                    for (int sy = 0; sy < map.mapScale; sy++) {
                        char ch = map.data.get(y).charAt(x);
                        String blockId = Character.toString(ch);
                        Entity block = world.createEntity(blockId);

                        if( block==null ) {
                            block = world.createEntity(" ");
                            WaypointComponent waypoint = wpComponents.get(blockId);
                            if( waypoint==null ) {
                                waypoint = new WaypointComponent();
                                waypoint.name = blockId;
                                wpComponents.put(blockId, waypoint);
                                waypoints.put(blockId, Lists.newArrayList());
                            }
                            waypoints.get(blockId).add(block);
                            block.add(waypoint);
                        }

                        TransformComponent transform = new TransformComponent();
                        block.add(transform);
                        transform.pos.set(x * map.mapScale + sx, y * map.mapScale+sy, 1);

                        map.map[map.offset(x * map.mapScale+ sx, y * map.mapScale+ sy)] = block;
                    }
                }
            }
        }

        bitMap = new BitMap(map.width, map.height) {
            @Override
            public boolean isPassable(int offset) {
                return bm.get(map.map[offset]).passable;
            }
        };

        logger.info("done loading map, found {} waypoints", wpComponents.size());

        pathfinder = new AStar(bitMap);


    }

    public Entity getWaypoint(String name) {
        List<Entity> candidates = waypoints.get(name);
        if( candidates==null ) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    public List<BlockPosition> findPath(int sx, int sy, int tx, int ty) {
        if( pathfinder!=null ) {
            boolean found = pathfinder.run(bitMap.offset(tx, ty), bitMap.offset(sx, sy));
            if (found) {
                List<BlockPosition> path = Lists.newArrayList();
                for (Integer offset : pathfinder.getPath()) {
                    path.add(new BlockPosition(bitMap.getX(offset), bitMap.getY(offset)));
                }
                return path;
            }
        }
        return null;
    }

    public Entity getBlock(int x, int y){
        return map.map[map.offset(x,y)];
    }

    public boolean isPassable(int x, int y){
        Entity block = getBlock(x, y);
        if( block!=null ) {
            return bm.get(block).passable;
        }
        return false;
    }

}

