/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class JsonFileLogger extends AbstractFileLogger {
    public static final String DEFAULT_FILENAME = "log.json";
    private static final String LOG_TAG = Constants.LOG_TAG + "_JSON_LOGGER";

    private final ObjectMapper mapper;
    private LogFile logFile;
    private LogSession currentLogSession;

    public JsonFileLogger() {
        this(DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    public JsonFileLogger(String filename) {
        this(DEFAULT_DIRECTORY, filename);
    }

    public JsonFileLogger(String directory, String filename) {
        super(directory, filename);
        mapper = new ObjectMapper();
    }

    @Override
    public void open() throws IOException {
        super.open();
        try {
            logFile = mapper.readValue(file, LogFile.class);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
            logFile = new LogFile(filename);
        }
        //Add new session
        currentLogSession = new LogSession();
        logFile.getSessions().add(currentLogSession);
    }

    @Override
    public void log(int id, IReading loggable) {
        currentLogSession.getEntries().add(new LogEntry(id, System.currentTimeMillis(), loggable));
    }

    @Override
    public void log(IReading loggable) {
        log(loggable);
    }

    @Override
    public void close() throws IOException {
        super.close();
        currentLogSession = null;
        mapper.writeValue(file, logFile);
    }
}
