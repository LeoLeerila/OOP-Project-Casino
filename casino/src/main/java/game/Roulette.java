package game;

public class Roulette extends GameAbstract {
    public Roulette(int maxPlayers, double minBet, double cashOutMult, double winChance, int gameTime){
        super("Roulette", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
