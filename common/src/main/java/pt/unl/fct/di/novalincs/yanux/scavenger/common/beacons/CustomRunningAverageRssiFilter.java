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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.beacons;


import android.os.SystemClock;

import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.RssiFilter;

import java.util.ArrayList;
import java.util.List;

public class CustomRunningAverageRssiFilter implements RssiFilter {
    private static final String TAG = "RunningAverageRssiFilter";
    private static final long DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS = 20000; /* 20 seconds */
    private static long sampleExpirationMilliseconds = DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS;
    private List<Measurement> measurements = new ArrayList<Measurement>();

    public static void setSampleExpirationMilliseconds(long newSampleExpirationMilliseconds) {
        sampleExpirationMilliseconds = newSampleExpirationMilliseconds;
    }

    @Override
    public void addMeasurement(Integer rssi) {
        Measurement measurement = new Measurement();
        measurement.rssi = rssi;
        measurement.timestamp = SystemClock.elapsedRealtime();
        measurements.add(measurement);
    }

    @Override
    public boolean noMeasurementsAvailable() {
        return measurements.size() == 0;
    }

    @Override
    public int getMeasurementCount() {
        return measurements.size();
    }

    private synchronized void refreshMeasurements() {
        List<Measurement> newMeasurements = new ArrayList<Measurement>();
        for (Measurement m : measurements) {
            if (SystemClock.elapsedRealtime() - m.timestamp < sampleExpirationMilliseconds) {
                newMeasurements.add(m);
            }
        }
        measurements = newMeasurements;
    }

    @Override
    public double calculateRssi() {
        refreshMeasurements();
        int size = measurements.size();
        double sum = 0;
        for (Measurement m : measurements) {
            sum += m.rssi;
        }
        double runningAverage = sum / size;
        LogManager.d(TAG, "Running average mRssi based on %s measurements: %s", size, runningAverage);
        return runningAverage;
    }

    private class Measurement implements Comparable<Measurement> {
        Integer rssi;
        long timestamp;

        @Override
        public int compareTo(Measurement arg0) {
            return rssi.compareTo(arg0.rssi);
        }
    }
}
