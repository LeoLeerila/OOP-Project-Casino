package simu.model;

public class NPC {
    private static int _id;
    private int id;
    private double arrivalTime;
    private double removalTime;

    private double money;
    private int chips;
    private int chipsTarget;
    private int casinoLoan = 0;
    private int casinoLoanWill;
    private int totalGamesPlayed;
    private int differentGamesPlayed;
    private String gamePreference;
    private ArrayList<String> differentGames = new ArrayList<>();
    private boolean isInGame;

    public NPC(int minMoney,int maxMoney, int moneyChange, ArrayList<GameAbstract> gameList){
        this.money = (int) Math.round(Math.random() * (maxMoney-minMoney+1)) + minMoney;
        this.chips = (int) Math.round(this.money / moneyChange);
        this.casinoLoanWill = (int) Math.round(Math.random() * 100) + 1;
        this.chipsTarget = (int) Math.round(this.chips+((this.chips * Math.random())*2));
        this.gamePreference = String.valueOf(gameList.get((int) (Math.random() * (gameList.size()))));
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
    public void toggleInGame(){
        isInGame = !isInGame;
    }
    public void setArrivalTime(double arrivalTime) {this.arrivalTime = arrivalTime;}
    public void setRemovalTime(double removalTime) {this.removalTime = removalTime;}
    //get wall
    public int getCasinoLoan() {return casinoLoan;}
    public int getCasinoLoanWill() {return casinoLoanWill;}
    public int getDifferentGamesPlayed() {return differentGamesPlayed;}
    public double getMoney() {return money;}
    public int getChips() {return chips;}
    public int getTotalGamesPlayed() {return totalGamesPlayed;}
    public String getGamePreference() {return gamePreference;}
    public int getChipsTarget() {return chipsTarget;}
    public int getId() {return id;}
    public double getRemovalTime() {return removalTime;}
    public double getArrivalTime() {return arrivalTime;}
}