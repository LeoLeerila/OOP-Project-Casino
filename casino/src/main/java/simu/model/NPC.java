package simu.model;

import simu.framework.Clock;
import simu.model.game.GameAbstract;

import java.util.ArrayList;
import java.util.List;

public class NPC {
    private static int _id;
    private int id;
    private double arrivalTime;
    private double removalTime;

    private int convesion;
    private double money;
    private int chips; // & fish
    private int chipsTarget;
    private int casinoLoan = 0;
    private double casinoLoanWill;
    private int totalGamesPlayed;
    private int differentGamesPlayed;
    private String gamePreference;
    private ArrayList<String> differentGames = new ArrayList<>();
    private boolean isInGame;
    private int lastBet;

    public NPC(int minMoney,int maxMoney, int moneyChange, List<GameAbstract> gameList){
        this.convesion = moneyChange;
        this.money = (int) Math.round(Math.random() * (maxMoney-minMoney+1)) + minMoney;
        this.chips = (int) Math.round(this.money / convesion);
        this.casinoLoanWill = Math.random();
        this.chipsTarget = (int) Math.round(this.chips+((this.chips * Math.random())*2));
        this.gamePreference = gameList.get((int) (Math.random() * (gameList.size()))).getType();
        this.id = _id++;
        this.arrivalTime = Clock.getInstance().getTime();

    }

    public void casinoLoan(int amount){
        chips += amount;casinoLoan += amount;
    }
    public void GamePlayed(String gameName){
        if (!differentGames.contains(gameName)){
            differentGames.add(gameName);
            differentGamesPlayed++;
        }
        this.totalGamesPlayed++;
    }
    /**
     * Sets the bet for the NPC based on a random value between the minimum bet and the chips target.
     * @param minBet (int)
     * */
    public void setBet(int minBet){
        int fish = (int) Math.round(minBet+(Math.random()*(chipsTarget)));
        if (fish > chips){ //heh
            this.lastBet = chips;
        }else {
            this.lastBet = fish;
        }
        chips -= lastBet;
    }
    public void addChips(int chips){
        this.chips += chips;
    }
    public void addChipsLoaned(int chips){
        this.chips += chips;this.casinoLoan += chips;
    }
    public void toggleInGame(){
        isInGame = !isInGame;
    }
    public void setArrivalTime(double arrivalTime) {this.arrivalTime = arrivalTime;}
    public void setRemovalTime(double removalTime) {this.removalTime = removalTime;}
    @Override
    public String toString(){
        return "NPC: "+id+", Money: "+money+", Chips&Target: "+chips+"/"+chipsTarget+"("+convesion+")"+", CasinoWill: "+casinoLoanWill+"%, GamePreference: "+gamePreference + ".";
    }
    //get wall
    /**
     * Getters for the NPC class, used to access the private variables of the class. These are used in the controller and view to
     * display the NPC's information and to make decisions based on the NPC's state.
     * */
    public int getCasinoLoan() {return casinoLoan;}
    public double getCasinoLoanWill() {return casinoLoanWill;}
    public int getDifferentGamesPlayed() {return differentGamesPlayed;}
    public double getMoney() {return money;}
    public int getChips() {return chips;}
    public int getTotalGamesPlayed() {return totalGamesPlayed;}
    public String getGamePreference() {return gamePreference;}
    public int getChipsTarget() {return chipsTarget;}
    public int getId() {return id;}
    public boolean IsInGame(){return isInGame;}
    public double getChipsToMoney(){return chips * convesion;}
    public int getBet(){return lastBet;}
    public double getChipsToMoneyFinal(){return getChipsToMoney() - casinoLoan;}
    public double getRemovalTime() {return removalTime;}
    public double getArrivalTime() {return arrivalTime;}
}