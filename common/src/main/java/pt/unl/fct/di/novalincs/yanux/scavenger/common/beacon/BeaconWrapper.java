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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.beacon;

import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;

public class BeaconWrapper extends Beacon {
    public static final Parcelable.Creator<Beacon> CREATOR = new Parcelable.Creator<Beacon>() {
        public BeaconWrapper createFromParcel(Parcel in) {
            return new BeaconWrapper(in);
        }

        public BeaconWrapper[] newArray(int size) {
            return new BeaconWrapper[size];
        }
    };
    private static CustomDistanceCalculator distanceCalculator = new CustomDistanceCalculator();

    public BeaconWrapper(Parcel in) {
        super(in);
    }

    public BeaconWrapper(Beacon beacon) {
        super(beacon);
    }

    @Override
    public String toString() {
        return "Name: " + this.getBluetoothName()
                + "\nAddress: " + this.getBluetoothAddress()
                + "\nID1: " + this.getId1()
                + "\nID2: " + this.getId2()
                + "\nID3: " + this.getId3()
                + "\nRSSI: " + this.getRssi()
                + "\nTX Power: " + this.getTxPower()
                + "\nDistance: " + this.getDistance() + " m";
    }

    @Override
    public double getDistance() {
        return super.getDistance();
        //return distanceCalculator.calculateDistance(getTxPower(), getRssi());
    }
}
