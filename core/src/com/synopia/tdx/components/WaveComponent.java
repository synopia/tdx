package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;

import java.util.List;

/**
 * Created by synopia on 09.01.2015.
 */
public class WaveComponent extends Component {
    public List<List<String>> paths;
    public String unit;
    public int count;
    public float duration;
    public float delay;

    public float time;
    public int spawned;
    public boolean spawning;
    public boolean allSpawned;
}
