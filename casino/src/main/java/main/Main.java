package main;

import java.util.ArrayList;

import game.*;
import npc.NPC;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        GameAbstract game = new Blackjack(4, 10, 1.5, 0.3, 10); // create new game
        // ^ all games can be stored in GameAbstract variables
        ArrayList<NPC> players = new ArrayList<NPC>(); // placeholder player class

        for (int i = 0; i < 10; i++) {
            players.add(new NPC()); // create players
        }

        for (int i = 0; i < players.size(); i++) {
            game.addPlayerToQueue(players.get(i)); // populate game queue
        }


        while (players.size() > 0){ // all players get to play the game once
            System.out.println("Players: " + players.size());
            System.out.println("Game in progress: " + game.isGameInProgress()); // indicates if a game is in progress
            game.startGame(); // moves players from playerQueue to currentPlayers then sets gameInProgress to true and currentGameTime to gameTime, gameTime set when creating the game
            System.out.println("Game in progress: " + game.isGameInProgress());
            System.out.println("Game time: " + game.getCurrentGameTime()); // current game time 
            game.updateCurrentGameTime(-5); // updates current game time
            System.out.println("Game time: " + game.getCurrentGameTime());
            game.updateCurrentGameTime(-5);
            System.out.println("Game time: " + game.getCurrentGameTime());
            game.stopGame(); // set gameInProgress to false in the game
            System.out.println("Game in progress: " + game.isGameInProgress());
            for (int i = 0; i < players.size(); i++) { // remove participating players from the game and casino after a game
                int playerIndex = -1;
                playerIndex = game.getPlayerIndexFromCurrentPlayers(players.get(i)); // find player in the game by player object, returns -1 if not found
                if (playerIndex != -1) {
                    game.removePlayerFromCurrentPlayers(playerIndex); // remove that player from the game
                    players.remove(i); // remove that player from the casino
                }
            }
        }
    }
}