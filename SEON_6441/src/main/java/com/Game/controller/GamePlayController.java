package com.Game.controller;

import com.Game.Phases.IssueOrderPhase;
import com.Game.Phases.Phase;
import com.Game.Phases.PhaseType;
import com.Game.model.Map;
import com.Game.model.Player;
import com.Game.model.Territory;
import com.Game.model.order.Order;
import com.Game.view.GameView;
import com.Game.view.CommandPromptView;

import java.util.List;
import java.util.Random;

/**
 * Controller class responsible for handling gameplay operations.
 * This class processes user commands related to the main game phase, including issuing orders,
 * executing orders, and managing the game turn cycle.
 */
public class GamePlayController {
    
    /**
     * Reference to the main game controller.
     */
    private GameController d_gameController;
    
    /**
     * The game map being used.
     */
    private Map d_gameMap;
    
    /**
     * The list of players in the game.
     */
    private List<Player> d_players;
    
    /**
     * Constructor initializing the controller with necessary references.
     * 
     * @param p_gameController Reference to the main game controller
     * @param p_gameMap The game map
     * @param p_players The list of players
     */
    public GamePlayController(GameController p_gameController, Map p_gameMap, List<Player> p_players) {
        this.d_gameController = p_gameController;
        this.d_gameMap = p_gameMap;
        this.d_players = p_players;
    }
    
    /**
     * Updates the game map reference.
     * 
     * @param p_gameMap The new game map
     */
    public void setGameMap(Map p_gameMap) {
        this.d_gameMap = p_gameMap;
    }
    
    /**
     * Updates the player list reference.
     * 
     * @param p_players The new list of players
     */
    public void setPlayers(List<Player> p_players) {
        this.d_players = p_players;
    }

    /**
     * Represents the current phase of the game.
     */
    public Phase d_currentPhase =  new IssueOrderPhase();;

    /**
     * Handles a command in the main game phase.
     * 
     * @param p_commandParts The parts of the command (split by spaces)
     * @param p_command The main command (first word)
     */
    public void handleCommand(String[] p_commandParts, String p_command) {
        switch (p_command) {
            case "showmap":
                d_gameController.getView().displayMap(d_gameMap, d_players);
                break;
            case "issueorder":
                handleIssueOrder();
                break;
            case "executeorders":
                handleExecuteOrders();
                break;
            case "endturn":
                handleEndTurn();
                break;
            default:
                d_gameController.getView().displayError("Unknown command or invalid for current phase: " + p_command);
        }
    }
    
    /**
     * Handles the reinforcement phase of the game.
     * Calculates and assigns reinforcement armies for each player.
     */
    public void handleReinforcement() {
        if (!d_gameController.isGameStarted()) {
            d_gameController.getView().displayError("Game has not started yet. Use 'startgame' command.");
            return;
        }
        
        d_gameController.getView().displayReinforcementPhase();
        
        // Calculate reinforcements for each player
        for (Player l_player : d_players) {
            // Basic calculation: number of territories divided by 3, minimum 3
            int l_reinforcements = Math.max(3, l_player.getOwnedTerritories().size() / 3);
            
            l_player.setNbrOfReinforcementArmies(l_reinforcements);
            d_gameController.getView().displayReinforcementAllocation(l_player.getName(), l_reinforcements);
        }
        
        d_gameController.getView().displayReinforcementComplete();
    }
    
    /**
     * Handles the issue order phase of the game.
     * Allows each player to issue deploy orders.
     */
    private void handleIssueOrder() {
        if (!d_gameController.isGameStarted()) {
            d_gameController.getView().displayError("Game has not started yet. Use 'startgame' command.");
            return;
        }
        
        d_gameController.getView().displayIssueOrdersPhase();

        d_currentPhase = d_currentPhase.setPhase(PhaseType.ISSUE_ORDER);
        d_currentPhase.StartPhase(d_gameController, d_players, d_gameController.getCommandPromptView(), null);

        
        d_gameController.getView().displayIssueOrdersComplete();
    }
    
    /**
     * Handles the execute orders phase of the game.
     * Executes all orders in round-robin fashion.
     */
    private void handleExecuteOrders() {
        if (!d_gameController.isGameStarted()) {
            d_gameController.getView().displayError("Game has not started yet. Use 'startgame' command.");
            return;
        }
        
        d_gameController.getView().displayExecuteOrdersPhase();

        d_currentPhase = d_currentPhase.setPhase(PhaseType.ORDER_EXECUTION);
        d_currentPhase.StartPhase(d_gameController, d_players, d_gameController.getCommandPromptView(), null);

        
        d_gameController.getView().displayExecuteOrdersComplete();
    }
    
