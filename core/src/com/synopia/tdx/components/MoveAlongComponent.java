package com.synopia.tdx.components;

import com.badlogic.ashley.core.Component;

import java.util.List;

/**
 * Created by synopia on 08.01.2015.
 */
public class MoveAlongComponent extends Component {
    public List<String> waypoints;
    public String current;
    public boolean finished;
}
