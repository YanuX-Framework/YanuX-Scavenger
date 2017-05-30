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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileOutput implements IFileOutput {
    public static final String DEFAULT_DIRECTORY = "YanuX-Scavenger";
    public static final String DEFAULT_FILENAME = "file.out";
    public static final StorageType DEFAULT_STORAGE_TYPE = StorageType.EXTERNAL;

    protected Context context;
    protected String directory;
    protected String filename;
    protected StorageType storageType;
    protected boolean open;
    protected File file;

    public AbstractFileOutput(Context context, String directory, String filename, StorageType storageType) {
        this.context = context;
        this.directory = directory;
        this.filename = filename;
        this.storageType = storageType;

        if(context == null || storageType == null) {
            this.storageType = StorageType.EXTERNAL;
        }
        if(directory == null) {
            this.directory = DEFAULT_DIRECTORY;
        }
        if(filename == null) {
            this.filename = DEFAULT_FILENAME;
        }

        open = false;
    }

    public AbstractFileOutput(Context context, String filename, StorageType storageType) {
        this(context, DEFAULT_DIRECTORY, filename, storageType);
    }

    public AbstractFileOutput(Context context, String directory, String filename) {
        this(context, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileOutput(Context context, String filename) {
        this(context, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileOutput(Context context) {
        this(context, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileOutput(String directory, String filename, StorageType storageType) {
        this(null, directory, filename, storageType);
    }

    public AbstractFileOutput(String directory, String filename) {
        this(null, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileOutput(String filename, StorageType storageType) {
        this(null, DEFAULT_DIRECTORY, filename, storageType);
    }

    public AbstractFileOutput(String filename) {
        this(null, DEFAULT_DIRECTORY, filename, DEFAULT_STORAGE_TYPE);
    }

    public AbstractFileOutput() {
        this(null, DEFAULT_DIRECTORY, DEFAULT_FILENAME, DEFAULT_STORAGE_TYPE);
    }

    public String getDirectory() {
        return directory;
    }

    protected static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public StorageType getStorageType() {
        return storageType;
    }

    @Override
    public String getPath() {
        if(directory != null && !directory.isEmpty()) {
            return directory + "/" + filename;
        } else {
            return filename;
        }
    }

    @Override
    public String getStorageDirectory() {
        if(directory != null && !directory.isEmpty()) {
            switch(storageType) {
                case EXTERNAL:
                    return Environment.getExternalStorageDirectory() + "/" + directory;
                case INTERNAL:
                    return context.getFilesDir() + "/" + directory;
                case CACHE:
                    return context.getFilesDir()+ "/" + directory;
                default:
                    return "";
            }
        } else {
            switch(storageType) {
                case EXTERNAL:
                    return Environment.getExternalStorageDirectory().getPath();
                case INTERNAL:
                    return context.getFilesDir().getPath();
                case CACHE:
                    return context.getFilesDir().getPath();
                default:
                    return "";
            }
        }
    }

    @Override
    public String getStoragePath() {
        return getStorageDirectory() + "/" + filename;
    }

    @Override
    public void setStorageType(StorageType storageType) throws IOException {
        if(isOpen()) {
            throw new IOException("The file is currently open. Please close it before changing the storage type.");
        } else {
            this.storageType = storageType;
        }
    }

    @Override
    public void open() throws IOException {
        if (isOpen()) {
            throw new IOException("The file is already open.");
        }
        File directory = new File(getStorageDirectory());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Couldn't create the directory");
        }
        if(storageType == StorageType.EXTERNAL) {
            if (isExternalStorageWritable()) {
                file = new File(getStoragePath());
            } else {
                throw new IOException("External storage is not writable.");
            }
        }
        open = true;
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            open = false;
        } else {
            throw new IOException("The file is already closed.");
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }


}
