package simu.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import simu.model.game.GameAbstract;
/**
 * Casino is the manager model of the simulation that runs on it's own thread.
 * */

public class Casino extends Thread {
    private double totalRevenue;
    private LinkedList<Double> casinoRevenueChange;
    private double totalLoaned;
    private LinkedList<Double> casinoLoanedChange;
    private int totalPlayers;
    //private ArrayList<NPC> players;
    private List<NPC> players;
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
        casinoRevenueChange = new LinkedList<Double>();
        casinoLoanedChange = new LinkedList<Double>();
        players = Collections.synchronizedList(new ArrayList<NPC>());
        games = Collections.synchronizedList(new ArrayList<GameAbstract>());;
        playersThatLeft = Collections.synchronizedList(new ArrayList<NPC>());
    }
    /**
     * @return totalRevenue (double)
     * */
    public double getTotalRevenue(){
        return totalRevenue;
    }
    /**
     * @return totalLoaned (double)
     * */
    public double getTotalLoaned(){
        return totalLoaned;
    }
    /**
     * @return totalPlayers (int)
     * */
    public double getTotalPlayers(){
        return totalPlayers;
    }
    /**
     * @return synchronized list of players
     * */
    public synchronized List<NPC> getCurrentPlayers(){
        return players;
    }
    /**
     * @return synchronized list of initialised games
     * */
    public List<GameAbstract> getGames(){
        return games;
    }
    /**
     * Adds the new changes to revenue to a Linked list
     * @param amount (double)
     * */
    public void addRevenueChange(double amount){
        casinoRevenueChange.add(amount);
    }
    /**
     * Calculates the total revenue change by adding all the changes in the Linked list to the total revenue and then clearing the Linked list
     * */
    public void calculateRevenueChange(){
        while (casinoRevenueChange.size() > 0) {
            totalRevenue += casinoRevenueChange.removeFirst();
        }
    }
    /**
     * Adds the new changes to loans to a Linked list
     * @param amount (double)
     * */
    public void addLoanedChange(double amount){
        casinoLoanedChange.add(amount);
    }
    /**
     * Calculates the total loaned change by adding all the changes in the
     * Linked list to the total loaned and then clearing the Linked list
     * */
    public void calculateLoanedChange(){
        while (casinoLoanedChange.size() > 0) {
            totalLoaned += casinoLoanedChange.removeFirst();
        }
    }
    /**
     * synchronized method for adding new players to the simulation
     * @param player (NPC)
     * */
    public synchronized NPC addPlayer(NPC player){
        addRevenueChange(player.getMoney());
        players.add(player);
        totalPlayers++;
        return player;
    }
    /**
     * Adds new game
     * @param game (GameAbstract)
     * */
    public void addGame(GameAbstract game){
        games.add(game);
    }
    /**
     * removes a specified player
     * @param player (NPC)
     * */
    public void removePlayer(NPC player){
        addRevenueChange(-1 * player.getChipsToMoneyFinal());
        //player.setRemovalTime(time);
        playersThatLeft.add(player);
        players.remove(player);

    }
    /**
     * @return playersThatLeft (List of NPCs)
     * */
    public synchronized List<NPC> getPlayersThatLeft() {
        return playersThatLeft; // temp arraylist to return
    }
    /**
     * synchronized method to clear the list of players that left the casino
     * */
    public synchronized void clearPlayersThatLeft(){
        if(!playersThatLeft.isEmpty())
            playersThatLeft.clear(); //clear all players
    }
    /**
     * removes game
     * @param game (GameAbstract)
     * */
    public void removeGame(GameAbstract game){
        games.remove(game);
    }
    /**
     * Updates the game times on game tables and progresses the game.
     * @param change (int)
     * */
    public void updateGameTimes(int change){
        for (GameAbstract game : games){
            game.updateCurrentGameTime(change);
            if (game.getCurrentGameTime() <= 0) {
                game.stopGame();
            }
        }
    }
    /**
     * Starts the events in game tables.
     * */
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
    /**
     * Handles the free players by checking if they are in a game, if not it checks
     * if they have enough chips to play or if they are willing to take a loan, then
     * it checks for available games and adds them to the queue of the game
     * they prefer, if there are no available games it removes them from the
     * simulation.
     * */
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
    /**
     * Checker for loan taking.
     * @param player, amount (NPC, int)
     * @return true if the player takes the loan, false if not
     * */
    public Boolean tryTakeLoan(NPC player, int amount){
        if(Math.random() > player.getCasinoLoanWill()){
            player.addChipsLoaned(amount);
            addLoanedChange(player.getChipsToMoney());
            return true;
        }else{
            return false;
        }
    }

    /**
     * The main loop of the casino thread, it handles the free players,
     * starts the games, calculates the revenue and loaned changes, updates
     * the game times and then pauses the thread until it is resumed again.
     * */
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
            handleFreePlayers();
            startGames();
            calculateRevenueChange();
            calculateLoanedChange();
            updateGameTimes(-1*getLowestGameTime());
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
            pauseLock.notifyAll();
        }
    }
    public boolean getIsPaused(){return paused;};
}
