package simu.model.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import simu.model.NPC;

interface Game {
    String getType();
    int getMaxPlayers();
    int getCurrentPlayerNum();
    int getTotalPlayers();
    int getMinBet();
    int getGameTime();
    int getCurrentGameTime();
    int getPlayerIndexFromCurrentPlayers(NPC player);
    int getPlayerQueueSize();
    double getCashOut();
    double getWinChance();
    ConcurrentLinkedQueue<NPC> getPlayerQueue();
    ArrayList<NPC> getCurrentPlayers();
    boolean isGameInProgress();
    void startGame();
    void stopGame();
    void setMaxPlayers(int maxPlayers);
    void setCurrentPlayerNum(int currentPlayerNum);
    void setTotalPlayers(int totalPlayers);
    void setMinBet(int minBet);
    void updateCurrentGameTime(int timeChange);
    void setCashOut(double cashOutMult);
    void setWinChance(double winChance);
    void addPlayerToQueue(NPC player);
    void addPlayerToCurrentPlayers(NPC player);
    void fillCurrentPlayersFromQueue();
    NPC removePlayerFromQueue();
    void removePlayerFromCurrentPlayers(NPC player);
    
}
