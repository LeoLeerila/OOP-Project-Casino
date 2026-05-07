package simu.model;

import java.util.ArrayList;
import java.util.LinkedList;

import simu.model.game.GameAbstract;

public class Casino {
    private double totalRevenue;
    private LinkedList<Double> casinoRevenueChange;
    private double totalLoaned;
    private LinkedList<Double> casinoLoanedChange;
    private int totalPlayers;
    private ArrayList<NPC> players;
    private ArrayList<GameAbstract> games;

    private ArrayList<NPC> playersThatLeft; //to see what players left

    public Casino(){
        totalRevenue = 0.0;
        totalLoaned = 0.0;
        totalPlayers = 0;
        casinoRevenueChange = new LinkedList<Double>();
        casinoLoanedChange = new LinkedList<Double>();
        players = new ArrayList<NPC>();
        games = new ArrayList<GameAbstract>();
        playersThatLeft = new ArrayList<NPC>();
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
    public NPC addPlayer(NPC player){
        addRevenueChange(player.getMoney());
        players.add(player);
        totalPlayers++;
        return player;
    }

    public void addGame(GameAbstract game){
        games.add(game);
    }

    public void removePlayer(NPC player){
        addRevenueChange(-1 * player.getChipsToMoneyFinal());
        //player.setRemovalTime(time);
        playersThatLeft.add(player);
        players.remove(player);

    }

    public ArrayList<NPC> getPlayersThatLeft() {
        return playersThatLeft; // temp arraylist to return
    }
    public void clearPlayersThatLeft(){
        if(!playersThatLeft.isEmpty())
            playersThatLeft.clear(); //clear all players
    }

    public void removeGame(GameAbstract game){
        games.remove(game);
    }

    public void updateGameTimes(int change){
        for (GameAbstract game : games){
            game.updateCurrentGameTime(change);
            if (game.getCurrentGameTime() <= 0) {
                game.stopGame();
            }
        }
    }

    public void startGames(){
        for (GameAbstract game : games) {
            if (!game.isGameInProgress()) {
                game.startGame();
            }
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

    public void handleFreePlayers(){
        ArrayList<NPC> freePlayers = new ArrayList<>();
        for (NPC player : players) {
            if (!player.IsInGame()) {
                freePlayers.add(player);
            }
        }
        for (NPC player : freePlayers) {
            if (player.getChipsTarget() < player.getChips()){
                removePlayer(player);
            }else {
                ArrayList<GameAbstract> availableGames = new ArrayList<>();
                for (GameAbstract game : games) {
                    if (game.getPlayerQueueSize() < game.getMaxPlayers() && (game.getMinBet() < player.getChips() || tryTakeLoan(player, (int) (Math.round(game.getMinBet() * 1.2))))) {
                        availableGames.add(game);
                    }
                }

                for (GameAbstract game : availableGames) {
                    if (game.getType() == player.getGamePreference() && !player.IsInGame()) {
                        game.addPlayerToQueue(player);
                        player.toggleInGame();
                    }
                }
                if (!player.IsInGame() && availableGames.size() > 0) {
                    availableGames.get(0).addPlayerToQueue(player);
                    player.toggleInGame();
                } else if (!player.IsInGame()) {
                    removePlayer(player);
                }
            }
        }
    }
    public Boolean tryTakeLoan(NPC player, int amount){
        if(Math.random() > player.getCasinoLoanWill()){
            player.addChipsLoaned(amount);
            addLoanedChange(player.getChipsToMoney());
            return true;
        }else{
            return false;
        }
    }
}
