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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons;

import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.IReading;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;

public class BeaconWrapper extends Beacon implements IReading {
    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
        public BeaconWrapper createFromParcel(Parcel in) {
            return new BeaconWrapper(in);
        }

        public BeaconWrapper[] newArray(int size) {
            return new BeaconWrapper[size];
        }
    };
    private static CustomDistanceCalculator distanceCalculator = new CustomDistanceCalculator();

    private long timestamp;

    public BeaconWrapper(Parcel in) {
        super(in);
        init();
    }

    public BeaconWrapper(Beacon beacon) {
        super(beacon);
        init();
    }

    public BeaconWrapper() {
        super();
        init();
    }

    private void init() {
        timestamp = Utilities.getUnixTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public BeaconDetails getDetails() {
        return new BeaconDetails(this);
    }

    @Override
    public String toString() {
        String result = "Name: " + this.getBluetoothName()
                + "\nAddress: " + this.getBluetoothAddress();

        int idCounter = 1;
        for (Identifier id : this.getIdentifiers()) {
            result += "\nID" + idCounter++ + ": " + id.toString();
        }

        result += "\nTX Power: " + this.getTxPower()
                + "\nRSSI: " + this.getRssi() + " dBm"
                + "\nDistance: " + this.getDistance() + " m";
        return result;
    }

    @Override
    public double getDistance() {
        return super.getDistance();
        //return distanceCalculator.calculateDistance(getTxPower(), getRssi());
    }
}
