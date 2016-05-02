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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

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
    private ArrayNode entries;
    private Writer writer;

    public JsonLogger(String name, String directory, String filename) throws IOException {
        super(directory, filename);
        this.name = name;
        mapper = new ObjectMapper();
    }

    public JsonLogger(String name) throws IOException {
        this(name, DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    @Override
    public void open() throws IOException {
        super.open();
        if (isExternalStorageWritable()) {
            File file = new File(getExternalStoragePath());
            rootNode = mapper.createObjectNode();
            try {
                Reader reader = new FileReader(file);
                rootNode = mapper.readTree(reader);
                reader.close();
            } catch (IOException e) {
            }
            writer = new FileWriter(file);
            ObjectNode root = (ObjectNode) rootNode;
            root.put("filename", filename);
            if (!root.has("creationTime")) {
                root.put("creationTime", System.currentTimeMillis());
            }
            ArrayNode logs;
            if (root.has("logs")) {
                logs = (ArrayNode) root.get("logs");
            } else {
                logs = mapper.createArrayNode();
            }
            root.set("logs", logs);
            ObjectNode log = mapper.createObjectNode();
            logs.add(log);
            log.put("name", name);
            log.put("timestamp", System.currentTimeMillis());
            entries = mapper.createArrayNode();
            log.set("entries", entries);
        } else {
            throw new IOException("External Storage is not writable.");
        }
    }

    @Override
    public void log(Object object) {
        entries.add(mapper.valueToTree(object));
    }

    @Override
    public void close() throws IOException {
        super.close();
        mapper.writeValue(writer, rootNode);
    }
}
