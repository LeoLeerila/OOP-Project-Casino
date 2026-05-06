package simu.framework;

//For updating the UI visuals (mainly to stay thread safe and have one class handle changes)
public class SimuUpdateEvent implements ISimuUpdate {
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
