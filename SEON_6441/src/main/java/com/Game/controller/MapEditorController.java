package com.Game.controller;

import com.Game.Phases.PhaseType;
import com.Game.model.Map;
import com.Game.model.Territory;
import com.Game.utils.MapLoader;
import com.Game.view.GameView;

import java.io.BufferedReader;

/**
 * Controller class responsible for handling map editing operations.
 * This class processes user commands related to map editing and interacts with the model and view.
 */
public class MapEditorController {
    
    /**
     * Reference to the main game controller.
     */
    private GameController d_gameController;
    
    /**
     * The game map being edited.
     */
    private Map d_gameMap;
    
    /**
     * The map loader used for loading and validating maps.
     */
    private MapLoader d_mapLoader;
    
    /**
     * Constructor initializing the controller with necessary references.
     * 
     * @param p_gameController Reference to the main game controller
     * @param p_gameMap The game map to edit
     * @param p_mapLoader The map loader for file operations
     */
    public MapEditorController(GameController p_gameController, Map p_gameMap, MapLoader p_mapLoader) {
        this.d_gameController = p_gameController;
        this.d_gameMap = p_gameMap;
        this.d_mapLoader = p_mapLoader;
    }
    
    /**
     * Handles a command in the map editing phase.
     * 
     * @param p_commandParts The parts of the command (split by spaces)
     * @param p_command The main command (first word)
     * @param p_isMapLoaded Flag indicating if a map is currently loaded
     * @return Updated map loaded status
     */
    public boolean handleCommand(String[] p_commandParts, String p_command, boolean p_isMapLoaded) {
        boolean l_isMapLoaded = p_isMapLoaded;
        
        switch (p_command) {
            case "editcontinent":
                handleEditContinent(p_commandParts);
                break;
            case "editcountry":
                handleEditCountry(p_commandParts);
                break;
            case "editneighbor":
                handleEditNeighbor(p_commandParts);
                break;
            case "showmap":
                d_gameController.getView().displayMap(d_gameMap, d_gameController.getPlayers());
                break;
            case "savemap":
                handleSaveMap(p_commandParts);
                break;
            case "editmap":
                l_isMapLoaded = true;
                handleEditMap(p_commandParts);
                break;
            case "validatemap":
                handleValidateMap();
                break;
            case "loadmap":
                l_isMapLoaded = true;
                handleLoadMap(p_commandParts);
                break;
            case "gameplayer":
                // Transition to startup phase
                d_gameController.setCurrentPhase(GameController.STARTUP_PHASE);
                d_gameController.setStartupPhase(d_gameController, p_commandParts);
                break;
            default:
                d_gameController.getView().displayError("Unknown command: " + p_command);
        }
        return l_isMapLoaded;
    }
    
