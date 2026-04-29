package game;

public class Bingo extends GameAbstract {
    public Bingo(int maxPlayers, int minBet, double cashOutMult, double winChance, int gameTime){
        super("Bingo", maxPlayers, minBet, cashOutMult, winChance, gameTime);
    }
}
