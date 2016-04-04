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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi;

import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WifiConnectionInfo {
    private String ssid;
    private boolean ssidHidden;
    private String bssid;
    private InetAddress wifiIpAdress;
    private String macAddress;
    private int rssi;
    private int linkSpeed;
    private int networkId;
    private SupplicantState supplicantState;
    private NetworkInfo.DetailedState detailedState;

    private InetAddress ipAdress;
    private InetAddress netmask;
    private InetAddress gateway;
    private InetAddress dns1;
    private InetAddress dns2;
    private int leaseDuration;

    public WifiConnectionInfo(WifiInfo wifiInfo, DhcpInfo dhcpInfo) {
        this.ssid = wifiInfo.getSSID();
        this.ssidHidden = wifiInfo.getHiddenSSID();
        this.bssid = wifiInfo.getBSSID();
        this.macAddress = wifiInfo.getMacAddress();
        this.rssi = wifiInfo.getRssi();
        this.linkSpeed = wifiInfo.getLinkSpeed();
        this.networkId = wifiInfo.getNetworkId();
        this.supplicantState = wifiInfo.getSupplicantState();
        this.detailedState = wifiInfo.getDetailedStateOf(this.supplicantState);
        try {
            this.wifiIpAdress = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(wifiInfo.getIpAddress()).array());
            this.ipAdress = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.ipAddress).array());
            this.netmask = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.netmask).array());
            this.gateway = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.gateway).array());
            this.dns1 = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.dns1).array());
            this.dns2 = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.dns2).array());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.leaseDuration = dhcpInfo.leaseDuration;
    }

    public String getSsid() {
        return ssid;
    }

    public boolean isSsidHidden() {
        return ssidHidden;
    }

    public String getBssid() {
        return bssid;
    }

    public InetAddress getWifiIpAdress() {
        return wifiIpAdress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public int getNetworkId() {
        return networkId;
    }

    public SupplicantState getSupplicantState() {
        return supplicantState;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        return detailedState;
    }

    public InetAddress getIpAdress() {
        return ipAdress;
    }

    public InetAddress getNetmask() {
        return netmask;
    }

    public InetAddress getGateway() {
        return gateway;
    }

    public InetAddress getDns1() {
        return dns1;
    }

    public InetAddress getDns2() {
        return dns2;
    }

    public int getLeaseDuration() {
        return leaseDuration;
    }
}
