package com.Game.Phases;

import com.Game.model.Player;
import com.Game.controller.GameController;
import com.Game.view.CommandPromptView;

import java.util.List;

/**
 * Represents the Issue Order phase of the game.
 * In this phase, players assign orders such as deploying armies to their territories.
 */
public class IssueOrderPhase extends Phase{

    /**
     * Starts the Issue Order phase, allowing players to assign orders until they finish or run out of reinforcement armies.
     *
     * @param p_gameController    the game controller handling the game state
     * @param p_players           list of players participating in the game
     * @param p_commandPromptView view handling user interactions through the command prompt
     * @param p_commandParts       the command parts passed as input
     */
    @Override
    public void StartPhase(GameController p_gameController, List<Player> p_players, CommandPromptView p_commandPromptView, String[] p_commandParts) {

        for (Player l_player : p_players) {
            if (l_player.getNbrOfReinforcementArmies() > 0) {
                p_gameController.getView().displayPlayerTurn(l_player.getName(), l_player.getNbrOfReinforcementArmies());
                p_gameController.getView().displayPlayerTerritories(l_player.getOwnedTerritories());

                while (l_player.getNbrOfReinforcementArmies() > 0) {
                    String l_orderCommand = p_commandPromptView.getPlayerOrder(
                            l_player.getName(), l_player.getNbrOfReinforcementArmies());

                    if (l_orderCommand.equalsIgnoreCase("FINISH")) {
                        break;
                    }

                    String[] l_orderParts = l_orderCommand.split(" ");

                    if (l_orderParts.length != 3) {
                        p_gameController.getView().displayError("Invalid command format. Usage: <OrderType> <territoryName> <numArmies>");
                        continue;
                    }

                    String l_orderType = l_orderParts[0];
                    String l_targetTerritoryName = l_orderParts[1];
                    int l_numberOfArmies;

                    try {
                        l_numberOfArmies = Integer.parseInt(l_orderParts[2]);
                    } catch (NumberFormatException e) {
                        p_gameController.getView().displayError("Invalid number of armies: " + l_orderParts[2]);
                        continue;
                    }

                    if (l_orderType.equalsIgnoreCase("deploy")) {
                        boolean l_success = l_player.createDeployOrder(l_targetTerritoryName, l_numberOfArmies);

                        if (l_success) {
                            p_gameController.getView().displayMessage(
                                    l_player.getName() + "'s deploy order issued: Deploy " +
                                            l_numberOfArmies + " armies to " + l_targetTerritoryName);
                        } else {
                            p_gameController.getView().displayError(
                                    "Failed to create deploy order. Check territory name and number of armies.");
                        }
                    } else {
                        p_gameController.getView().displayError("Invalid order type. Only 'deploy' is supported in this phase.");
                    }
                }
            }
        }
    }

}
