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

public class LogEntry {
    private static final int DEFAULT_ID = -1;
    private int id;
    private long timestamp;
    private IReading reading;

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

    public LogEntry(long timestamp, IReading reading) {
        this(DEFAULT_ID, timestamp, reading);
    }

    public LogEntry(int id, long timestamp, IReading reading) {
        this.id = id;
        this.timestamp = timestamp;
        this.reading = reading;
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

    public IReading getReading() {
        return reading;
    }

    public void setReading(IReading reading) {
        this.reading = reading;
    }
}
