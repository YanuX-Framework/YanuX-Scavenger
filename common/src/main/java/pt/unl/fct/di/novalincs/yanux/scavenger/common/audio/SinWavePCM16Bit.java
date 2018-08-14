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

public class SinWavePCM16Bit extends AbstractWavePCM16Bit implements ISoundWave {

    public SinWavePCM16Bit() {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_FREQUENCY, DEFAULT_SAMPLE_RATE);
    }

    public SinWavePCM16Bit(double frequency) {
        this(DEFAULT_SAMPLE_RATE, frequency, DEFAULT_SAMPLE_RATE);
    }

    public SinWavePCM16Bit(int sampleRate, double frequency, int samples) {
        this.sampleRate = sampleRate;
        this.frequency = frequency;
        this.samples = samples;
    }

    @Override
    protected void updateData() {
        data = new short[samples];
        for (int i = 0; i < samples; i++) {
            data[i] = (short) (Math.sin(2 * Math.PI * i / (sampleRate / frequency)) * 0x7FFF);
        }
        super.updateData();
    }
}
