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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BluetoothDetectedDevice implements Parcelable {
    public static final Parcelable.Creator<BluetoothDetectedDevice> CREATOR = new Parcelable.Creator<BluetoothDetectedDevice>() {
        public BluetoothDetectedDevice createFromParcel(Parcel in) {
            return new BluetoothDetectedDevice(in);
        }

        public BluetoothDetectedDevice[] newArray(int size) {
            return new BluetoothDetectedDevice[size];
        }
    };

    private final String name;
    private final String address;
    private final int rssi;
    public long timestamp;

    public BluetoothDetectedDevice(Parcel in) {
        name = in.readString();
        address = in.readString();
        rssi = in.readInt();
        timestamp = in.readLong();
    }

    public BluetoothDetectedDevice(BluetoothDevice bluetoothDevice, int rssi, long timestamp) {
        this.name = bluetoothDevice.getName();
        this.address = bluetoothDevice.getAddress();
        this.rssi = rssi;
        this.timestamp = timestamp;
    }

    public BluetoothDetectedDevice(BluetoothDevice bluetoothDevice, int rssi) {
        this(bluetoothDevice, rssi, -1);
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeInt(rssi);
        dest.writeLong(timestamp);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothDetectedDevice that = (BluetoothDetectedDevice) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(address != null ? !address.equals(that.address) : that.address != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "\nAddress: " + address +
                "\nRSSI: " + rssi;
    }
}