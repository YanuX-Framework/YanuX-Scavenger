/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class JsonLogger extends AbstractLogger {
    public static final String DEFAULT_FILENAME = "log.json";
    private final ObjectMapper mapper;
    private String name;
    private JsonNode rootNode;
    private ArrayNode longEntries;
    private Writer writer;

    public JsonLogger(String name, String directory, String filename) throws IOException {
        super(directory, filename);
        this.name = name;
        mapper = new ObjectMapper();
        open();
    }

    public JsonLogger(String name) throws IOException {
        this(name, DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    @Override
    public void open() throws IOException {
        if (isExternalStorageWritable()) {
            File file = new File(getExternalStoragePath());
            rootNode = mapper.createObjectNode();
            if (file.exists()) {
                try {
                    Reader reader = new FileReader(file);
                    rootNode = mapper.readTree(reader);
                    reader.close();
                } catch (IOException e) {
                }
            }
            writer = new FileWriter(file);
            ObjectNode obj = (ObjectNode) rootNode;
            obj.put("filename", filename);
            obj.put("creationTimestamp", System.currentTimeMillis());
            ArrayNode logsNode;
            if (obj.has("logs")) {
                logsNode = (ArrayNode) obj.get("logs");
            } else {
                logsNode = mapper.createArrayNode();
                obj.set("logs", logsNode);
            }
            ObjectNode log = mapper.createObjectNode();
            log.put("name", name);
            log.put("timestamp", System.currentTimeMillis());
            longEntries = mapper.createArrayNode();
            log.set("logEntries", longEntries);
            logsNode.add(log);
        } else {
            throw new IOException("External Storage is not writable");
        }
    }

    @Override
    public void log(ILoggable object) {
        longEntries.add(mapper.valueToTree(object));
    }

    @Override
    public void log(Iterable<ILoggable> objects) {
        for (ILoggable object : objects) {
            longEntries.add(mapper.valueToTree(object));
        }
    }

    @Override
    public void close() throws IOException {
        mapper.writeValue(writer, rootNode);
    }
}
