package com.Game;

/**
 * Represents a deploy order where a player deploys armies to a target territory.
 */
public class DeployOrder extends Order {
    
    private Territory d_targetTerritory; 
    private int d_numberOfArmies; // The number of armies deployed in the target territory

    /**
     * Default constructor.
     */
    public DeployOrder() {
        super();
    }
    
    /**
     * Constructor initializing a deploy order with an issuer, target territory, and number of armies.
     * @param p_issuer The player issuing the order
     * @param p_targetTerritory The territory where armies are deployed
     * @param p_numberOfArmies The number of armies to deploy
     */
    public DeployOrder(Player p_issuer, Territory p_targetTerritory, int p_numberOfArmies) {
        super(p_issuer);
        this.d_targetTerritory = p_targetTerritory;
        this.d_numberOfArmies = p_numberOfArmies;
    }
    
    /**
     * Copy constructor.
     * @param p_order The order to copy
     */
    public DeployOrder(DeployOrder p_order) {
        this.d_issuer = p_order.d_issuer;
    }
    
    /**
     * Executes the deploy order, adding the specified number of armies to the target territory.
     */
    @Override
    public void execute() {
        int l_currentNumberOfArmies = this.d_targetTerritory.getNumOfArmies();
        d_targetTerritory.setNumOfArmies(l_currentNumberOfArmies + d_numberOfArmies);
        
        System.out.println("Order Executed: " + this.d_targetTerritory.getName() + " has now " + this.d_targetTerritory.getNumOfArmies() + " armies.");
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
