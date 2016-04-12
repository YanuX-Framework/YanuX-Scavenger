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

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public abstract class AbstractLogger implements ILogger {
    public static final String DEFAULT_DIRECTORY = "YanuX-Scavenger";
    protected String directory;
    protected String filename;

    public AbstractLogger(String directory, String filename) throws IOException {
        this.directory = directory;
        this.filename = filename;
        File file = new File(getExternalStorageDirectory());
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("Couldn't create the log directory");
        }
    }

    protected static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
