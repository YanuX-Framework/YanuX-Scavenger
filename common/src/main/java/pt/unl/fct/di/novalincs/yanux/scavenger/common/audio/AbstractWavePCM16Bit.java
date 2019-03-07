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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.audio;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public abstract class AbstractWavePCM16Bit implements ISoundWave {

    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final double DEFAULT_FREQUENCY = 440;

    protected int sampleRate;
    protected double frequency;
    protected int samples;
    protected boolean outdated = false;
    protected short[] data;

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        outdated = true;
    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(double frequency) {
        this.frequency = frequency;
        outdated = true;
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public void setSamples(int samples) {
        this.samples = samples;
        outdated = true;
    }

    @Override
    public int getDuration() {
        return (int) ((double) samples / (double) sampleRate * 1000.0d);
    }

    @Override
    public void setDuration(int duration) {
        this.setSamples((int) ((double) duration / 1000.0d * (double) sampleRate));
        outdated = true;
    }

    @Override
    public short[] getData() {
        if (outdated) {
            updateData();
        }
        return data;
    }

    @Override
    public int getByteSize() {
        return samples * Constants.SHORT_BYTES;
    }

    protected void updateData() {
        outdated = false;
    }
}
