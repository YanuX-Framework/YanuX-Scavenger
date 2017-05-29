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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.audio;

public class SinWavePCM16Bit implements ISoundWave {

    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final float DEFAULT_FREQUENCY = 440;

    private int sampleRate;
    private double frequency;
    private int samples;
    private short[] data;

    public SinWavePCM16Bit() {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_FREQUENCY, DEFAULT_SAMPLE_RATE);
    }

    public SinWavePCM16Bit(int sampleRate) {
        this(sampleRate, DEFAULT_FREQUENCY, sampleRate);
    }

    public SinWavePCM16Bit(float frequency) {
        this(DEFAULT_SAMPLE_RATE, frequency, DEFAULT_SAMPLE_RATE);
    }

    public SinWavePCM16Bit(int sampleRate, float frequency) {
        this(sampleRate, frequency, sampleRate);
    }

    public SinWavePCM16Bit(int sampleRate, int samples) {
        this(sampleRate, DEFAULT_FREQUENCY, samples);
    }

    public SinWavePCM16Bit(int sampleRate, float frequency, int samples) {
        this.sampleRate = sampleRate;
        this.frequency = frequency;
        this.samples = samples;
        updateWave();
    }

    @Override
    public int getSampleRate() {
        return sampleRate;
    }

    @Override
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
        updateWave();

    }

    @Override
    public double getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(double frequency) {
        this.frequency = frequency;
        updateWave();
    }

    @Override
    public int getSamples() {
        return samples;
    }

    @Override
    public void setSamples(int samples) {
        this.samples = samples;
        updateWave();
    }

    @Override
    public int getDuration() {
        return (int) ((double) samples / (double) sampleRate * 1000.0d);
    }

    @Override
    public void setDuration(int duration) {
        this.setSamples((int) ((double) duration / 1000.0d * (double) sampleRate));
    }

    @Override
    public short[] getData() {
        return data;
    }

    @Override
    public int getByteSize() {
        return samples * Short.SIZE / Byte.SIZE;
    }

    private short[] updateWave() {
        data = new short[samples];
        for (int i = 0; i < samples; i++) {
            data[i] = (short) (Math.sin(2 * Math.PI * i / (sampleRate / frequency)) * 0x7FFF);
        }
        return data;
    }
}
