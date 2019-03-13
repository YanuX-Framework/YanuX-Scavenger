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

import java.util.List;

public class YanuxBrokerBeacon {
    private String user;
    private String deviceUuid;
    private String beaconKey;
    private YanuxBrokerBeaconDetails beacon;

    public YanuxBrokerBeacon(String user, String deviceUuid, String beaconKey, String id, String type, List<Object> values, int txPower, int rssi, long timestamp) {
        this.user = user;
        this.deviceUuid = deviceUuid;
        this.beaconKey = beaconKey;
        this.beacon = new YanuxBrokerBeaconDetails(id, type, values, txPower, rssi, timestamp);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public String getBeaconKey() {
        return beaconKey;
    }

    public void setBeaconKey(String beaconKey) {
        this.beaconKey = beaconKey;
    }

    public YanuxBrokerBeaconDetails getBeacon() {
        return beacon;
    }

    public void setBeacon(YanuxBrokerBeaconDetails beacon) {
        this.beacon = beacon;
    }
}
