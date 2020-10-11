package com.dca.checkers.model;

public interface State extends Cloneable {

    /**
     * Returns the value of the state.
     * A value of 0 means the goal has been reached
     * @param evalForP1 tells if current state must be evaluated for player 1 (true) or player 2 (false)
     * @return current state value.
     **/
    double value(boolean evalForP1);
}