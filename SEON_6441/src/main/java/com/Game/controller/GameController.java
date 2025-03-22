package com.Game.controller;

import com.Game.Phases.IssueOrderPhase;
import com.Game.Phases.Phase;
import com.Game.Phases.PhaseType;
import com.Game.Phases.StartupPhase;
import com.Game.model.Map;
import com.Game.model.Player;
import com.Game.utils.MapLoader;
import com.Game.view.GameView;
import com.Game.view.CommandPromptView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller class that coordinates the game flow.
 * This class manages the game state, delegates to specialized controllers,
 * and handles transitions between different game phases.
 */
public class GameController {
    
    /**
     * Constant representing the Map Editing phase of the game.
     */
    public static final int MAP_EDITING_PHASE = 0;
    
    /**
     * Constant representing the Startup phase of the game.
     */
    public static final int STARTUP_PHASE = 1;
    
    /**
     * Constant representing the Main Game phase of the game.
     */
    public static final int MAIN_GAME_PHASE = 2;
    
    /**
     * The current game map being used.
     */
    private Map d_gameMap;
    
    /**
     * The map loader responsible for loading and validating maps.
     */
    private MapLoader d_mapLoader;
    
    /**
     * The file path of the current map.
     */
    private String d_mapFilePath;
    
    /**
     * The list of players in the game.
     */
    private List<Player> d_players;
    
    /**
     * The neutral player used for certain game mechanics.
     */
    private Player d_neutralPlayer;
    
    /**
     * Indicates whether the game has started or not.
     */
    private boolean d_gameStarted;
    
    /**
     * Represents the current phase of the game.
     */
    private int d_currentPhase;
    
    /**
     * Indicates whether the countries have been assigned to players.
     */
    private boolean d_countriesAssigned;
    
    /**
     * The view for displaying game information.
     */
    private GameView d_view;
    
    /**
     * The view for handling command input.
     */
    private CommandPromptView d_commandPromptView;
    
    /**
     * Controller for map editing operations.
     */
    private MapEditorController d_mapEditorController;
    
    /**
     * Controller for gameplay operations.
     */
    private GamePlayController d_gamePlayController;

    /**
     * Represents the startup phase of the game.
     */
    private Phase d_startupPhase;;

    /**
     * Default constructor that initializes the game controller.
     */
    public GameController() {
        this.d_gameMap = new Map();
        this.d_mapLoader = new MapLoader();
        this.d_players = new ArrayList<>();
        this.d_neutralPlayer = new Player("Neutral");
        this.d_gameStarted = false;
        this.d_currentPhase = MAP_EDITING_PHASE;
        this.d_countriesAssigned = false;
        
        this.d_view = new GameView();
        this.d_commandPromptView = new CommandPromptView();
        this.d_mapEditorController = new MapEditorController(this, d_gameMap, d_mapLoader);
        this.d_gamePlayController = new GamePlayController(this, d_gameMap, d_players);
        d_startupPhase =  new StartupPhase();
    }
    
    /**
     * Starts the game and processes user commands.
     * Provides a command prompt that's available throughout the game.
     */
    public void startGame() {

        d_startupPhase.setPhase(PhaseType.STARTUP);

        d_view.displayWelcomeMessage();
        boolean l_isMapLoaded = false;
        while (true) {
            String l_input;
            String[] l_commandParts;

            if (d_currentPhase == MAP_EDITING_PHASE) {
                d_view.displayMapEditingMenu();
                l_input = d_commandPromptView.getCommand();
                l_commandParts = l_input.split("\\s+");

                if (l_commandParts.length == 0) continue;

                String l_command = l_commandParts[0];

                if (l_command.equals("exit")) {
                    d_view.displayMessage("Exiting the program.");
                    d_commandPromptView.closeScanner();
                    System.exit(0);
                }

                if (!l_isMapLoaded && !l_command.equals("editmap") && !l_command.equals("loadmap")) {
                    d_view.displayError("You must load/edit a map first using the 'editmap' or 'loadmap' command.");
                } else {
                    l_isMapLoaded = d_mapEditorController.handleCommand(l_commandParts, l_command, l_isMapLoaded);
                }
            } else if (d_currentPhase == STARTUP_PHASE) {
                d_view.displayStartupMenu();
                l_input = d_commandPromptView.getCommand();
                l_commandParts = l_input.split("\\s+");

                if (l_commandParts.length == 0) continue;

                String l_command = l_commandParts[0];

                if (l_command.equals("exit")) {
                    d_view.displayMessage("Exiting the program.");
                    d_commandPromptView.closeScanner();
                    System.exit(0);
                }

                handleStartupCommand(l_commandParts, l_command);
            } else if (d_currentPhase == MAIN_GAME_PHASE) {
                d_view.displayMainGameMenu();
                l_input = d_commandPromptView.getCommand();
                l_commandParts = l_input.split("\\s+");

                if (l_commandParts.length == 0) continue;

                String l_command = l_commandParts[0];

                if (l_command.equals("exit")) {
                    d_view.displayMessage("Exiting the program.");
                    d_commandPromptView.closeScanner();
                    System.exit(0);
                }

                d_gamePlayController.handleCommand(l_commandParts, l_command);
            }
        }
    }
    
