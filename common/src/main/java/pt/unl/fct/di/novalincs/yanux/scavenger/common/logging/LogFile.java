/*
 * Copyright (c) 2018 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

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
