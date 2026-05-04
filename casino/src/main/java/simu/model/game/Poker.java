package simu.model.game;

public class Poker extends GameAbstract {
    public Poker(int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        super("Poker", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
