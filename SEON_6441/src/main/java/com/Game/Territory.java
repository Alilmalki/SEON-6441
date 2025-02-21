package com.Game;
import java.util.ArrayList;
import java.util.List;

public class Territory {
    private String name;
    private String continent;
    private int bonus;
    private int numOfArmies;
    private Player owner;
    private int numOfReservedArmies;
    private boolean isVisited;
    private List<Territory> neighborList;

    public Territory(String name, String continent, int bonus) {
        this.name = name;
        this.continent = continent;
        this.bonus = bonus;
        this.numOfArmies = 0;
        this.owner = null;
        this.numOfReservedArmies = 0;
        this.isVisited = false;
        this.neighborList = new ArrayList<>();
    }

    public Territory(Territory territory) {
        this(territory.name, territory.continent, territory.bonus);
        this.numOfArmies = territory.numOfArmies;
        this.numOfReservedArmies = territory.numOfReservedArmies;
        this.owner = territory.owner;

        for (Territory neighbor : territory.neighborList) {
            this.neighborList.add(neighbor);
        }
    }

    public String getName() { return name; }
    public String getContinent() { return continent; }
    public int getBonus() { return bonus; }
    public int getNumOfArmies() { return numOfArmies; }
    public void setNumOfArmies(int numOfArmies) { this.numOfArmies = numOfArmies; }
    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }
    public int getNumOfReservedArmies() { return numOfReservedArmies; }
    public void setNumOfReservedArmies(int numOfReservedArmies) { this.numOfReservedArmies = numOfReservedArmies; }
    public List<Territory> getNeighborList() { return neighborList; }

    public boolean hasNeighbor(Territory target) {
        return neighborList.contains(target);
    }

    public void addNeighbor(Territory neighbor) {
        neighborList.add(neighbor);
    }

    public List<Territory> getEnemyNeighbors() {
        List<Territory> enemyNeighbors = new ArrayList<>();
        for (Territory neighbor : neighborList) {
            if (neighbor.getOwner() != this.owner) {
                enemyNeighbors.add(neighbor);
            }
        }
        return enemyNeighbors;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null || obj.getClass() != this.getClass()) {
    		return false;
    	}
    	Territory t = (Territory)obj;
    	return t.getName() == this.name && t.getContinent() == this.continent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Territory Name: ").append(name)
                .append(" Continent: ").append(continent)
                .append(" Number of Armies: ").append(numOfArmies)
                .append(" Neighbors: ");
        for (Territory neighbor : neighborList) {
            sb.append(neighbor.getName()).append(", ");
        }
        return sb.toString();
    }
}