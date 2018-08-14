/*
 * Copyright (c) 2018 Pedro Albuquerque Santos.
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

public class LinearChirpWavePCM16Bit extends AbstractWavePCM16Bit implements ISoundWave {

    public static final double DEFAULT_PHASE = 0;
    private double phase;
    private double frequency1;

    public LinearChirpWavePCM16Bit() {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_PHASE, DEFAULT_FREQUENCY, DEFAULT_FREQUENCY, DEFAULT_SAMPLE_RATE);
    }

    public LinearChirpWavePCM16Bit(double frequency0, double frequency1) {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_PHASE, frequency0, frequency1, DEFAULT_SAMPLE_RATE);
    }

    public LinearChirpWavePCM16Bit(double phase, double frequency0, double frequency1) {
        this(DEFAULT_SAMPLE_RATE, phase, frequency0, frequency1, DEFAULT_SAMPLE_RATE);
    }

    public LinearChirpWavePCM16Bit(double frequency0, double frequency1, int samples) {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_PHASE, frequency0, frequency1, samples);
    }

    public LinearChirpWavePCM16Bit(double phase, double frequency0, double frequency1, int samples) {
        this(DEFAULT_SAMPLE_RATE, phase, frequency0, frequency1, samples);
    }

    public LinearChirpWavePCM16Bit(int sampleRate, double phase, double frequency0, double frequency1, int samples) {
        this.sampleRate = sampleRate;
        this.phase = phase;
        this.frequency = frequency0;
        this.samples = samples;
    }

    public double getPhase() {
        return phase;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }

    public double getStartFrequency() {
        return getFrequency();
    }

    public void setStartFrequency(double frequency0) {
        setFrequency(frequency0);
    }

    public double getFinalFrequency() {
        return frequency1;
    }

    public void setFinalFrequency(double frequency1) {
        this.frequency1 = frequency1;
        outdated = true;
    }

    public double getChirpyness() {
        return (frequency1 - frequency) / samples;
    }

    public void setChirpyness(double k) {
        frequency1 = k * samples + frequency;
        outdated = true;
    }

    @Override
    protected void updateData() {
        double k = getChirpyness();
        data = new short[samples];
        for (int i = 0; i < samples; i++) {
            data[i] = (short) (Math.sin(phase + 2 * Math.PI * (i * frequency / sampleRate + k / 2 * Math.pow(i, 2) / sampleRate)) * 0x7FFF);
        }
        super.updateData();
    }
}
