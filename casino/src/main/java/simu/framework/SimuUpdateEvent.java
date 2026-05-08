package simu.framework;
/**
 * This is a class that enables communication between Engine and Controller, while being
 * a separated class from IControllerMtoV and so on to ensure thread safety.
 * */
//For updating the UI visuals (mainly to stay thread safe and have one class handle changes)
public class SimuUpdateEvent implements ISimuUpdate {
    /**
     * ENUM types to define the type of data being handed over.
     * */
    public enum Type {
        PLAYER_ADDED,
        PLAYER_REMOVED,
        GAME_STARTED,
        GAME_ENDED,
        STATISTICS_UPDATE,
        TIME_ADVANCED
    }
    private final Type type;
    private final long timestamp;
    private final Object data;

    public SimuUpdateEvent(Type type, Object data) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
    }

    public Type getType(){ return type; }
    public long getTimestamp() { return timestamp; }
    public Object getData() { return data; }
}
