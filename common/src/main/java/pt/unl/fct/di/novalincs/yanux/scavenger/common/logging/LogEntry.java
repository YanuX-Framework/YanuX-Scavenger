package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

public class LogEntry {
    private static final int DEFAULT_ID = -1;
    private int id;
    private long timestamp;
    private ILoggable loggable;

    public LogEntry() {
        this(DEFAULT_ID, System.currentTimeMillis());
    }

    public LogEntry(int id) {
        this(id, System.currentTimeMillis());
    }

    public LogEntry(long timestamp) {
        this(DEFAULT_ID, timestamp, null);
    }

    public LogEntry(int id, long timestamp) {
        this(id, timestamp, null);
    }

    public LogEntry(long timestamp, ILoggable loggable) {
        this(DEFAULT_ID, timestamp, loggable);
    }

    public LogEntry(int id, long timestamp, ILoggable loggable) {
        this.id = id;
        this.timestamp = timestamp;
        this.loggable = loggable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ILoggable getLoggable() {
        return loggable;
    }

    public void setLoggable(ILoggable loggable) {
        this.loggable = loggable;
    }
}
