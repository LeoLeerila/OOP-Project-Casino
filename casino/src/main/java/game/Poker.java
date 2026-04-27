package game;

public class Poker extends GameAbstract {
    public Poker(int maxPlayers, double minBet, double cashOutMult, double winChance, int gameTime){
        super("Poker", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
