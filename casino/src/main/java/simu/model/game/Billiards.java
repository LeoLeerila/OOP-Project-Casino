package simu.model.game;

public class Billiards extends GameAbstract {
    public Billiards(int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        super("Billiards", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
