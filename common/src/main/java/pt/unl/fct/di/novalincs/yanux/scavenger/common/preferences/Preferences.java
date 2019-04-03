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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    public static final String EMPTY = "";

    private static final String SHOW_RATIONALE_PREFERENCE_PREFIX = "SHOW_RATIONALE:";
    private static final boolean SHOW_RATIONALE_PREFERENCE_PREFIX_DEFAULT = true;

    public static final String PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE = "asked_wifi_scanning_always_available";
    public static final boolean PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE_DEFAULT = false;

    public static final String PREFERENCE_LOG_NAME = "log_name";
    public static final String PREFERENCE_LOG_NAME_DEFAULT = "log";

    public static final String PREFERENCE_LOG_SAMPLES = "log_samples";
    public static final int PREFERENCE_LOG_SAMPLES_DEFAULT = 10;

    public static final String ALLOW_PERSISTENT_SERVICE = "allow_persistent_service";
    public static final boolean ALLOW_PERSISTENT_SERVICE_DEFAULT = true;

    public static final String ALLOW_ZEROCONF = "allow_zeroconf";
    public static final boolean ALLOW_ZEROCONF_DEFAULT = false;

    public static final String SHOULD_BEACON_SCAN = "beacon_scan";
    public static final boolean SHOULD_BEACON_SCAN_DEFAULT = false;

    public static final String SHOULD_BEACON_ADVERTISE = "beacon_advertise";
    public static final boolean SHOULD_BEACON_ADVERTISE_DEFAULT = false;

    public static final String PREFERENCE_DEVICE_UUID = "device_uuid";
    public static final String PREFERENCE_DEVICE_UUID_DEFAULT = EMPTY;

    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_UUID = "beacon_matcher_parameters_uuid";
    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_UUID_DEFAULT = "113069EC-6E64-4BD3-6810-DE01B36E8A3E";

    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR = "beacon_matcher_parameters_major";
    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR_DEFAULT = "-1";

    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_MINOR = "beacon_matcher_parameters_minor";
    public static final String PREFERENCE_BEACON_MATCHER_PARAMETERS_MINOR_DEFAULT = "-1";

    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_UUID = "beacon_advertiser_parameters_uuid";
    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_UUID_DEFAULT = "113069EC-6E64-4BD3-6810-DE01B36E8A3E";

    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR = "beacon_advertiser_parameters_major";
    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR_DEFAULT = "0";

    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR = "beacon_advertiser_parameters_minor";
    public static final String PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR_DEFAULT = "0";

    public static final String PREFERENCE_BEACONS_REFRESH_INTERVAL = "beacons_refresh_interval";
    public static final String PREFERENCE_BEACONS_REFRESH_INTERVAL_DEFAULT = Integer.toString(1000);

    public static final String PREFERENCE_BEACONS_INACTIVITY_TIMER = "beacons_inactivity_timer";
    public static final String PREFERENCE_BEACONS_INACTIVITY_TIMER_DEFAULT = Integer.toString(1000);

    public static final String PREFERENCE_DEVICE_CAPABILITIES_VIEW = "device_capabilities_view";
    public static final boolean PREFERENCE_DEVICE_CAPABILITIES_VIEW_DEFAULT = false;

    public static final String PREFERENCE_DEVICE_CAPABILITIES_CONTROL = "device_capabilities_control";
    public static final boolean PREFERENCE_DEVICE_CAPABILITIES_CONTROL_DEFAULT = false;

    public static final String PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL = "yanux_auth_oauth2_authorization_server_url";
    public static final String PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL_DEFAULT = "http://localhost:3001/";

    public static final String PREFERENCE_YANUX_AUTH_CLIENT_ID = "yanux_auth_client_id";
    public static final String PREFERENCE_YANUX_AUTH_CLIENT_ID_DEFAULT = "yanux-mobile-client";

    public static final String PREFERENCE_YANUX_AUTH_CLIENT_SECRET = "yanux_auth_client_secret";
    public static final String PREFERENCE_YANUX_AUTH_CLIENT_SECRET_DEFAULT = "topsecret_client_secret";

    public static final String PREFERENCE_YANUX_AUTH_JWT = "yanux_auth_jwt_token";
    public static final String PREFERENCE_YANUX_AUTH_JWT_DEFAULT = EMPTY;

    public static final String PREFERENCE_YANUX_AUTH_AUTHORIZATION_CODE = "yanux_auth_authorization_code";
    public static final String PREFERENCE_YANUX_AUTH_AUTHORIZATION_CODE_DEFAULT = EMPTY;

    public static final String PREFERENCE_YANUX_AUTH_ACCESS_TOKEN = "yanux_auth_access_token";
    public static final String PREFERENCE_YANUX_AUTH_ACCESS_TOKEN_DEFAULT = EMPTY;

    public static final String PREFERENCE_YANUX_AUTH_REFRESH_TOKEN = "yanux_auth_refresh_token";
    public static final String PREFERENCE_YANUX_AUTH_REFRESH_TOKEN_DEFAULT = EMPTY;

    public static final String PREFERENCE_YANUX_AUTH_REDIRECT_URI = "yanux_auth_redirect_uri";
    public static final String PREFERENCE_YANUX_AUTH_REDIRECT_URI_DEFAULT = EMPTY;

    public static final String PREFERENCE_YANUX_BROKER_URL = "yanux_broker_url";
    public static final String PREFERENCE_YANUX_BROKER_URL_DEFAULT = "http://localhost:3002/";

    public static final String PREFERENCE_HTTP_SERVER_PORT = "http_server_port";
    public static final String PREFERENCE_HTTP_SERVER_PORT_DEFAULT = "8080";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor preferencesEditor;

    public Preferences(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferencesEditor = preferences.edit();
    }

    public boolean shouldShowRequestPermissionRationale(String permission) {
        return preferences.getBoolean(SHOW_RATIONALE_PREFERENCE_PREFIX + permission, SHOW_RATIONALE_PREFERENCE_PREFIX_DEFAULT);
    }

    public void setShouldShowRequestRationale(String permission, boolean shouldShow) {
        preferencesEditor.putBoolean(SHOW_RATIONALE_PREFERENCE_PREFIX + permission, shouldShow).apply();
    }

    public boolean hasAskedForWifiScanningAlwaysAvailable() {
        return preferences.getBoolean(PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE,
                PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE_DEFAULT);
    }

    public void setHasAskedForWifiScanningAlwaysAvailable(boolean flag) {
        preferencesEditor.putBoolean(PREFERENCE_ASKED_WIFI_SCANNING_ALWAYS_AVAILABLE, flag).apply();
    }

    public String getLogName() {
        return preferences.getString(PREFERENCE_LOG_NAME, PREFERENCE_LOG_NAME_DEFAULT);
    }

    public void setLogName(String logName) {
        preferencesEditor.putString(PREFERENCE_LOG_NAME, logName).apply();
    }

    public int getLogSamples() {
        return preferences.getInt(PREFERENCE_LOG_SAMPLES, PREFERENCE_LOG_SAMPLES_DEFAULT);
    }

    public void setLogSamples(int samples) {
        preferencesEditor.putInt(PREFERENCE_LOG_SAMPLES, samples).apply();
    }

    public boolean isPersistentServiceAllowed() {
        return preferences.getBoolean(ALLOW_PERSISTENT_SERVICE,
                ALLOW_PERSISTENT_SERVICE_DEFAULT);
    }

    public void setIsPersistentServiceAllowed(boolean isPersistentServiceAllowed) {
        preferencesEditor.putBoolean(ALLOW_PERSISTENT_SERVICE, isPersistentServiceAllowed).apply();
    }

    public boolean isZeroconfAllowed() {
        return preferences.getBoolean(ALLOW_ZEROCONF, ALLOW_ZEROCONF_DEFAULT);
    }

    public void setIsZeroconfAllowed(boolean isZeroconfAllowed) {
        preferencesEditor.putBoolean(ALLOW_ZEROCONF, isZeroconfAllowed).apply();
    }

    public boolean shouldBeaconScan() {
        return preferences.getBoolean(SHOULD_BEACON_SCAN, SHOULD_BEACON_SCAN_DEFAULT);
    }

    public void setShouldBeaconScan(boolean shouldBeaconScan) {
        preferencesEditor.putBoolean(SHOULD_BEACON_SCAN, shouldBeaconScan).apply();
    }

    public boolean shouldBeaconAdvertise() {
        return preferences.getBoolean(SHOULD_BEACON_ADVERTISE, SHOULD_BEACON_ADVERTISE_DEFAULT);
    }

    public void setShouldBeaconAdvertise(boolean shouldBeaconAdvertise) {
        preferencesEditor.putBoolean(SHOULD_BEACON_ADVERTISE, shouldBeaconAdvertise).apply();
    }

    public String getDeviceUuid() {
        return preferences.getString(PREFERENCE_DEVICE_UUID, PREFERENCE_DEVICE_UUID_DEFAULT);
    }

    public void setDeviceUuid(String deviceUuid) {
        preferencesEditor.putString(PREFERENCE_DEVICE_UUID, deviceUuid).apply();
    }

    public String getBeaconMatcherParametersUuid() {
        return preferences.getString(PREFERENCE_BEACON_MATCHER_PARAMETERS_UUID, PREFERENCE_BEACON_MATCHER_PARAMETERS_UUID_DEFAULT);
    }

    public void setBeaconMatcherParametersUuid(String beaconScannerParametersUuid) {
        preferencesEditor.putString(PREFERENCE_BEACON_MATCHER_PARAMETERS_UUID, beaconScannerParametersUuid).apply();
    }

    public int getBeaconMatcherParametersMajor() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR, PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR_DEFAULT));
    }

    public void setBeaconMatcherParameterMajor(int beaconScannerParametersMajor) {
        preferencesEditor.putString(PREFERENCE_BEACON_MATCHER_PARAMETERS_MAJOR, String.valueOf(beaconScannerParametersMajor)).apply();
    }

    public int getBeaconMatcherParametersMinor() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACON_MATCHER_PARAMETERS_MINOR, PREFERENCE_BEACON_MATCHER_PARAMETERS_MINOR_DEFAULT));
    }

    public String getBeaconAdvertiserParametersUuid() {
        return preferences.getString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_UUID, PREFERENCE_BEACON_ADVERTISER_PARAMETERS_UUID_DEFAULT);
    }

    public void setBeaconAdvertiserParametersUuid(String beaconAdvertiserParametersUuid) {
        preferencesEditor.putString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_UUID, beaconAdvertiserParametersUuid).apply();
    }

    public int getBeaconAdvertiserParametersMajor() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR, PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR_DEFAULT));
    }

    public void setBeaconAdvertiserParameterMajor(int beaconAdvertiserParametersMajor) {
        preferencesEditor.putString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MAJOR, String.valueOf(beaconAdvertiserParametersMajor)).apply();
    }

    public int getBeaconAdvertiserParametersMinor() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR, PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR_DEFAULT));
    }

    public void setBeaconAdvertiserParameterMinor(int beaconAdvertiserParametersMinor) {
        preferencesEditor.putString(PREFERENCE_BEACON_ADVERTISER_PARAMETERS_MINOR, String.valueOf(beaconAdvertiserParametersMinor)).apply();
    }

    public int getBeaconsRefreshInterval() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACONS_REFRESH_INTERVAL, PREFERENCE_BEACONS_REFRESH_INTERVAL_DEFAULT));
    }

    public void setBeaconsRefreshInterval(int beaconsRefreshInterval) {
        preferencesEditor.putString(PREFERENCE_BEACONS_REFRESH_INTERVAL, String.valueOf(beaconsRefreshInterval)).apply();
    }

    public int getBeaconsInactivityTimer() {
        return Integer.parseInt(preferences.getString(PREFERENCE_BEACONS_INACTIVITY_TIMER, PREFERENCE_BEACONS_INACTIVITY_TIMER_DEFAULT));
    }

    public void setBeaconsInactivityTimer(int beaconsInactivityTimer) {
        preferencesEditor.putString(PREFERENCE_BEACONS_INACTIVITY_TIMER, String.valueOf(beaconsInactivityTimer)).apply();
    }

    public boolean hasViewCapabilities() {
        return preferences.getBoolean(PREFERENCE_DEVICE_CAPABILITIES_VIEW, PREFERENCE_DEVICE_CAPABILITIES_VIEW_DEFAULT);
    }

    public void setHasViewCapabilities(boolean viewCapabilities) {
        preferencesEditor.putBoolean(PREFERENCE_DEVICE_CAPABILITIES_VIEW, viewCapabilities).apply();
    }

    public boolean hasControlCapabilities() {
        return preferences.getBoolean(PREFERENCE_DEVICE_CAPABILITIES_CONTROL, PREFERENCE_DEVICE_CAPABILITIES_CONTROL_DEFAULT);
    }

    public void setHasControlCapabilities(boolean controlCapabilities) {
        preferencesEditor.putBoolean(PREFERENCE_DEVICE_CAPABILITIES_CONTROL, controlCapabilities).apply();
    }

    public String getYanuxAuthOauth2AuthorizationServerUrl() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL, PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL_DEFAULT);
    }

    public void setYanuxAuthOauth2AuthorizationServerUrl(String authorizationServerUrl) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_OAUTH2_AUTHORIZATION_SERVER_URL, authorizationServerUrl).apply();
    }

    public String getYanuxAuthClientId() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_CLIENT_ID, PREFERENCE_YANUX_AUTH_CLIENT_ID_DEFAULT);
    }

    public void setYanuxAuthClientId(String clientId) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_CLIENT_ID, clientId).apply();
    }

    public String getYanuxAuthClientSecret() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_CLIENT_SECRET, PREFERENCE_YANUX_AUTH_CLIENT_SECRET_DEFAULT);
    }

    public void setYanuxAuthClientSecret(String clientSecret) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_CLIENT_SECRET, clientSecret).apply();
    }

    public String getYanuxAuthJwt() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_JWT, PREFERENCE_YANUX_AUTH_JWT_DEFAULT);
    }

    public void setYanuxAuthJwt(String jwt) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_JWT, jwt).apply();
    }

    public String getYanuxAuthAuthorizationCode() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_AUTHORIZATION_CODE, PREFERENCE_YANUX_AUTH_AUTHORIZATION_CODE_DEFAULT);
    }

    public void setYanuxAuthAuthorizationCode(String authorizationCode) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_AUTHORIZATION_CODE, authorizationCode).apply();
    }

    public String getYanuxAuthAccessToken() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_ACCESS_TOKEN, PREFERENCE_YANUX_AUTH_ACCESS_TOKEN_DEFAULT);
    }

    public void setYanuxAccessToken(String accessToken) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_ACCESS_TOKEN, accessToken).apply();
    }

    public String getYanuxAuthRefreshToken() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_REFRESH_TOKEN, PREFERENCE_YANUX_AUTH_REFRESH_TOKEN_DEFAULT);
    }

    public void setYanuxRefreshToken(String refreshToken) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_REFRESH_TOKEN, refreshToken).apply();
    }

    public String getYanuxAuthRedirectUri() {
        return preferences.getString(PREFERENCE_YANUX_AUTH_REDIRECT_URI, PREFERENCE_YANUX_AUTH_REDIRECT_URI_DEFAULT);
    }

    public void setYanuxAuthRedirectUri(String jwt) {
        preferencesEditor.putString(PREFERENCE_YANUX_AUTH_REDIRECT_URI, jwt).apply();
    }

    public String getYanuxBrokerUrl() {
        return preferences.getString(PREFERENCE_YANUX_BROKER_URL, PREFERENCE_YANUX_BROKER_URL_DEFAULT);
    }

    public void setYanuxBrokerUrl(String brokerUrl) {
        preferencesEditor.putString(PREFERENCE_YANUX_BROKER_URL, brokerUrl).apply();
    }

    public int getHttpServerPort() {
        return Integer.parseInt(preferences.getString(PREFERENCE_HTTP_SERVER_PORT, PREFERENCE_HTTP_SERVER_PORT_DEFAULT));
    }

    public void setHttpServerPort(int httpServerPort) {
        preferencesEditor.putString(PREFERENCE_HTTP_SERVER_PORT, String.valueOf(httpServerPort)).apply();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getPreferencesEditor() {
        return preferencesEditor;
    }
}