    /**
     * Handles commands specific to the startup phase.
     * 
     * @param p_commandParts Array of command components
     * @param p_command The main command
     */
    private void handleStartupCommand(String[] p_commandParts, String p_command) {
        switch (p_command) {
            case "showmap":
                d_view.displayMap(d_gameMap, d_players);
                break;
            case "gameplayer":
                if (p_commandParts.length >= 3) {
                    String l_action = p_commandParts[1];
                    String l_playerName = p_commandParts[2];
                    handleGamePlayer(l_action, l_playerName);
                } else {
                    d_view.displayError("Usage: gameplayer -add playerName OR gameplayer -remove playerName");
                }
                break;
            case "assigncountries":
                if (d_gamePlayController.handleAssignCountries()) {
                    d_countriesAssigned = true;
                }
                break;
            case "startgame":
                if (!d_countriesAssigned) {
                    d_view.displayError("You must assign countries first using 'assigncountries' before starting the game.");
                } else {
                    d_gamePlayController.startMainGame();
                }
                break;
            case "editmap":
                // Allow returning to map editing phase
                d_currentPhase = MAP_EDITING_PHASE;
                d_mapEditorController.handleCommand(p_commandParts, p_command, true);
                break;
            case "loadmap":
                // Allow loading a different map
                d_currentPhase = MAP_EDITING_PHASE;
                d_mapEditorController.handleCommand(p_commandParts, p_command, true);
                break;
            default:
                d_view.displayError("Unknown command or invalid for current phase: " + p_command);
        }
    }
    
    /**
     * Handles the gameplayer command to add or remove players.
     * 
     * @param p_action The action to perform (add/remove)
     * @param p_playerName The name of the player to add/remove
     */
    public void handleGamePlayer(String p_action, String p_playerName) {
        if (p_action.equals("-add")) {
            // Check if player already exists
            boolean l_playerExists = false;
            for (Player l_player : d_players) {
                if (l_player.getName().equals(p_playerName)) {
                    l_playerExists = true;
                    break;
                }
            }

            if (!l_playerExists) {
                d_players.add(new Player(p_playerName));
                d_view.displayMessage("Player added: " + p_playerName);
            } else {
                d_view.displayError("Player already exists: " + p_playerName);
            }
        } else if (p_action.equals("-remove")) {
            d_players.removeIf(player -> player.getName().equals(p_playerName));
            d_view.displayMessage("Player removed: " + p_playerName);
        } else {
            d_view.displayError("Invalid action for gameplayer.");
        }
    }
    
    /**
     * Gets the game view.
     * 
     * @return The game view
     */
    public GameView getView() {
        return d_view;
    }
    
    /**
     * Gets the command prompt view.
     * 
     * @return The command prompt view
     */
    public CommandPromptView getCommandPromptView() {
        return d_commandPromptView;
    }
    
    /**
     * Gets the game map.
     * 
     * @return The game map
     */
    public Map getGameMap() {
        return d_gameMap;
    }
    
    /**
     * Sets the game map.
     * 
     * @param p_gameMap The game map to set
     */
    public void setGameMap(Map p_gameMap) {
        this.d_gameMap = p_gameMap;
        this.d_gamePlayController.setGameMap(p_gameMap);
    }
    
    /**
     * Gets the list of players.
     * 
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return d_players;
    }
    
    /**
     * Sets the list of players.
     * 
     * @param p_players The list of players to set
     */
    public void setPlayers(List<Player> p_players) {
        this.d_players = p_players;
        this.d_gamePlayController.setPlayers(p_players);
    }
    
    /**
     * Gets the map file path.
     * 
     * @return The map file path
     */
    public String getMapFilePath() {
        return d_mapFilePath;
    }
    
    /**
     * Sets the map file path.
     * 
     * @param p_mapFilePath The map file path to set
     */
    public void setMapFilePath(String p_mapFilePath) {
        this.d_mapFilePath = p_mapFilePath;
    }
    
    /**
     * Checks if the game has started.
     * 
     * @return true if the game has started, false otherwise
     */
    public boolean isGameStarted() {
        return d_gameStarted;
    }
    
    /**
     * Sets whether the game has started.
     * 
     * @param p_gameStarted The game started status to set
     */
    public void setGameStarted(boolean p_gameStarted) {
        this.d_gameStarted = p_gameStarted;
    }
    
    /**
     * Gets the current phase of the game.
     * 
     * @return The current phase
     */
    public int getCurrentPhase() {
        return d_currentPhase;
    }
    
    /**
     * Sets the current phase of the game.
     * 
     * @param p_currentPhase The current phase to set
     */
    public void setCurrentPhase(int p_currentPhase) {
        this.d_currentPhase = p_currentPhase;
    }

    /**
     * Sets the game phase to the startup phase.
     *
     * @param p_gameController the game controller managing the game state
     * @param p_commandParts   the command parts passed as input
     */
    public void setStartupPhase(GameController p_gameController, String[] p_commandParts)
    {
        d_startupPhase.setPhase(PhaseType.STARTUP);
        d_startupPhase.StartPhase(p_gameController, null, null, p_commandParts);
    }

}