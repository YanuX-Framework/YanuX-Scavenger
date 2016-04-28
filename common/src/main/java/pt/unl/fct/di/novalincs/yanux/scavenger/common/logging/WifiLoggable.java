/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
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

public class WifiLoggable implements ILoggable {
    private List<WifiResult> wifiResults;
    private List<SensorLoggable> sensorLogEntries;
    private WifiConnectionInfo connectionInfo;

    public WifiLoggable() {
        this(new ArrayList<WifiResult>(), new ArrayList<SensorLoggable>(), null);
    }

    public WifiLoggable(List<WifiResult> wifiResults) {
        this(wifiResults, new ArrayList<SensorLoggable>(), null);
    }

    public WifiLoggable(WifiConnectionInfo connectionInfo) {
        this(new ArrayList<WifiResult>(), new ArrayList<SensorLoggable>(), connectionInfo);
    }

    public WifiLoggable(List<WifiResult> wifiResults, List<SensorLoggable> sensorSample, WifiConnectionInfo connectionInfo) {
        this.wifiResults = wifiResults;
        this.sensorLogEntries = sensorSample;
        this.connectionInfo = connectionInfo;
    }

    public List<WifiResult> getWifiResults() {
        return wifiResults;
    }

    public void setWifiResults(List<WifiResult> wifiResults) {
        this.wifiResults = wifiResults;
    }

    public List<SensorLoggable> getSensorLogEntries() {
        return sensorLogEntries;
    }

    public void setSensorLogEntries(List<SensorLoggable> sensorLogEntries) {
        this.sensorLogEntries = sensorLogEntries;
    }

    public WifiConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(WifiConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
