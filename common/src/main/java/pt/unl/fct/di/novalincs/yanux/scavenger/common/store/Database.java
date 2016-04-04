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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.Constants;

public class Database {
    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public Database(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(this.context);
    }

    private void openDatabase() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    private void closeDatabase() {
        databaseHelper.close();
    }

    public void addLocation(double latitude, double longitude, String description) {
        this.openDatabase();
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
            values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
            database.insert(DatabaseHelper.TABLE_LOCATIONS, null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            this.closeDatabase();
        }
    }

    public void removeLocation(int position) {
        this.openDatabase();
        database.beginTransaction();
        try {
            Cursor locationIds = database.query(DatabaseHelper.TABLE_LOCATIONS, new String[]{DatabaseHelper.COLUMN_ID}, null, null, null, null, DatabaseHelper.COLUMN_ID + " ASC");
            if (locationIds.moveToPosition(position)) {
                database.delete(DatabaseHelper.TABLE_LOCATIONS, "ID = ?", new String[]{Integer.toString(locationIds.getInt(0))});
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            this.closeDatabase();
        }

    }

    public void logLocations() {
        this.openDatabase();
        database.beginTransaction();
        try {
            Cursor locationsCursor = database.query(DatabaseHelper.TABLE_LOCATIONS, DatabaseHelper.LOCATIONS_ALL_COLUMNS, null, null, null, null, DatabaseHelper.COLUMN_ID + " ASC");
            while (locationsCursor.moveToNext()) {
                Log.d(Constants.LOG_TAG, "Latitude: " + locationsCursor.getDouble(1) + "Longitude: " + locationsCursor.getDouble(2) + "Description: " + locationsCursor.getDouble(3));
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            this.closeDatabase();
        }
    }

}
