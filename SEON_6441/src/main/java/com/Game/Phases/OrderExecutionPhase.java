package com.Game.Phases;

import com.Game.model.Player;
import com.Game.controller.GameController;
import com.Game.model.order.Order;
import com.Game.view.CommandPromptView;

import java.util.List;

/**
 * Represents the Order Execution phase of the game.
 * In this phase, players execute their orders in a sequential manner until all orders are processed.
 */
public class OrderExecutionPhase extends Phase {

    /**
     * Starts the Order Execution phase, where players take turns executing their orders
     * until no orders remain.
     *
     * @param p_gameController    the game controller handling the game state
     * @param p_players           list of players participating in the game
     * @param p_commandPromptView view handling user interactions through the command prompt
     * @param p_commandParts       the command parts passed as input
     */
    @Override
    public void StartPhase(GameController p_gameController, List<Player> p_players, CommandPromptView p_commandPromptView, String[] p_commandParts) {

        // Loop until all orders are executed
        boolean l_ordersRemaining = true;

        while (l_ordersRemaining) {
            l_ordersRemaining = false;

            // Each player executes one order
            for (Player l_player : p_players) {
                Order l_nextOrder = l_player.nextOrder();

                if (l_nextOrder != null) {
                    p_gameController.getView().displayExecutingOrder(l_player.getName());
                    l_nextOrder.execute();
                    l_ordersRemaining = true;
                }
            }
        }

    }
}
