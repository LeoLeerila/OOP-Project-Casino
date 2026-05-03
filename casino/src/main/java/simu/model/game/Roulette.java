package simu.model.game;

public class Roulette extends GameAbstract {
    public Roulette(int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        super("Roulette", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
