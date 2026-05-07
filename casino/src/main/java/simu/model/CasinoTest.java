package simu.model;

import java.util.ArrayList;

import simu.model.game.Blackjack;
import simu.model.game.GameAbstract;
// i made all this to comment cus i changed add mechanic and too lazy to fix here
//public class CasinoTest {
//    public static void main(String[] args) {
//        System.out.println("start");
//        Casino casino = new Casino();
//        ArrayList<GameAbstract> games = new ArrayList<GameAbstract>(10);
//        ArrayList<NPC> players = new ArrayList<NPC>(50);
//
//        for (int i = 0; i < 10; i++) {
//            games.add(new Blackjack(4,2,1.5,0.5,5));
//        }
//
//        for (int i = 0; i < 50; i++) {
//            players.add(new NPC(3000, 30000, 1, games));
//        }
//
//        for (GameAbstract game : games) {
//            casino.addGame(game);
//            System.out.println("add game to casino");
//        }
//
//        for (NPC player : players) {
//            casino.addPlayer(player);
//            System.out.println("add player to casino");
//        }
//
//        while (casino.getGames().size() > 0) {
//            System.out.println("current game num: " + casino.getGames().size());
//            //casino.handleFreePlayers();
//            for (int i = 0; i < 5; i++) {
//                casino.startGames();
//
//                System.out.println("lowering game time by: " + casino.getLowestGameTime());
//                casino.updateGameTimes(-1 * casino.getLowestGameTime());
//
//                casino.handleFreePlayers();
//                casino.calculateRevenueChange();
//                casino.calculateLoanedChange();
//                System.out.println("remaining players: " + casino.getCurrentPlayers().size());
//            }
//            casino.removeGame(games.get(0));
//            games.remove(0);
//        }
//        System.out.println("stop");
//    }
//}

