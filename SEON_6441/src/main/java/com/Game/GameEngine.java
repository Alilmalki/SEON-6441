//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package com.Game;

public class GameEngine {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Welcome to Warzone Game!");

        //TESTING VALID MAP FILES
        String path = "LoadingMaps/";
        String fileName = "canada.map";
        String fileName2 = "Not_Map.map";

        //-------------------------------------------t1-------------------------------------------
        MapLoader test1 = new MapLoader();
        if (test1.isValid(path + fileName)) {
            System.out.println(fileName + " is a valid map.");
            System.out.println("Reading file: " + fileName + "\n");
            test1.read(path + fileName);
            System.out.println(test1.getLoadedMap());


            System.out.println("==============================================================================> "  + "\n");
            Map testMap = test1.getLoadedMap();

            // Adding continents
            testMap.addContinent("Asia", 6);
            testMap.addContinent("Europe", 7);


            // Adding countries
            testMap.addCountry("India", "Asia");
            testMap.addCountry("China", "Asia");
            testMap.addCountry("France", "Europe");

            // Adding neighbors
            testMap.addNeighbor("India", "China");
            testMap.addNeighbor("France", "China");

            String editedFileName = "edited_" + fileName;
            testMap.saveToFile(path + editedFileName);
            System.out.println(testMap);

            System.out.println("----------------------------------------------------------------------> "  + "\n");

            if (test1.isValid(path + editedFileName))
            {
                test1.read(path + editedFileName);
                System.out.println(test1.getLoadedMap());
                System.out.println("done reading the edited file"  + "\n");
            }
        } else {
            System.out.println(fileName + " is an invalid map.");
            System.out.println("Cannot read file: " + fileName + "\n");
        }

    }
}