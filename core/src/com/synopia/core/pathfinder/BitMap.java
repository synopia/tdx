package com.synopia.core.pathfinder;

import java.util.List;

/**
 * @author synopia
 */
public abstract class BitMap {
    public static final float SQRT_2 = (float) Math.sqrt(2);
    private int width;
    private int height;

    public BitMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int offset(int x, int y) {
        return x + y * width();
    }

    public abstract boolean isPassable(int offset);

    public void getSuccessors(int offset, List<Integer> successors) {
        int x = getX(offset);
        int y = getY(offset);
        if (y > 0 && isPassable(offset - width())) {
            successors.add(offset - width());
        }
        if (x < width() - 1 && isPassable(offset + 1)) {
            successors.add(offset + 1);
        }
        if (y < height() - 1 && isPassable(offset + width())) {
            successors.add(offset + width());
        }
        if (x > 0 && isPassable(offset - 1)) {
            successors.add(offset - 1);
        }

//        if (x < width() - 1 && y > 0 && isPassable(offset + 1 - width())) {
//            successors.add(offset + 1 - width());
//        }
//        if (x < width() - 1 && y < height() - 1 && isPassable(offset + 1 + width())) {
//            successors.add(offset + 1 + width());
//        }
//        if (x > 0 && y < height() - 1 && isPassable(offset - 1 + width())) {
//            successors.add(offset - 1 + width());
//        }
//        if (x > 0 && y > 0 && isPassable(offset - 1 - width())) {
//            successors.add(offset - 1 - width());
//        }
    }

    public int getNumberOfNodes() {
        return width() * height();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public float exactDistance(int from, int to) {
        int diff = to - from;
        if (diff == -width() || diff == 1 || diff == width() || diff == -1) {
            return 1;
        }
        if (diff == -width() + 1 || diff == width() + 1 || diff == width() - 1 || diff == -width() - 1) {
            return SQRT_2;
        }
        return 0;
    }

    public float fastDistance(int from, int to) {
        int fromX = getX(from);
        int fromY = getY(from);
        int toX = getX(to);
        int toY = getY(to);

        return (float) Math.abs(fromX - toX) + Math.abs(fromY - toY);
//        return (float) Math.sqrt( (fromX-toX)*(fromX-toX)+ (fromY-toY)*(fromY-toY));

    }

    public int getY(int id) {
        return id / width();
    }

    public int getX(int id) {
        return id % width();
    }
}
