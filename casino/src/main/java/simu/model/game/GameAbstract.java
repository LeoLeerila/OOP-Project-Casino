package simu.model.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import simu.model.NPC;
/**
 * GameAbstract handles game logic and updates NPC chips
 */
public abstract class GameAbstract implements Game {
    private String type;
    private int gameTime;
    private int currentGameTime;
    private int maxPlayers;
    private int currentPlayerNum;
    private int totalPlayers;
    private int minBet;
    private double cashOutMult;
    private double winChance;
    private ConcurrentLinkedQueue<NPC> playerQueue;
    private ArrayList<NPC> currentPlayers;
    private boolean gameInProgress;

    public GameAbstract(String type, int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        this.type = type;
        this.gameTime = gameTime;
        this.maxPlayers = maxPlayers;
        this.minBet = minBet;
        this.cashOutMult = cashOutMult;
        this.winChance = winChance;
        currentPlayerNum = 0;
        totalPlayers = 0;
        playerQueue = new ConcurrentLinkedQueue<NPC>();
        currentPlayers = new ArrayList<NPC>(0);
        gameInProgress = false;
    }
    /**
     * @return type (String)
     */
    public String getType(){
        return type;
    }
    /**
     * @return maxPlayers (int)
     */
    public int getMaxPlayers(){
        return maxPlayers;
    }
    /**
     * @return currentPlayerNum (int)
     */
    public int getCurrentPlayerNum(){
        return currentPlayerNum;
    }
    /**
     * @return totalPlayers (int)
     */
    public int getTotalPlayers(){
        return totalPlayers;
    }
    /**
     * @return minBet (int)
     */
    public int getMinBet(){
        return minBet;
    }
    /**
     * @return gameTime (int)
     */
    public int getGameTime(){
        return gameTime;
    }
    /**
     * @return currentGameTime (int)
     */
    public int getCurrentGameTime(){
        return currentGameTime;
    }
    /**
     * @param player (NPC)
     * @return playerIndex (int)
     */
    public int getPlayerIndexFromCurrentPlayers(NPC player){
        return currentPlayers.indexOf(player);
    }
    /**
     * @return playerQueue size (int)
     */
    public int getPlayerQueueSize(){
        return playerQueue.size();
    }
    /**
     * @return cashOutMult (double)
     */
    public double getCashOut(){
        return cashOutMult;
    }
    /**
     * @return winChance (double)
     */
    public double getWinChance(){
        return winChance;
    }
    /**
     * @return playerQueue (ConcurrentLinkedQueue<NPC>)
     */
    public ConcurrentLinkedQueue<NPC> getPlayerQueue(){
        return playerQueue;
    }
    /**
     * @return currentPlayers (ArrayList<NPC>)
     */
    public ArrayList<NPC> getCurrentPlayers(){
        return currentPlayers;
    }
    /**
     * @return gameInProgress (boolean)
     */
    public boolean isGameInProgress(){
        return gameInProgress;
    }
    /**
     * Set game to be running, move players from queue to current players and set bets to players
     */
    public void startGame(){
        currentGameTime = gameTime;
        fillCurrentPlayersFromQueue();
        // TODO: loop all players, call getPlayerBet method 
        for (NPC player : currentPlayers){
            player.setBet(minBet);
        }
        gameInProgress = true;
    }
    /**
     * Set game to not be running, calculate which players won the game and add chips to those players, remove players from current players
     */
    public void stopGame(){
        gameInProgress = false;
        // TODO: loop all players, if rng > winChance, call getCurrentBet method, call setPlayerMoney method
        for (NPC player : currentPlayers) {
            if (Math.random() < winChance) {
                player.addChips((int) Math.round(player.getBet() * cashOutMult));
            }
            player.toggleInGame();
        }
        while (currentPlayers.size() > 0) {
            removePlayerFromCurrentPlayers(currentPlayers.get(0));
        }
    }
    /**
     * @param maxPlayers (int)
     */
    public void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }
    /**
     * @param currentPlayers (int)
     */
    public void setCurrentPlayerNum(int currentPlayerNum){
        this.currentPlayerNum = currentPlayerNum;
    }
    /**
     * @param totalPlayers (int)
     */
    public void setTotalPlayers(int totalPlayers){
        this.totalPlayers = totalPlayers;
    }
    /**
     * @param minBet (int)
     */
    public void setMinBet(int minBet){
        this.minBet = minBet;
    }
    /**
     * @param timeChange (int)
     */
    public void updateCurrentGameTime(int timeChange){
        this.currentGameTime += timeChange;
    }
    /**
     * @param cashOutMult (double)
     */
    public void setCashOut(double cashOutMult){
        this.cashOutMult = cashOutMult;
    }
    /**
     * @param winChance (double)
     */
    public void setWinChance(double winChance){
        this.winChance = winChance;
    }
    /**
     * @param player (NPC)
     */
    public void addPlayerToQueue(NPC player){
        playerQueue.add(player);
    }
    /**
     * @param player (NPC)
     */
    public void addPlayerToCurrentPlayers(NPC player){
        currentPlayers.add(player);
        currentPlayerNum++;
    }
    /**
     * Move players from playerQueue to currenPlayers
     */
    public void fillCurrentPlayersFromQueue(){
        while (currentPlayerNum < maxPlayers && playerQueue.size() > 0) {
            addPlayerToCurrentPlayers(removePlayerFromQueue());
        }
    }
    /**
     * remove player from queue
     * @return player (NPC)
     */
    public NPC removePlayerFromQueue(){
        return playerQueue.poll();
    }
    /**
     * @param player (NPC)
     */
    public void removePlayerFromCurrentPlayers(NPC player){
        currentPlayers.remove(player);
        currentPlayerNum--;
    }

    @Override
    public String toString(){
        return "game: "+ type +", maxPlayers: "+ maxPlayers +", currentPlayerNum: " + currentPlayerNum + ", playerQueue size: " + playerQueue.size() +", totalPlayers: "+ totalPlayers +", cashOutMult: "+ cashOutMult +", winChance: "+ winChance + ".";
    }
}
