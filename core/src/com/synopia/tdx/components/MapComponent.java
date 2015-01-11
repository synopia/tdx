package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by synopia on 07.01.2015.
 */
public class MapComponent extends Component {
    public List<String> data;
    public int mapScale;
    public Entity[] map;
    public int width;
    public int height;

    public int offset(int x, int y ) {
        return y*width+x;
    }

    public Entity getBlockEntity(int x, int y ) {
        if( map==null || x<0 || y<0 || x>=width || y>=height ) {
            return null;
        }
        return map[offset(x,y)];
    }
}
