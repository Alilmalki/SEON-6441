package com.UnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


import com.Game.Player;
import com.Game.Order;
import com.Game.Territory;
import com.Game.DeployOrder;

public class PlayerTest {
	
	private Player player;
	private Territory territory;
	
	@BeforeEach
	void setup() {
		player = new Player("Ali");
		territory = new Territory("Canada", "America", 5);
		territory.setOwner(player);
		player.getOwnedTerritories().add(territory);
		
		player.setNbrOfReinforcementArmies(10);
	}
	
	@Test
	void testValidDeployOrder() {
		
		String input = "deploy Canada 5\n";
		
		InputStream backupIn = System.in;
		
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
			System.setIn(in);
			player.issueOrder();
		} finally {
			System.setIn(backupIn);
		}
		
	
		
		//Making sure that the number of armies the player has went from 10 to 5
		assertEquals(5, player.getNbrOfReinforcementArmies());
		
		//Making sure that the order is created and exists in the order list of the player
		Order order = player.nextOrder();
		assertNotNull(order);
		
		//Making sure that its a deploy order and not any type of orders
		assertTrue(order instanceof DeployOrder);
		
		// Making sure that there are no armies in the target territory
        assertEquals(0, territory.getNumOfArmies());
		
		order.execute();
		
		//Making sure that the territory armies increased from 0 to 5
        assertEquals(5, territory.getNumOfArmies());

		
		
	}
	
	@Test
	void testNotEnoughReinforcements() {

		String fakeInput = "deploy Canada 20\n";
        InputStream backupIn = System.in;

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(fakeInput.getBytes());
            System.setIn(in);

            player.issueOrder();

        } finally {
            System.setIn(backupIn);
        }

        // The deploy should fail because the player only has 10
        assertEquals(10, player.getNbrOfReinforcementArmies());
        
        // Making sure that the order was not added
        assertNull(player.nextOrder(), "No order should have been created.");
    }
	
	@Test
    void testUnknownTerritory() {
        // Provide fake input: "deploy Berlin 3\n" (player does not own "Berlin")
        String fakeInput = "deploy USA 3\n";
        InputStream backupIn = System.in;

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(fakeInput.getBytes());
            System.setIn(in);

            player.issueOrder();

        } finally {
            System.setIn(backupIn);
        }

        // The reinforcement pool should remain unchanged
        assertEquals(10, player.getNbrOfReinforcementArmies());
        // No order should be created
        assertNull(player.nextOrder());
    }
	
	@Test
	void testIssueOrder_MultipleOrders() {
	    // Provide multiple lines of input, each line representing one deploy command.
	    // For example, we want to deploy 3 armies, then 2 armies, to "Paris".
	    String fakeInput1 = "deploy Canada 3\n";
	    String fakeInput2 = "deploy Canada 2\n";
	    
	    // Backup the original System.in
	    InputStream backupIn = System.in;

	    try {
	        ByteArrayInputStream in = new ByteArrayInputStream(fakeInput1.getBytes());
	        System.setIn(in);
	        player.issueOrder();
	        
	        in = new ByteArrayInputStream(fakeInput2.getBytes());
	        System.setIn(in);
	        player.issueOrder();

	    } finally {
	        System.setIn(backupIn);
	    }
	    
	    

	    // Now we should have two orders
	    // The total reinforcements used: 3 + 2 = 5, so the player’s pool should be 5 left (10 - 5).
	    assertEquals(5, player.getNbrOfReinforcementArmies(), 
	        "Reinforcement pool should be 5 after deploying 3 and 2 armies.");

	    // Retrieving the first order
	    Order firstOrder = player.nextOrder();
	    assertNotNull(firstOrder, "First order should not be null.");
	    firstOrder.execute();
	    
	    // After the first deploy, territory should have 3 armies.
	    assertEquals(3, territory.getNumOfArmies(), 
	        "Territory should have 3 armies after the first deploy.");
	    
	    // Retrieving the second order
	    Order secondOrder = player.nextOrder();
	    assertNotNull(secondOrder, "Second order should not be null.");
	    secondOrder.execute();
	    
	    // After the second deploy, territory should have 5 armies total (3 + 2).
	    assertEquals(5, territory.getNumOfArmies(), 
	        "Territory should have 5 armies total after both deploy orders.");

	    // There should be no more orders in the queue
	    assertNull(player.nextOrder(), "No more orders should remain.");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
