/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
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

import android.content.Context;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.StorageType;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;

public class JsonSreamFileLogger extends AbstractFileLogger {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + JsonSreamFileLogger.class.getSimpleName();
    private final JsonFactory jsonFactory;
    private final ObjectMapper objectMapper;
    private JsonGenerator jsonGenerator;
    private long entryIdCounter;


    public JsonSreamFileLogger(Context context, String directory, String filename, StorageType storageType) {
        super(context, directory, filename, storageType);
        jsonFactory = new JsonFactory();
        objectMapper = new ObjectMapper();
        entryIdCounter = 0;
    }

    public JsonSreamFileLogger(Context context, String filename, StorageType storageType) {
        this(context, DEFAULT_DIRECTORY, filename, storageType);
    }

    public JsonSreamFileLogger(Context context, String directory, String filename) {
        this(context, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public JsonSreamFileLogger(Context context, String filename) {
        this(context, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public JsonSreamFileLogger(Context context) {
        this(context, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }

    public JsonSreamFileLogger(String directory, String filename, StorageType storageType) {
        this(null, directory, filename, storageType);
    }

    public JsonSreamFileLogger(String directory, String filename) {
        this(null, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public JsonSreamFileLogger(String filename, StorageType storageType) {
        this(null, DEFAULT_DIRECTORY, filename, storageType);
    }

    public JsonSreamFileLogger(String filename) {
        this(null, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public JsonSreamFileLogger() {
        this(null, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }

    @Override
    public void open() throws IOException {
        super.open();
        jsonGenerator = jsonFactory.createJsonGenerator(new FileOutputStream(file));
        jsonGenerator.setCodec(objectMapper);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("timestamp", Utilities.getUnixTimeMillis());
        jsonGenerator.writeStringField("name", getFilename());
        jsonGenerator.writeObjectFieldStart("sessions");
        jsonGenerator.writeArrayFieldStart("entries");
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (jsonGenerator != null && !jsonGenerator.isClosed()) {
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
            jsonGenerator.close();
        }
    }

    @Override
    public void log(long id, IReading IReading) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", id);
        jsonGenerator.writeObjectField("reading", IReading);
        jsonGenerator.writeEndObject();
    }

    @Override
    public void log(IReading IReading) throws IOException {
        log(entryIdCounter++, IReading);
    }
}
