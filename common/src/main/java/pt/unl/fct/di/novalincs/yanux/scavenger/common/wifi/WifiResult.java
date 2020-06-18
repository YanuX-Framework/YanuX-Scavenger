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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi;

import android.net.wifi.ScanResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WifiResult {
    public long timestamp;
    private ScanResult scanResult;
    private String ssid;
    private String macAddress;
    private int signalStrength;
    private int frequency;

    public WifiResult() {
    }

    public WifiResult(ScanResult scanResult) {
        this();
        update(scanResult);
    }

    public WifiResult(String ssid, String macAddress, int signalStrength, int frequency, long timestamp) {
        setSsid(ssid);
        setMacAddress(macAddress);
        setSignalStrength(signalStrength);
        setFrequency(frequency);
        setTimestamp(timestamp);
        this.scanResult = null;
    }

    /*public ScanResult getScanResult() {
        return scanResult;
    }
    public void setScanResult(ScanResult scanResult) {
        update(scanResult);
    }*/

    private void update(ScanResult scanResult) {
        this.scanResult = scanResult;
        setSsid(scanResult.SSID);
        setMacAddress(scanResult.BSSID);
        setSignalStrength(scanResult.level);
        setFrequency(scanResult.frequency);
        setTimestamp(scanResult.timestamp);
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @JsonIgnore
    public int getChannel() {
        if (frequency == 2484) {
            return 14;
        } else if (frequency < 2484) {
            return (frequency - 2407) / 5;
        } else {
            return frequency / 5 - 1000;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return ssid + " [" + macAddress + "] Channel: " + getChannel() + " RSSI: " + signalStrength;
    }
}