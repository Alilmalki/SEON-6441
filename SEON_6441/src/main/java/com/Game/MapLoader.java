package com.Game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;


public class MapLoader {
    private Map loadedMap;

    public MapLoader() {
        this.loadedMap = null;
    }

    public MapLoader(MapLoader other) {
        this.loadedMap = new Map(other.loadedMap);
    }

    public Map getLoadedMap() {
        return loadedMap;
    }

    public void read(String fileName) {
        BufferedReader reader = null;

        try {
            // First, try reading from the local filesystem
            File file = new File(fileName);
            if (file.exists()) {
                reader = new BufferedReader(new FileReader(file));
            } else {
                // Fallback to reading from classpath resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                } else {
                    System.err.println("File not found: " + fileName);
                    return;
                }
            }

            loadedMap = new Map();
            // Reading the file content
            String line;
            List<String> continentNames = new ArrayList<>();
            List<Integer> bonuses = new ArrayList<>();
            List<Territory> territories = new ArrayList<>();
            java.util.Map<String, Integer> continentMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("[continents]")) {
                    while (!(line = reader.readLine()).isEmpty()) {
                        String[] parts = line.split(" ");
                        continentNames.add(parts[0]);
                        bonuses.add(Integer.parseInt(parts[1]));
                        continentMap.put(parts[0], Integer.parseInt(parts[1]));
                    }
                    loadedMap.setContinents(continentMap);
                } else if (line.equals("[countries]")) {
                    while (!(line = reader.readLine()).isEmpty()) {
                        String[] parts = line.split(" ");
                        Territory t = new Territory(parts[1], continentNames.get(Integer.parseInt(parts[2]) - 1), bonuses.get(Integer.parseInt(parts[2]) - 1));
                        territories.add(t);
                        loadedMap.addTerritory(t);
                    }
                } else if (line.equals("[borders]")) {
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ");
                        Territory t = territories.get(Integer.parseInt(parts[0]) - 1);
                        for (int i = 1; i < parts.length; i++) {
                            t.addNeighbor(territories.get(Integer.parseInt(parts[i]) - 1));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid(String mapFile) {
        // Correct path relative to the resources folder
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LoadingMaps/canada.map");

        if (inputStream == null) {
            System.err.println("File not found: LoadingMaps/canada.map");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;

            // Validate [continents] section
            while ((line = reader.readLine()) != null && !line.equals("[continents]")) {}
            if (line == null) return false;

            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] parts = line.split(" ");
                if (parts.length < 2 || !parts[1].matches("\\d+")) return false;
            }

            // Validate [countries] section
            if ((line = reader.readLine()) == null || !line.equals("[countries]")) return false;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                String[] parts = line.split(" ");
                if (parts.length < 3 || !parts[0].matches("\\d+") || !parts[2].matches("\\d+")) return false;
            }

            // Validate [borders] section
            if ((line = reader.readLine()) == null || !line.equals("[borders]")) return false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 1 || !parts[0].matches("\\d+")) return false; // Allow empty neighbor lists
            }
        } catch (IOException e) {
            e.printStackTrace();  // Log the error to see what went wrong
            return false;
        }
        return true;
    }




}
