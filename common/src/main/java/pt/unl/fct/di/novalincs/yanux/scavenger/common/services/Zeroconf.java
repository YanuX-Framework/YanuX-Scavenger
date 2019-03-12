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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.services;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.nio.charset.StandardCharsets;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class Zeroconf {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + PersistentService.class.getSimpleName();
    private final String SERVICE_TYPE = "_http._tcp.";
    private Context context;
    private Preferences preferences;
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;

    public Zeroconf(Context context) {
        this.context = context;
    }

    public void startDiscovery() {
        if (context != null) {
            if (discoveryListener != null) {
                stopDiscovery();
            }
            preferences = new Preferences(context);
            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
            discoveryListener = new ZeroconfDiscoveryListener();
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        }
    }

    public void stopDiscovery() {
        if (nsdManager != null && discoveryListener != null) {
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryListener = null;
        }
    }

    private class ZeroconfDiscoveryListener implements NsdManager.DiscoveryListener {
        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(LOG_TAG, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            // A service was found! Do something with it.
            Log.d(LOG_TAG, "Service discovery success" + service);
            if (service.getServiceType().equals(SERVICE_TYPE)
                    && service.getServiceName().contains("YanuX")) {
                nsdManager.resolveService(service, new ZeroconfResolveListener());
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(LOG_TAG, "service lost: " + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(LOG_TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(LOG_TAG, "Discovery failed: Error code:" + errorCode);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(LOG_TAG, "Discovery failed: Error code:" + errorCode);
            nsdManager.stopServiceDiscovery(this);
        }
    }

    private class ZeroconfResolveListener implements NsdManager.ResolveListener {
        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.d(LOG_TAG, "Service resolution success: " + serviceInfo);
            String protocol = new String(serviceInfo.getAttributes().get("protocol"), StandardCharsets.UTF_8);
            if (serviceInfo.getServiceName().equals("YanuX-Auth")) {
                preferences.setYanuxAuthOauth2AuthorizationServerUrl(protocol + "://" + serviceInfo.getHost().getCanonicalHostName() + ":" + serviceInfo.getPort() + "/");
                Log.d(LOG_TAG, "YanuX-Auth: " + preferences.getYanuxAuthOauth2AuthorizationServerUrl());
            } else if (serviceInfo.getServiceName().equals("YanuX-Broker")) {
                preferences.setYanuxBrokerUrl(protocol + "://" + serviceInfo.getHost().getCanonicalHostName() + ":" + serviceInfo.getPort() + "/");
                Log.d(LOG_TAG, "YanuX-Broker: " + preferences.getYanuxBrokerUrl());
            }
        }

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(LOG_TAG, "Resolution failed: Error code: " + errorCode + " Service Info: " + serviceInfo);
            if (errorCode == NsdManager.FAILURE_ALREADY_ACTIVE) {
                nsdManager.resolveService(serviceInfo, this);
            }
        }
    }
}
