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

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.IReading;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Utilities;

public class BeaconReading implements IReading {
    private long timestamp;
    private final Beacon beacon;

    public BeaconReading(Beacon beacon) {
        this.timestamp = Utilities.getUnixTimeMillis();
        this.beacon = beacon;
    }

    public String getBluetoothName() {
        return beacon.getBluetoothName();
    }

    public String getBluetoothAddress() {
        return beacon.getBluetoothAddress();
    }

    public String getParserIdentifier() {
        return beacon.getParserIdentifier();
    }

    public List<Object> getIdentifiers() {
        List<Object> identifiers = new ArrayList<>();
        for (Identifier identifier : beacon.getIdentifiers()) {
            Object currentIdentifier;
            try {
                currentIdentifier = identifier.toInt();
            } catch (UnsupportedOperationException e) {
                currentIdentifier = identifier.toString().toUpperCase();
            }
            identifiers.add(currentIdentifier);
        }
        return identifiers;
    }

    public int getRssi() {
        return beacon.getRssi();
    }

    public double getDistance() {
        return beacon.getDistance();
    }

    public int getTxPower() {
        return beacon.getTxPower();
    }

    public double getRunningAverageRssi() {
        return beacon.getRunningAverageRssi();
    }

    public int getMeasurementCount() {
        return beacon.getMeasurementCount();
    }

    public int getBeaconTypeCode() {
        return beacon.getBeaconTypeCode();
    }

    public int getManufacturer() {
        return beacon.getManufacturer();
    }

    public int getServiceUuid() {
        return beacon.getServiceUuid();
    }

    public int getPacketCount() {
        return beacon.getPacketCount();
    }

    public List<Long> getDataFields() {
        return beacon.getDataFields();
    }

    public boolean isExtraBeaconData() {
        return beacon.isExtraBeaconData();
    }

    public List<Long> getExtraDataFields() {
        return beacon.getExtraDataFields();
    }

    public boolean isMultiFrameBeacon() {
        return beacon.isMultiFrameBeacon();
    }
}
