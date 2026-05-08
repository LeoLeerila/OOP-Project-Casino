package simu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import simu.model.game.GameAbstract;

public class Casino extends Thread {
    private double totalRevenue;
    private ConcurrentLinkedQueue<Double> casinoRevenueChange;
    private double totalLoaned;
    private ConcurrentLinkedQueue<Double> casinoLoanedChange;
    private int totalPlayers;
    //private ArrayList<NPC> players;
    private ConcurrentLinkedQueue<NPC> players;
    private List<GameAbstract> games;

    private List<NPC> playersThatLeft; //to see what players left

    //this is for the thing to make this into a thread
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public Casino(){
        totalRevenue = 0.0;
        totalLoaned = 0.0;
        totalPlayers = 0;
        casinoRevenueChange = new ConcurrentLinkedQueue<Double>();
        casinoLoanedChange = new ConcurrentLinkedQueue<Double>();
        players = new ConcurrentLinkedQueue<NPC>();
        games = Collections.synchronizedList(new ArrayList<GameAbstract>());;
        playersThatLeft = Collections.synchronizedList(new ArrayList<NPC>());
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

    public synchronized ConcurrentLinkedQueue<NPC> getCurrentPlayers(){
        return players;
    }

    public List<GameAbstract> getGames(){
        return games;
    }

    public void addRevenueChange(double amount){
        casinoRevenueChange.add(amount);
    }

    public void calculateRevenueChange(){
        while (!casinoRevenueChange.isEmpty()) {
            var value = casinoRevenueChange.poll();
            if (value == null){
                System.out.println("VITTU SAATANA, value tyhjä");
            }else {
                System.out.println("Money change: " + value);
                totalRevenue += value;
            }
        }
    }

    public void addLoanedChange(double amount){
        casinoLoanedChange.add(amount);
    }

    public void calculateLoanedChange(){
        while (!casinoLoanedChange.isEmpty()) {
            var value = casinoLoanedChange.poll();
            if (value == null){
                System.out.println("VITTU SAATANA, value tyhjä");
            }else {
                totalLoaned += value;
            }
        }
    }
    public synchronized NPC addPlayer(int minNPCMoney, int maxNPCMoney, int ChipConvesion){
        NPC player = new NPC(minNPCMoney, maxNPCMoney, ChipConvesion, getGames());
        addRevenueChange(player.getChipsToMoney());
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

    public synchronized List<NPC> getPlayersThatLeft() {
        return playersThatLeft; // temp arraylist to return
    }
    public synchronized void clearPlayersThatLeft(){
        if(!playersThatLeft.isEmpty())
            playersThatLeft.clear(); //clear all players
    }

    public void removeGame(GameAbstract game){
        games.remove(game);
    }

    public synchronized void updateGameTimes(int change){
        for (GameAbstract game : games){
            game.updateCurrentGameTime(change);
            if (game.getCurrentGameTime() <= 0) {
                game.stopGame();
            }
        }
    }

    public synchronized void startGames(){
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
        List<NPC> freePlayers = Collections.synchronizedList(new ArrayList<NPC>());
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


    @Override
    public void run() {//one big pain in the ass
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        System.err.println(e);
                        break;
                    }
                    if (!running) {
                        break;
                    }
                }
            }
            clearPlayersThatLeft();
            handleFreePlayers();
            startGames();
            calculateRevenueChange();
            calculateLoanedChange();
            updateGameTimes(-1); //-1*getLowestGameTime()
            pause();
        }
    }
    public void stopCopy() {
        running = false;
        resumeCopy();
    }
    public void pause() {
        paused = true;
    }
    public void resumeCopy() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify();
        }
    }
    public boolean getIsPaused(){return paused;};
}
