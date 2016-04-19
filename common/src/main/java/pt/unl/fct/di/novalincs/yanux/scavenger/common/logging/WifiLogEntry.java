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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.logging;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiConnectionInfo;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;

public class WifiLogEntry extends WifiResult {
    private int sampleId;
    private WifiConnectionInfo connectionInfo;
    private List<SensorLogEntry> sensorLogEntry;

    public WifiLogEntry() {
        super();
    }

    public WifiLogEntry(int sampleId, WifiResult wifiResult, WifiConnectionInfo connectionInfo) {
        super(wifiResult.getSsid(), wifiResult.getMacAddress(), wifiResult.getSignalStrength(), wifiResult.getFrequency(), wifiResult.getSignalStrength());
        this.sampleId = sampleId;
        this.connectionInfo = connectionInfo;
    }

    public WifiLogEntry(int sampleId, WifiResult wifiResult, WifiConnectionInfo connectionInfo, List<SensorLogEntry> sensorLogEntry) {
        this(sampleId, wifiResult, connectionInfo);
        this.sensorLogEntry = sensorLogEntry;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public WifiConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(WifiConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public List<SensorLogEntry> getSensorLogEntry() {
        return sensorLogEntry;
    }

    public void setSensorLogEntry(List<SensorLogEntry> sensorLogEntry) {
        this.sensorLogEntry = sensorLogEntry;
    }
}
