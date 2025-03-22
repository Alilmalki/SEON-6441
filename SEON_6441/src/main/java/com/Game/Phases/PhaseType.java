package com.Game.Phases;

/**
 * Enum representing the different phases of the game.
 * Each phase dictates the current state of gameplay and determines available actions.
 */
public enum PhaseType {
    /**
     * The Startup phase where the game initializes and players are set up.
     */
    STARTUP,

    /**
     * The Issue Order phase where players create and assign orders.
     */
    ISSUE_ORDER,

    /**
     * The Order Execution phase where orders are executed sequentially.
     */
    ORDER_EXECUTION
}
