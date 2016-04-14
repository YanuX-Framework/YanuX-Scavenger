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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

public class CsvLogger extends AbstractLogger {
    public static final String DEFAULT_FILENAME = "log.csv";

    private CSVFormat csvFileFormat;
    private CSVPrinter csvPrinter;

    public CsvLogger(String directory, String filename) throws IOException {
        super(directory, filename);
        csvFileFormat = CSVFormat.DEFAULT;
    }

    public CsvLogger() throws IOException {
        this(DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    @Override
    public void open() throws IOException {
        super.isOpen();
        if (isExternalStorageWritable()) {
            csvPrinter = new CSVPrinter(new FileWriter(getExternalStoragePath()), csvFileFormat);
        } else {
            throw new IOException("External Storage is not writable");
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        csvPrinter.flush();
        csvPrinter.close();
    }

    @Override
    public void log(ILoggable object) {
        try {
            csvPrinter.printRecord(object.getFieldValues());
            //If an exception happens there's not much that the "callee" can do, so I'll just print the stack trace for now
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CSVFormat getCsvFileFormat() {
        return csvFileFormat;
    }

    public void setCsvFileFormat(CSVFormat csvFileFormat) {
        this.csvFileFormat = csvFileFormat;
    }
}