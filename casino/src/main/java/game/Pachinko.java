package game;

public class Pachinko extends GameAbstract {
    public Pachinko(int maxPlayers, double minBet, double cashOutMult, double winChance, int gameTime){
        super("Pachinko", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
