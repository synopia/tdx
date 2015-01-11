package com.synopia.core.behavior;

/**
 * Created by synopia on 11.01.2015.
 */
public enum BehaviorState {
    UNDEFINED(false),
    FAILURE(true),
    SUCCESS(true),
    RUNNING(false);

    private boolean finished;

    BehaviorState(boolean finished) {
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }
}