    /**
     * Handles the end of a turn.
     * Checks for a winner and either ends the game or starts a new turn.
     */
    private void handleEndTurn() {
        if (!d_gameController.isGameStarted()) {
            d_gameController.getView().displayError("Game has not started yet. Use 'startgame' command.");
            return;
        }
        
        // Check for game end condition
        Player l_winner = checkForWinner();
        
        if (l_winner != null) {
            d_gameController.getView().displayWinner(l_winner.getName());
            
            d_gameController.setGameStarted(false);
            d_gameController.setCurrentPhase(GameController.MAP_EDITING_PHASE);
        } else {
            // Start a new turn with reinforcement phase
            d_gameController.getView().displayEndTurn();
            handleReinforcement();
        }
    }
    
    /**
     * Checks if there is a winner in the current game state.
     * 
     * @return The winning player, or null if there is no winner yet
     */
    private Player checkForWinner() {
        // Check if any player owns all territories
        for (Player l_player : d_players) {
            if (l_player.getOwnedTerritories().size() == d_gameMap.getTerritoryList().size()) {
                return l_player;
            }
        }
        
        // Check if only one player has territories
        int l_playersWithTerritories = 0;
        Player l_lastPlayerWithTerritories = null;
        
        for (Player l_player : d_players) {
            if (l_player.getOwnedTerritories().size() > 0) {
                l_playersWithTerritories++;
                l_lastPlayerWithTerritories = l_player;
            }
        }
        
        if (l_playersWithTerritories == 1) {
            return l_lastPlayerWithTerritories;
        }
        
        return null; // No winner yet
    }
    
    /**
     * Handles the assignment of countries to players.
     * Randomly distributes territories among players.
     * 
     * @return true if assignment was successful, false otherwise
     */
    public boolean handleAssignCountries() {
        if (d_players.size() < 2) {
            d_gameController.getView().displayError("Need at least 2 players to start the game.");
            return false;
        }
        
        // Get all territories from the map
        List<Territory> l_territories = d_gameMap.getTerritoryList();
        
        if (l_territories.isEmpty()) {
            d_gameController.getView().displayError("No territories in the map. Cannot assign countries.");
            return false;
        }
        
        // Shuffle territories for random assignment
        Random l_random = new Random();
        for (int i = l_territories.size() - 1; i > 0; i--) {
            int l_index = l_random.nextInt(i + 1);
            Territory l_temp = l_territories.get(l_index);
            l_territories.set(l_index, l_territories.get(i));
            l_territories.set(i, l_temp);
        }
        
        // Clear any existing ownership
        for (Player l_player : d_players) {
            l_player.getOwnedTerritories().clear();
        }
        
        // Assign territories to players
        int l_playerCount = d_players.size();
        for (int i = 0; i < l_territories.size(); i++) {
            Territory l_territory = l_territories.get(i);
            Player l_player = d_players.get(i % l_playerCount);
            
            l_territory.setOwner(l_player);
            l_player.addTerritory(l_territory);
            
            // Set initial armies (e.g., 1 per territory)
            l_territory.setNumOfArmies(1);
        }
        
        d_gameController.getView().displayMessage("Countries assigned to players:");
        for (Player l_player : d_players) {
            d_gameController.getView().displayMessage(
                l_player.getName() + " owns " + l_player.getOwnedTerritories().size() + " territories.");
        }
        
        d_gameController.getView().displayMessage("Ready to start the game. Use 'startgame' command to begin.");
        return true;
    }
    
    /**
     * Starts the main game after the startup phase.
     * 
     * @return true if the game started successfully, false otherwise
     */
    public boolean startMainGame() {
        if (d_players.size() < 2) {
            d_gameController.getView().displayError("Need at least 2 players to start the game.");
            return false;
        }
        
        // Initialize the game
        d_gameController.setGameStarted(true);
        d_gameController.setCurrentPhase(GameController.MAIN_GAME_PHASE);
        
        d_gameController.getView().displayMessage("Game started! Beginning reinforcement phase.");
        // Start with reinforcement phase
        handleReinforcement();
        return true;
    }
}