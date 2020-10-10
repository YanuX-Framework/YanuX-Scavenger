/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.file;

import android.content.Context;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public abstract class AbstractFileOutput implements IFileOutput {
    private static final String LOG_TAG = Constants.LOG_TAG + "_ABS_FILE_OUT";
    public static final String DEFAULT_DIRECTORY = "YanuX";
    public static final String DEFAULT_FILENAME = "file.out";
    public static final StorageType DEFAULT_STORAGE_TYPE = StorageType.ANDROID_URI;

    protected Context context;
    protected String directory;
    protected String filename;
    protected StorageType storageType;
    protected boolean open;
    protected FileOutputStream fileOutputStream;

    public AbstractFileOutput(Context context, String directory, String filename, StorageType storageType) {
        this.context = context;
        this.directory = directory;
        this.filename = filename;
        this.storageType = storageType;

        if (storageType == null) {
            this.storageType = DEFAULT_STORAGE_TYPE;
        }
        if (directory == null) {
            this.directory = DEFAULT_DIRECTORY;
        }
        if (filename == null) {
            this.filename = DEFAULT_FILENAME;
        }

        if(storageType == StorageType.ANDROID_URI) {
            List<UriPermission> uriPermissions = context.getContentResolver().getPersistedUriPermissions();
            if(!uriPermissions.isEmpty()) {
                this.directory = Uri.decode(uriPermissions.get(uriPermissions.size()-1).getUri().toString());
            }
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

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) throws IOException {
        if (isOpen()) {
            throw new IOException("The file is currently open. Please close it before changing the storage type.");
        } else {
            this.storageType = storageType;
        }
    }

    public String getPath() {
        if (directory != null && !directory.isEmpty()) {
            return directory + "/" + filename;
        } else {
            return filename;
        }
    }

    public String getStorageDirectory() {
        if (directory != null && !directory.isEmpty()) {
            switch (storageType) {
                case EXTERNAL:
                    return Environment.getExternalStorageDirectory() + "/" + directory;
                case INTERNAL:
                    return context.getFilesDir() + "/" + directory;
                case CACHE:
                    return context.getCacheDir() + "/" + directory;
                default:
                    return directory;
            }
        } else {
            switch (storageType) {
                case EXTERNAL:
                    return Environment.getExternalStorageDirectory().getPath();
                case INTERNAL:
                    return context.getFilesDir().getPath();
                case CACHE:
                    return context.getCacheDir().getPath();
                default:
                    return "";
            }
        }
    }

    @Override
    public String getStoragePath() {
        return getStorageDirectory().endsWith(":") ? filename : getStorageDirectory() + "/" + filename;
    }

    @Override
    public void open() throws IOException {
        if (isOpen()) {
            throw new IOException("The file is already open.");
        }
        if(storageType == StorageType.ANDROID_URI) {
            Uri directoryUri = Uri.parse(getStorageDirectory());
            Log.d(LOG_TAG, "Directory Uri: " + directoryUri);
            DocumentFile folder = DocumentFile.fromTreeUri(context, directoryUri);
            DocumentFile file = folder.findFile(filename);
            if (file == null) {
                file = folder.createFile("application/octet-stream", filename);
            }
            if (file == null) {
                throw new IOException("Couldn't find or create the required the file. The probable cause is that the directory in which the file was supposed to be saved no longer exists.");
            }
            FileDescriptor fd = context.getContentResolver().openFileDescriptor(file.getUri(), "w").getFileDescriptor();
            fileOutputStream = new FileOutputStream(fd);
        } else {
            File directory = new File(getStorageDirectory());
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Couldn't create the directory");
            }
            if (storageType == StorageType.EXTERNAL) {
                if (isExternalStorageWritable()) {
                    fileOutputStream = new FileOutputStream(getStoragePath(), false);
                } else {
                    throw new IOException("External storage is not writable.");
                }
            }
        }
        open = true;
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            open = false;
            fileOutputStream.close();
        } else {
            throw new IOException("The file is already closed.");
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }


}
