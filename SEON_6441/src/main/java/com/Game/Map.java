package com.Game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;

class Map {
    private List<Territory> territoryList;

    private java.util.Map<String, Integer> continents;
    private boolean hasUniqueTerritories;

    public Map() {
        this.territoryList = new ArrayList<>();
        this.continents = new HashMap<>();
        this.hasUniqueTerritories = true;
    }

    public Map(Map map) {
        this();
        this.hasUniqueTerritories = map.hasUniqueTerritories;
        for (Territory territory : map.territoryList) {
            this.territoryList.add(new Territory(territory));
        }
    }

    public List<Territory> getTerritoryList() {
        return territoryList;
    }

    public void addTerritory(Territory newTerritory) {
        if (getTerritoryByName(newTerritory.getName()) != null) {
            hasUniqueTerritories = false;
        }
        territoryList.add(newTerritory);
    }

    public Territory getTerritoryByName(String name) {
        for (Territory territory : territoryList) {
            if (territory.getName().equals(name)) {
                return territory;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("****Map*****\n");
        for (Territory territory : territoryList) {
            sb.append(territory).append("\n");
        }
        return sb.toString();
    }

    public void addContinent(String continentID, int continentValue) {
        continents.put(continentID, continentValue);
    }

    public void removeContinent(String continentID) {
        continents.remove(continentID);
        territoryList.removeIf(t -> t.getContinent().equals(continentID));
    }

    public void addCountry(String countryID, String continentID) {
        if (!continents.containsKey(continentID)) {
            System.out.println("Continent does not exist.");
            return;
        }
        territoryList.add(new Territory(countryID, continentID, continents.get(continentID)));
    }

    public void removeCountry(String countryID) {
        territoryList.removeIf(t -> t.getName().equals(countryID));
    }

    public void addNeighbor(String countryID, String neighborCountryID) {
        Territory country = getTerritoryByName(countryID);
        Territory neighbor = getTerritoryByName(neighborCountryID);
        if (country != null && neighbor != null) {
            country.addNeighbor(neighbor);
            neighbor.addNeighbor(country);
        }
    }

    public void removeNeighbor(String countryID, String neighborCountryID) {
        Territory country = getTerritoryByName(countryID);
        Territory neighbor = getTerritoryByName(neighborCountryID);
        if (country != null && neighbor != null) {
            country.getNeighborList().remove(neighbor);
            neighbor.getNeighborList().remove(country);
        }
    }

    public void setContinents(java.util.Map<String, Integer> continents) {
        this.continents = new HashMap<>(continents);
    }

    public void saveToFile(String filePath) {
        // Debugging: Print the absolute path to confirm it's correct
        System.out.println("Saving to file: " + new File(filePath).getAbsolutePath());

        // Check if the directory exists, create it if not
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            System.out.println("Directory does not exist. Creating directory: " + parentDir.getAbsolutePath());
            parentDir.mkdirs();  // Creates the directories if they do not exist
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Writing continents section
            writer.write("[continents]\n");
            for (java.util.Map.Entry<String, Integer> entry : continents.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
            writer.write("\n");

            // Writing countries section
            writer.write("[countries]\n");
            for (int i = 0; i < territoryList.size(); i++) {
                Territory t = territoryList.get(i);
                writer.write((i + 1) + " " + t.getName() + " " + (new ArrayList<>(continents.keySet()).indexOf(t.getContinent()) + 1) + "\n");
            }
            writer.write("\n");

            // Writing borders section
            writer.write("[borders]\n");
            for (int i = 0; i < territoryList.size(); i++) {
                Territory t = territoryList.get(i);
                writer.write((i + 1) + "");
                for (Territory neighbor : t.getNeighborList()) {
                    writer.write(" " + (territoryList.indexOf(neighbor) + 1));
                }
                writer.write("\n");
                writer.flush(); // Forces any data in the buffer to be written to the file

            }

            System.out.println("File saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving the file: " + e.getMessage());
        }
    }

}


