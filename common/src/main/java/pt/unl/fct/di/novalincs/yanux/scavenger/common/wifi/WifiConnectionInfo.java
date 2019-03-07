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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.wifi;

import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class WifiConnectionInfo {
    private static final String LOG_TAG = Constants.LOG_TAG + "_WIFI_CONNECTIONINFO";

    private String ssid;
    private boolean ssidHidden;
    private String bssid;
    private InetAddress ipAddress;
    private String macAddress;
    private int rssi;
    private int linkSpeed;
    private int networkId;
    private SupplicantState supplicantState;
    private NetworkInfo.DetailedState detailedState;

    private InetAddress dhcpIpAddress;
    private InetAddress dhcpNetmask;
    private InetAddress dhcpGateway;
    private InetAddress dhcpDns1;
    private InetAddress dhcpDns2;
    private int dhcpLeaseDuration;

    public WifiConnectionInfo() {
    }

    public WifiConnectionInfo(WifiManager wifiManager) {
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        this.ssid = wifiInfo.getSSID();
        this.ssidHidden = wifiInfo.getHiddenSSID();
        this.bssid = wifiInfo.getBSSID();
        this.macAddress = wifiInfo.getMacAddress();
        this.rssi = wifiInfo.getRssi();
        this.linkSpeed = wifiInfo.getLinkSpeed();
        this.networkId = wifiInfo.getNetworkId();
        this.supplicantState = wifiInfo.getSupplicantState();
        this.detailedState = WifiInfo.getDetailedStateOf(this.supplicantState);
        try {
            this.ipAddress = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(wifiInfo.getIpAddress()).array());
            this.dhcpIpAddress = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.ipAddress).array());
            this.dhcpNetmask = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.netmask).array());
            this.dhcpGateway = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.gateway).array());
            this.dhcpDns1 = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.dns1).array());
            this.dhcpDns2 = InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dhcpInfo.dns2).array());
            this.dhcpLeaseDuration = dhcpInfo.leaseDuration;
        } catch (UnknownHostException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public boolean isSsidHidden() {
        return ssidHidden;
    }

    public void setSsidHidden(boolean ssidHidden) {
        this.ssidHidden = ssidHidden;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public SupplicantState getSupplicantState() {
        return supplicantState;
    }

    public void setSupplicantState(SupplicantState supplicantState) {
        this.supplicantState = supplicantState;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        return detailedState;
    }

    public void setDetailedState(NetworkInfo.DetailedState detailedState) {
        this.detailedState = detailedState;
    }

    public InetAddress getDhcpIpAddress() {
        return dhcpIpAddress;
    }

    public void setDhcpIpAddress(InetAddress dhcpIpAddress) {
        this.dhcpIpAddress = dhcpIpAddress;
    }

    public InetAddress getDhcpNetmask() {
        return dhcpNetmask;
    }

    public void setDhcpNetmask(InetAddress dhcpNetmask) {
        this.dhcpNetmask = dhcpNetmask;
    }

    public InetAddress getDhcpGateway() {
        return dhcpGateway;
    }

    public void setDhcpGateway(InetAddress dhcpGateway) {
        this.dhcpGateway = dhcpGateway;
    }

    public InetAddress getDhcpDns1() {
        return dhcpDns1;
    }

    public void setDhcpDns1(InetAddress dhcpDns1) {
        this.dhcpDns1 = dhcpDns1;
    }

    public InetAddress getDhcpDns2() {
        return dhcpDns2;
    }

    public void setDhcpDns2(InetAddress dhcpDns2) {
        this.dhcpDns2 = dhcpDns2;
    }

    public int getDhcpLeaseDuration() {
        return dhcpLeaseDuration;
    }

    public void setDhcpLeaseDuration(int dhcpLeaseDuration) {
        this.dhcpLeaseDuration = dhcpLeaseDuration;
    }
}
