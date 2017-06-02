/*
 * Copyright (c) 2017 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import android.content.Context;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.AbstractFileOutput;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.StorageType;

public abstract class AbstractFileLogger extends AbstractFileOutput implements IFileLogger {
    public static final String DEFAULT_FILENAME = "file.log";

    public AbstractFileLogger(Context context, String directory, String filename, StorageType storageType) {
        super(context, directory, filename, storageType);
    }

    public AbstractFileLogger(Context context, String filename, StorageType storageType) {
        this(context, DEFAULT_DIRECTORY, filename, storageType);
    }

    public AbstractFileLogger(Context context, String directory, String filename) {
        this(context, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileLogger(Context context, String filename) {
        this(context, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileLogger(Context context) {
        this(context, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileLogger(String directory, String filename, StorageType storageType) {
        this(null, directory, filename, storageType);
    }

    public AbstractFileLogger(String directory, String filename) {
        this(null, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileLogger(String filename, StorageType storageType) {
        this(null, DEFAULT_DIRECTORY, filename, storageType);
    }

    public AbstractFileLogger(String filename) {
        this(null, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileLogger() {
        this(null, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }
}