    /**
     * Handles the editcontinent command to add or remove continents.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleEditContinent(String[] p_commandParts) {
        if (p_commandParts.length < 3) {
            d_gameController.getView().displayError("Usage: editcontinent -add continentID continentvalue -remove continentID");
            return;
        }
        
        String l_action = p_commandParts[1];
        
        if (l_action.equals("-add") && p_commandParts.length >= 4) {
            String l_continentID = p_commandParts[2];
            try {
                int l_continentValue = Integer.parseInt(p_commandParts[3]);
                d_gameMap.addContinent(l_continentID, l_continentValue);
                d_gameController.getView().displayMessage("Continent added: " + l_continentID);
            } catch (NumberFormatException e) {
                d_gameController.getView().displayError("Invalid continent value: " + p_commandParts[3]);
            }
        } else if (l_action.equals("-remove") && p_commandParts.length >= 3) {
            String l_continentID = p_commandParts[2];
            d_gameMap.removeContinent(l_continentID);
            d_gameController.getView().displayMessage("Continent removed: " + l_continentID);
        } else {
            d_gameController.getView().displayError("Usage: editcontinent -add continentID continentvalue -remove continentID");
        }
    }
    
    /**
     * Handles the editcountry command to add or remove countries.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleEditCountry(String[] p_commandParts) {
        if (p_commandParts.length < 2) {
            d_gameController.getView().displayError("Usage: editcountry -add countryID continentID | editcountry -remove countryID");
            return;
        }
        
        String l_action = p_commandParts[1];
        
        if (l_action.equals("-add")) {
            
            if (p_commandParts.length != 4) {
                d_gameController.getView().displayError("Usage: editcountry -add countryID continentID");
                return;
            }
            String l_countryID = p_commandParts[2];
            String l_continentID = p_commandParts[3];
            d_gameMap.addCountry(l_countryID, l_continentID);
            d_gameController.getView().displayMessage("Country added: " + l_countryID);
            
        } else if (l_action.equals("-remove")) {
            
            if (p_commandParts.length != 3) {
                d_gameController.getView().displayError("Usage: editcountry -remove countryID");
                return;
            }
            String l_countryID = p_commandParts[2];
            d_gameMap.removeCountry(l_countryID);
            d_gameController.getView().displayMessage("Country removed: " + l_countryID);
            
        } else {
            d_gameController.getView().displayError("Invalid action for editcountry.");
        }
    }
    
    /**
     * Handles the editneighbor command to add or remove connections between countries.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleEditNeighbor(String[] p_commandParts) {
        if (p_commandParts.length < 4) {
            d_gameController.getView().displayError("Usage: editneighbor -add countryID neighborCountryID -remove countryID neighborCountryID");
            return;
        }
        
        String l_action = p_commandParts[1];
        String l_countryID = p_commandParts[2];
        String l_neighborCountryID = p_commandParts[3];
        
        if (l_action.equals("-add")) {
            d_gameMap.addNeighbor(l_countryID, l_neighborCountryID);
            d_gameController.getView().displayMessage("Neighbor added between: " + l_countryID + " and " + l_neighborCountryID);
        } else if (l_action.equals("-remove")) {
            d_gameMap.removeNeighbor(l_countryID, l_neighborCountryID);
            d_gameController.getView().displayMessage("Neighbor removed between: " + l_countryID + " and " + l_neighborCountryID);
        } else {
            d_gameController.getView().displayError("Invalid action for editneighbor.");
        }
    }
    
    /**
     * Handles the savemap command to save the current map to a file.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleSaveMap(String[] p_commandParts) {
        if (p_commandParts.length < 2) {
            d_gameController.getView().displayError("Usage: savemap filename");
            return;
        }
        
        String l_filename = p_commandParts[1];
        d_gameMap.saveToFile(l_filename);
    }
    
    /**
     * Handles the editmap command to load an existing map for editing or create a new one.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleEditMap(String[] p_commandParts) {
        if (p_commandParts.length < 2) {
            d_gameController.getView().displayError("Usage: editmap filename");
            return;
        }
        
        String l_mapFilePath = p_commandParts[1];
        d_gameController.setMapFilePath(l_mapFilePath);
        d_mapLoader.resetLoadedMap();

        // Check if map exists
        BufferedReader l_reader = null;
        boolean l_isMapExist = false;
        l_reader = d_mapLoader.isMapExist(l_mapFilePath);
        if (l_reader != null) {
            l_isMapExist = true;
        }

        if(l_isMapExist) {
            boolean l_isMapInitiallyValid = d_mapLoader.isValid(l_mapFilePath);

            if (l_isMapInitiallyValid) {
                d_mapLoader.read(l_mapFilePath);
                d_gameMap = d_mapLoader.getLoadedMap();
                d_gameController.setGameMap(d_gameMap);

                if(d_mapLoader.validateMap()) {
                    d_gameController.getView().displayMessage(l_mapFilePath + " is loaded successfully.");
                }
            }
        } else {
            d_gameController.getView().displayMessage("The specified map is not exist, a new map is created.");
            d_gameMap = new Map();
            d_gameController.setGameMap(d_gameMap);
        }
    }
    
    /**
     * Validates the current map.
     */
    private void handleValidateMap() {
        String l_mapFilePath = d_gameController.getMapFilePath();

        if (l_mapFilePath != null && d_gameMap != null) {
            if (d_mapLoader.isValid(l_mapFilePath)) {
                d_mapLoader.validateMap();
            } else {
                d_gameController.getView().displayError("The map is invalid.");
            }
        } else {
            d_gameController.getView().displayError("No map loaded to validate.");
        }
    }
    
    /**
     * Handles the loadmap command to load a map for gameplay.
     * 
     * @param p_commandParts Array of command components
     */
    private void handleLoadMap(String[] p_commandParts) {
        if (p_commandParts.length < 2) {
            d_gameController.getView().displayError("Usage: loadmap filename");
            return;
        }
        
        String l_mapFilePath = p_commandParts[1];
        d_gameController.setMapFilePath(l_mapFilePath);
        d_mapLoader.resetLoadedMap();

        BufferedReader l_reader = null;
        boolean l_isMapExist = false;
        l_reader = d_mapLoader.isMapExist(l_mapFilePath);
        if (l_reader != null) {
            l_isMapExist = true;
        }

        if(l_isMapExist) {
            boolean l_isMapInitiallyValid = d_mapLoader.isValid(l_mapFilePath);
            if (l_isMapInitiallyValid) {
                d_mapLoader.read(l_mapFilePath);
                d_gameMap = d_mapLoader.getLoadedMap();
                d_gameController.setGameMap(d_gameMap);

                if(d_mapLoader.validateMap()) {
                    d_gameController.getView().displayMessage(l_mapFilePath + " is loaded successfully.");
                    d_gameController.setCurrentPhase(GameController.STARTUP_PHASE);
                }
            }
        } else {
            d_gameController.getView().displayError("The specified map does not exist.");
            d_gameMap = new Map();
            d_gameController.setGameMap(d_gameMap);
        }
    }
}