package simu.model;

public class NPC {
    private static int _id;
    private int id;

    public NPC(){
        id = _id++;
    }

    public int getId(){
        return id;
    }
}
