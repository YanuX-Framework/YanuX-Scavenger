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

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiConnectionInfo;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;

public class WifiReading implements IReading {
    private List<WifiResult> wifiResults;
    private List<SensorReading> sensorEntries;
    private WifiConnectionInfo connectionInfo;

    public WifiReading() {
        this(new ArrayList<WifiResult>(), new ArrayList<SensorReading>(), null);
    }

    public WifiReading(List<WifiResult> wifiResults) {
        this(wifiResults, new ArrayList<SensorReading>(), null);
    }

    public WifiReading(WifiConnectionInfo connectionInfo) {
        this(new ArrayList<WifiResult>(), new ArrayList<SensorReading>(), connectionInfo);
    }

    public WifiReading(List<WifiResult> wifiResults, List<SensorReading> sensorSample, WifiConnectionInfo connectionInfo) {
        this.wifiResults = wifiResults;
        this.sensorEntries = sensorSample;
        this.connectionInfo = connectionInfo;
    }

    public List<WifiResult> getWifiResults() {
        return wifiResults;
    }

    public void setWifiResults(List<WifiResult> wifiResults) {
        this.wifiResults = wifiResults;
    }

    public List<SensorReading> getSensorEntries() {
        return sensorEntries;
    }

    public void setSensorEntries(List<SensorReading> sensorEntries) {
        this.sensorEntries = sensorEntries;
    }

    public WifiConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(WifiConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
