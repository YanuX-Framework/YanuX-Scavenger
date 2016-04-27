package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import java.util.ArrayList;
import java.util.List;

public class LogSession {
    private long timestamp;
    private List<LogEntry> entries;

    public LogSession() {
        this(System.currentTimeMillis());
    }

    public LogSession(long timestamp) {
        this(timestamp, new ArrayList<LogEntry>());
    }

    public LogSession(long timestamp, List<LogEntry> entries) {
        this.timestamp = timestamp;
        this.entries = entries;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<LogEntry> entries) {
        this.entries = entries;
    }
}
