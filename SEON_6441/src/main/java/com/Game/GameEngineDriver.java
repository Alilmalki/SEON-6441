package com.Game;

/**
 * The {@code GameEngineDriver} class serves as the entry point for the game.
 * It creates an instance of {@code GameEngine} and starts the game.
 */
public class GameEngineDriver {

    /**
     * The main method to launch the game.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
    }
}
