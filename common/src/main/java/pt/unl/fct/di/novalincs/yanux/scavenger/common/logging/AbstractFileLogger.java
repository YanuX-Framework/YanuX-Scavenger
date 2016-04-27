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

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileLogger implements IFileLogger {
    public static final String DEFAULT_DIRECTORY = "YanuX-Scavenger";
    public static final String DEFAULT_FILENAME = "log.txt";
    protected String directory;
    protected String filename;
    protected boolean open;
    protected File file;


    public AbstractFileLogger(String directory, String filename) {
        this.directory = directory;
        this.filename = filename;
        open = false;
    }

    public AbstractFileLogger(String filename) {
        this(DEFAULT_DIRECTORY, filename);
    }

    public AbstractFileLogger() {
        this(DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    protected static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void open() throws IOException {
        if (isOpen()) {
            throw new IOException("The logger is already open.");
        }
        File directory = new File(getExternalStorageDirectory());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Couldn't create the log directory");
        }
        if (isExternalStorageWritable()) {
            file = new File(getExternalStoragePath());
        } else {
            throw new IOException("External Storage is not writable.");
        }
        open = true;
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            open = false;
        } else {
            throw new IOException("The logger is already closed.");
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return directory + "/" + filename;
    }

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory() + "/" + directory;
    }

    public String getExternalStoragePath() {
        return getExternalStorageDirectory() + "/" + filename;
    }
}
