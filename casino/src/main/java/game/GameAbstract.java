package game;

import java.util.ArrayList;
import java.util.LinkedList;

import npc.NPC;

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
    private LinkedList<NPC> playerQueue;
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
        playerQueue = new LinkedList<NPC>();
        currentPlayers = new ArrayList<NPC>(0);
        gameInProgress = false;
    }

    public String getType(){
        return type;
    }

    public int getMaxPlayers(){
        return maxPlayers;
    }

    public int getCurrentPlayerNum(){
        return currentPlayerNum;
    }

    public int getTotalPlayers(){
        return totalPlayers;
    }

    public int getMinBet(){
        return minBet;
    }

    public int getGameTime(){
        return gameTime;
    }

    public int getCurrentGameTime(){
        return currentGameTime;
    }

    public int getPlayerIndexFromCurrentPlayers(NPC player){
        return currentPlayers.indexOf(player);
    }

    public int getPlayerQueueSize(){
        return playerQueue.size();
    }

    public double getCashOut(){
        return cashOutMult;
    }

    public double getWinChance(){
        return winChance;
    }

    public LinkedList<NPC> getPlayerQueue(){
        return playerQueue;
    }

    public ArrayList<NPC> getCurrentPlayers(){
        return currentPlayers;
    }

    public boolean isGameInProgress(){
        return gameInProgress;
    }

    public void startGame(){
        currentGameTime = gameTime;
        fillCurrentPlayersFromQueue();
        gameInProgress = true;
    }

    public void stopGame(){
        gameInProgress = false;
        
    }

    public void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    public void setCurrentPlayerNum(int currentPlayerNum){
        this.currentPlayerNum = currentPlayerNum;
    }

    public void setTotalPlayers(int totalPlayers){
        this.totalPlayers = totalPlayers;
    }

    public void setMinBet(int minBet){
        this.minBet = minBet;
    }

    public void updateCurrentGameTime(int timeChange){
        this.currentGameTime += timeChange;
    }

    public void setCashOut(double cashOutMult){
        this.cashOutMult = cashOutMult;
    }

    public void setWinChance(double winChance){
        this.winChance = winChance;
    }

    public void addPlayerToQueue(NPC player){
        playerQueue.add(player);
    }

    public void addPlayerToCurrentPlayers(NPC player){
        currentPlayers.add(player);
        currentPlayerNum++;
    }

    public void fillCurrentPlayersFromQueue(){
        while (currentPlayerNum < maxPlayers && playerQueue.size() > 0) {
            addPlayerToCurrentPlayers(removePlayerFromQueue());
        }
    }

    public NPC removePlayerFromQueue(){
        return playerQueue.removeFirst();
    }

    public NPC removePlayerFromCurrentPlayers(int index){
        NPC removedPlayer = currentPlayers.remove(index);
        currentPlayerNum--;
        fillCurrentPlayersFromQueue();
        return removedPlayer;
    }
}
