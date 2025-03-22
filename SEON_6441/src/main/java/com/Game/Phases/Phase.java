package com.Game.Phases;

import com.Game.model.Player;
import com.Game.controller.GameController;
import com.Game.view.CommandPromptView;

import java.util.List;

/**
 * Abstract class representing a game phase in the State pattern.
 * Different phases of the game will implement this class to provide phase-specific behavior.
 * This serves as the foundation for the State pattern, which will be fully implemented in Build 2.
 */
public abstract class Phase {

    /**
     * Starts the current phase with the provided game controller, players, and command prompt view.
     *
     * @param p_gameController     the game controller handling the game state
     * @param p_players            list of players participating in the game
     * @param p_commandPromptView  view handling user interactions through the command prompt
     * @param p_commandParts       the command parts passed as input
     */
    public abstract void StartPhase(GameController p_gameController, List<Player> p_players, CommandPromptView p_commandPromptView, String[] p_commandParts);

    /**
     * Transitions to the next game phase based on the specified phase type.
     *
     * @param p_nextPhaseType the type of the next phase to transition to
     * @return the new Phase instance corresponding to the given phase type
     * @throws IllegalArgumentException if an invalid phase type is provided
     */
    public Phase setPhase(PhaseType p_nextPhaseType) {
        switch (p_nextPhaseType) {
            case ISSUE_ORDER:
                System.out.println("Game phase changed to: IssueOrderPhase");
                return new IssueOrderPhase();
            case ORDER_EXECUTION:
                System.out.println("Game phase changed to: OrderExecutionPhase");
                return new OrderExecutionPhase();
            case STARTUP:
                System.out.println("Game phase changed to: StartupPhase");
                return new StartupPhase();
            default:
                throw new IllegalArgumentException("Invalid Phase Type!");
        }
    }
}
