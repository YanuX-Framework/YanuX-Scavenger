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

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiConnectionInfo;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi.WifiResult;

public class WifiLogEntry extends WifiResult implements ILoggable {
    private int sampleId;
    private WifiConnectionInfo connectionInfo;

    public WifiLogEntry(int sampleId, WifiResult wifiResult, WifiConnectionInfo connectionInfo) {
        super(wifiResult.getSsid(), wifiResult.getMacAddress(), wifiResult.getSignalStrength(), wifiResult.getFrequency(), wifiResult.getSignalStrength());
        this.sampleId = sampleId;
        this.connectionInfo = connectionInfo;
    }

    public WifiConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(WifiConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    @JsonIgnore
    public Object[] getFieldValues() {
        return new Object[]{getSsid(),
                getMacAddress(),
                getSignalStrength(),
                getFrequency(),
                Long.toString(getTimestamp())};
    }

    @Override
    @JsonIgnore
    public String[] getFieldValuesText() {
        return new String[]{getSsid(),
                getMacAddress(),
                Integer.toString(getSignalStrength()),
                Integer.toString(getFrequency()),
                Long.toString(getTimestamp())};
    }
}