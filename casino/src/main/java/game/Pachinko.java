package game;

public class Pachinko extends GameAbstract {
    public Pachinko(int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        super("Pachinko", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
