package com.Game.Phases;

import com.Game.controller.GameController;
import com.Game.model.Player;
import com.Game.view.CommandPromptView;

import java.util.List;

/**
 * Represents the Startup phase of the game.
 * This phase is responsible for initializing game settings, assigning players, and preparing the game state before gameplay begins.
 */
public class StartupPhase extends Phase {

    /**
     * Starts the Startup phase with the provided game controller, players, and command prompt view.
     * This method will handle game setup, such as initializing players and allocating resources.
     *
     * @param p_gameController    the game controller handling the game state
     * @param p_players           list of players participating in the game
     * @param p_commandPromptView view handling user interactions through the command prompt
     * @param p_commandParts       the command parts passed as input
     */
    @Override
    public void StartPhase(GameController p_gameController, List<Player> p_players, CommandPromptView p_commandPromptView, String[] p_commandParts) {
        if (p_commandParts.length < 3) {
            p_gameController.getView().displayError("Usage: gameplayer -add playerName OR gameplayer -remove playerName");
            return;
        }

        String l_action = p_commandParts[1];
        String l_playerName = p_commandParts[2];

        p_gameController.handleGamePlayer(l_action, l_playerName);
    }
}
