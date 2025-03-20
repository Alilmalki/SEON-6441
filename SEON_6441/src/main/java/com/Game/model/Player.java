package com.Game.model;


import java.util.ArrayList;
import java.util.List;
import com.Game.model.order.DeployOrder;
import com.Game.model.order.Order;
import java.util.InputMismatchException;
import com.Game.model.CardType;
import com.Game.model.order.AdvanceAttack;
import com.Game.model.order.AdvanceMove;


/**
 * Represents a player in the game who owns territories and can issue orders.
 * Players can deploy armies, acquire territories, and manage their reinforcements.
 */
public class Player {

    /**
     * The name of the player.
     */
    private String d_name;

    /**
     * The number of reinforcement armies available to the player.
     */
    private int d_nbrOfReinforcementArmies;

    /**
     * A list of territories owned by the player.
     */
    private List<Territory> d_ownedTerritories;

    /**
     * A list of orders issued by the player.
     */
    private List<Order> d_orders;
    private int d_territoriesConqueredPerTurn;
    
    
    private List<CardType> cards; // Collection of cards

    /**
     * Constructor initializing player with a name.
     * 
     * @param p_name Player's name
     */
    public Player(String p_name) {
        this.d_name = p_name;
        this.d_ownedTerritories = new ArrayList<>();
        this.d_orders = new ArrayList<>();
        this.d_nbrOfReinforcementArmies = 0;
        this.cards = new ArrayList<>();
      
    }

    /**
     * Constructor initializing player with a name and reinforcement armies.
     * 
     * @param p_name Player's name
     * @param p_nbrOfReinforcementArmies Number of reinforcement armies
     */
    public Player(String p_name, int p_nbrOfReinforcementArmies) {
        this.d_name = p_name;
        this.d_ownedTerritories = new ArrayList<>();
        this.d_orders = new ArrayList<>();
        this.d_nbrOfReinforcementArmies = p_nbrOfReinforcementArmies;
        this.cards = new ArrayList<>();
    }
    
    

    /**
     * Adds a territory to the player's owned territories.
     * 
     * @param p_territory Territory to be added
     */
    public void addTerritory(Territory p_territory) {
        d_ownedTerritories.add(p_territory);
    }

    /**
     * Removes a territory from the player's owned territories.
     * 
     * @param p_territory Territory to be removed
     */
    public void removeTerritory(Territory p_territory) {
        d_ownedTerritories.remove(p_territory);
    }

    /**
     * Creates a deploy order based on the provided command parameters.
     * This is an MVC-friendly version that doesn't depend on direct Scanner input.
     * 
     * @param p_targetTerritoryName The name of the target territory
     * @param p_numberOfArmies The number of armies to deploy
     * @return true if order creation was successful, false otherwise
     */
    public boolean createDeployOrder(String p_targetTerritoryName, int p_numberOfArmies) {
        Territory l_targetTerritory = findTerritoryByName(p_targetTerritoryName);
        
        if (l_targetTerritory == null) {
            return false;
        }
        
        if (p_numberOfArmies > this.d_nbrOfReinforcementArmies) {
            return false;
        }
        
        DeployOrder l_deployOrder = new DeployOrder(this, l_targetTerritory, p_numberOfArmies);
        d_orders.add(l_deployOrder);
        this.d_nbrOfReinforcementArmies -= p_numberOfArmies;
        return true;
    }
    
