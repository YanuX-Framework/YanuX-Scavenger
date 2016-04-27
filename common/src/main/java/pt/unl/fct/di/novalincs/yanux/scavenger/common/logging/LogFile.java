package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import java.util.ArrayList;
import java.util.List;

public class LogFile {
    private String name;
    private long creationTimestamp;
    private List<LogSession> sessions;

    public LogFile() {
        this.sessions = new ArrayList<>();
    }

    public LogFile(String name) {
        this(name, System.currentTimeMillis());
    }

    public LogFile(String name, long creationTimestamp) {
        this(name, creationTimestamp, new ArrayList<LogSession>());
    }

    public LogFile(String name, long creationTimestamp, List<LogSession> sessions) {
        this.name = name;
        this.creationTimestamp = creationTimestamp;
        this.sessions = sessions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public List<LogSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<LogSession> sessions) {
        this.sessions = sessions;
    }
}
