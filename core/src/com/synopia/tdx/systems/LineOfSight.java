package com.synopia.tdx.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.synopia.tdx.BlockPosition;

import javax.inject.Inject;

/**
 * Created by synopia on 09.01.2015.
 */
public class LineOfSight extends EntitySystem{
    private BlockPosition current;
    @Inject
    private MapSystem map;

    public boolean inSight(BlockPosition one, BlockPosition two) {
        current = one;
        int x0 = one.getX();
        int y0 = one.getY();
        int x1 = two.getX();
        int y1 = two.getY();
        int dy = y1 - y0;
        int dx = x1 - x0;
        int sx;
        int sy;
        int f = 0;
        if (dy < 0) {
            dy = -dy;
            sy = -1;
        } else {
            sy = 1;
        }
        if (dx < 0) {
            dx = -dx;
            sx = -1;
        } else {
            sx = 1;
        }
        if (dx > dy) {
            while (x0 != x1) {
                f += dy;
                if (f > dx) {
                    if (isBlocked(x0, y0, sx, sy)) {
                        return false;
                    }
                    y0 += sy;
                    f -= dx;
                }
                if (f != 0 && isBlocked(x0, y0, sx, sy)) {
                    return false;
                }
                if (dy == 0 && isBlocked(x0, y0, sx, 1)) {
                    return false;
                }
                x0 += sx;
            }
        } else {
            while (y0 != y1) {
                f += dx;
                if (f > dy) {
                    if (isBlocked(x0, y0, sx, sy)) {
                        return false;
                    }
                    x0 += sx;
                    f -= dy;
                }
                if (f != 0 && isBlocked(x0, y0, sx, sy)) {
                    return false;
                }
                if (dx == 0 && isBlocked(x0, y0, 1, sy)) {
                    return false;
                }
                y0 += sy;
            }
        }
        return true;
    }

    private boolean isBlocked(int x0, int y0, int sx, int sy) {
        int x = x0 + ((sx - 1) / 2);
        int y = y0 + ((sy - 1) / 2);
        return !map.isPassable(x,y);
    }
}