    public boolean issueOrder(String p_command, Map m) {
        // Split the command by spaces
        String[] l_parts = p_command.split(" ");
        if (l_parts.length < 2 || l_parts.length > 4) {
            // None of the commands are there
            return false;
        }

        String l_orderType = l_parts[0].toLowerCase();
        

        // Handle "deploy" command
        if (l_orderType.equalsIgnoreCase("deploy")) {
            // Expect exactly 3 parts: deploy <territoryName> <numArmies>
            if (l_parts.length != 3) {
                return false;
            }
            String l_targetTerritoryName = l_parts[1];
            int l_numberOfArmies;  
            
            
            try {
                l_numberOfArmies = Integer.parseInt(l_parts[2]);
            } catch (NumberFormatException e) {
                return false;
            }
            
            
            Territory l_targetTerritory = findTerritoryByName(l_targetTerritoryName);
            if (l_targetTerritory == null) {
                return false;
            }
            
            if (l_numberOfArmies > this.d_nbrOfReinforcementArmies) {
                return false;
            }
            
            if (!l_targetTerritory.getOwner().getName().equals(this.d_name)) {
            	return false;
            }
            
            DeployOrder l_deployOrder = new DeployOrder(this, l_targetTerritory, l_numberOfArmies);
            d_orders.add(l_deployOrder);
            this.d_nbrOfReinforcementArmies -= l_numberOfArmies;
            return true;
        }

        // Handle "advance" command
        else if (l_orderType.equalsIgnoreCase("advance")) {
        	if (l_parts.length != 4) {
        		return false;
        	}
        	
        	Territory l_territoryFrom = findTerritoryByName(l_parts[1]);
        	Territory l_territoryTo = m.getTerritoryByName(l_parts[2]);
        	
        	if(l_territoryFrom == null || l_territoryTo == null) {
        		System.err.println("Territorie(s) not found!");
        		return false;
        	}
        	
        	int l_numberOfArmies;
        	
        	try {
            	l_numberOfArmies = Integer.parseInt(l_parts[3]);
        	}
        	catch(InputMismatchException e) {
        		System.err.println("Invalid number of armies!");
        		return false;
        	}
        	
        	if(!l_territoryFrom.getOwner().getName().equals(this.getName())) {
        		System.err.println(l_territoryFrom + " does not belong to " + this.getName());
        		return false;
        	}
        	
        	if(!l_territoryFrom.hasNeighbor(l_territoryTo)) {
        		System.err.println(l_territoryFrom.getName() + " is not adjacent to " + l_territoryTo.getName());
        		return false;
        	}
        	
        	if(l_territoryFrom.getNumOfArmies() - l_numberOfArmies < 0) {
    			System.err.println("Not enough armies on " + l_territoryFrom.getName() + ". Only " + l_territoryFrom.getNumOfArmies() + " armie(s) available!");
    			return false;
    		}
        	l_territoryFrom.setNumOfArmies(l_territoryFrom.getNumOfArmies() - l_numberOfArmies); // So that the user won't spam the same order again
        	
        	
        	if(l_territoryTo.getOwner().getName().equals(this.getName())) { 
        		// the territory belongs to the current player so we will move armies without attacking
        		//check if terrFrom does not go negative
        		AdvanceMove advanceMove = new AdvanceMove(this, l_territoryFrom, l_territoryTo, l_numberOfArmies);
        		//System.out.println("DEBUG: Added advance move to d_orders");
        		d_orders.add(advanceMove);
        		return true;
        	}
        	else {
        		AdvanceAttack advanceAttack = new AdvanceAttack(this, l_territoryFrom, l_territoryTo, l_numberOfArmies);
        		//System.out.println("DEBUG: Added advance attack to d_orders");
        		d_orders.add(advanceAttack);
        		return true;
        	}
        	
        }

        // If it's neither deploy nor advance
        return false;
    }
    
   

