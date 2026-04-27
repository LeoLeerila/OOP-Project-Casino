package casino;

import java.util.ArrayList;
import java.util.LinkedList;

import npc.NPC;
import game.GameAbstract;

public class Casino {
    private double totalRevenue;
    private LinkedList<Double> casinoRevenueChange;
    private double totalLoaned;
    private LinkedList<Double> casinoLoanedChange;
    private int totalPlayers;
    private ArrayList<NPC> players;
    private ArrayList<GameAbstract> games;

    public Casino(){
        totalRevenue = 0.0;
        totalLoaned = 0.0;
        totalPlayers = 0;
        casinoRevenueChange = new LinkedList<Double>();
        casinoLoanedChange = new LinkedList<Double>();
        players = new ArrayList<NPC>();
        games = new ArrayList<GameAbstract>();
    }

    public double getTotalRevenue(){
        return totalRevenue;
    }

    public double getTotalLoaned(){
        return totalLoaned;
    }

    public double getTotalPlayers(){
        return totalPlayers;
    }

    public ArrayList<NPC> getCurrentPlayers(){
        return players;
    }

    public ArrayList<GameAbstract> getGames(){
        return games;
    }

    public void addRevenueChange(double amount){
        casinoRevenueChange.add(amount);
    }

    public void calculateRevenueChange(){
        while (casinoRevenueChange.size() > 0) {
            totalRevenue += casinoRevenueChange.removeFirst();
        }
    }

    public void addLoanedChange(double amount){
        casinoLoanedChange.add(amount);
    }

    public void calculateLoanedChange(){
        while (casinoLoanedChange.size() > 0) {
            totalLoaned += casinoLoanedChange.removeFirst();
        }
    }

    public void addPlayer(NPC player){
        players.add(player);
        totalPlayers++;
    }

    public void addGame(GameAbstract game){
        games.add(game);
    }

    public void removePlayer(NPC player){
        players.remove(player);
    }

    public void removeGame(GameAbstract game){
        games.remove(game);
    }

    public void updateGameTimes(int change){
        for (int i = 0; i < games.size(); i++){
            games.get(i).updateCurrentGameTime(change);
        }
    }

    public int getLowestGameTime(){
        int lowestGameTime = Integer.MAX_VALUE;
        for (int i = 0; i < games.size(); i++){
            if (games.get(i).getCurrentGameTime() < lowestGameTime) {
                lowestGameTime = games.get(i).getCurrentGameTime();
            }
        }
        return lowestGameTime;
    }

    public ArrayList<GameAbstract> findGameByType(String type){
        ArrayList<GameAbstract> gamesByType = new ArrayList<GameAbstract>();
        for (GameAbstract game : games) {
            if (game.getType() == type) {
                gamesByType.add(game);
            }
        }
        return gamesByType;
    }
}