    /**
     * Legacy method for issuing orders directly through user input.
     * To be refactored in future updates to follow MVC pattern.
     */
    public void issueOrder() {
        // Legacy method - to be fully refactored in future updates
        // Current implementation left for backward compatibility
        java.util.Scanner l_scanner = new java.util.Scanner(System.in);
        
        while(true) {
            System.out.print("Hi " + this.d_name + ", please enter your next deploy order, or type FINISH: ");
            
            String l_command = l_scanner.nextLine();
            String[] l_commandParts = l_command.split(" ");
            
            if (l_command.equalsIgnoreCase("FINISH")) {
                break;
            }
           
            if (l_commandParts.length != 3) {
                System.out.println("Invalid command format. Usage: <OrderType> <territoryName> <numArmies>");
                continue;
            }

            String l_orderType = l_commandParts[0];
            String l_targetTerritoryName = l_commandParts[1];
            int l_numberOfArmies;
            
            try {
                l_numberOfArmies = Integer.parseInt(l_commandParts[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of armies: " + l_commandParts[2]);
                continue;
            }

            Territory l_targetTerritory = findTerritoryByName(l_targetTerritoryName);

            if (l_targetTerritory == null) {
                System.out.println("Territory not found!: " + l_targetTerritoryName);
                continue;
            }

            if (l_numberOfArmies > this.d_nbrOfReinforcementArmies) {
                System.out.println("Not enough reinforcements. You have " + this.d_nbrOfReinforcementArmies + " available.");
                continue;
            }
            
            if (l_orderType.equalsIgnoreCase("deploy")) {
                DeployOrder l_deployOrder = new DeployOrder(this, l_targetTerritory, l_numberOfArmies);
                d_orders.add(l_deployOrder);
                this.d_nbrOfReinforcementArmies -= l_numberOfArmies;
                System.out.println(this.d_name + "'s deploy order issued: Deploy " + l_numberOfArmies + " armies to " + l_targetTerritoryName);
                continue;
            }
        }
    }

    /**
     * Retrieves the next order from the player's order queue.
     * 
     * @return Next order or null if queue is empty.
     */
    public Order nextOrder() {
        return d_orders.isEmpty() ? null : d_orders.remove(0);
    }

    /**
     * Gets the number of reinforcement armies available.
     * 
     * @return The number of reinforcement armies.
     */
    public int getNbrOfReinforcementArmies() {
        return d_nbrOfReinforcementArmies;
    }

    /**
     * Sets the number of reinforcement armies available.
     * 
     * @param p_nbrOfReinforcementArmies The number of reinforcement armies to set.
     */
    public void setNbrOfReinforcementArmies(int p_nbrOfReinforcementArmies) {
        this.d_nbrOfReinforcementArmies = p_nbrOfReinforcementArmies;
    }

    /**
     * Gets the name of the player.
     * 
     * @return The player's name.
     */
    public String getName() {
        return d_name;
    }

    /**
     * Sets the name of the player.
     * 
     * @param p_name The name to set for the player.
     */
    public void setName(String p_name) {
        this.d_name = p_name;
    }

    /**
     * Gets the list of territories owned by the player.
     * 
     * @return A list of owned territories.
     */
    public List<Territory> getOwnedTerritories() {
        return d_ownedTerritories;
    }

    /**
     * Sets the list of territories owned by the player.
     * 
     * @param p_ownedTerritories The list of territories to set.
     */
    public void setOwnedTerritories(List<Territory> p_ownedTerritories) {
        this.d_ownedTerritories = p_ownedTerritories;
    }

    /**
     * Gets the list of orders issued by the player.
     * 
     * @return A list of orders.
     */
    public List<Order> getOrders() {
        return d_orders;
    }

    /**
     * Sets the list of orders issued by the player.
     * 
     * @param p_orders The list of orders to set.
     */
    public void setOrders(List<Order> p_orders) {
        this.d_orders = p_orders;
    }

    /**
     * Clears all orders issued by the player.
     */
    public void clearOrders() {
        this.d_orders.clear();
    }
    
    /**
     * Adds a new card to the player's collection.
     */
    public void addCard(CardType p_card) {
        cards.add(p_card);
    }

    /**
     * Returns the player's current list of cards.
     */
    public List<CardType> getCards() {
        return cards;
    }

    /**
     * (Optional) Use or remove a card from the player's collection.
     */
    public boolean useCard(CardType p_card) {
        return cards.remove(p_card);
    }

    /**
     * Finds a territory owned by the player based on its name.
     * 
     * @param p_territoryName Name of the territory
     * @return Territory object if found, else null
     */
    public Territory findTerritoryByName(String p_territoryName) {
        for (Territory l_territory : d_ownedTerritories) {
            if (l_territory.getName().equals(p_territoryName)) {
                return l_territory;
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the player, including the player's name
     * and the number of reinforcement armies available.
     * 
     * @return A string representation of the player.
     */
    @Override
    public String toString() {
        return "\nPlayer: " + this.d_name +
               "\nNumber of Reinforcement Armies: " + this.d_nbrOfReinforcementArmies;
    }
}