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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class ToneGenerator {
    private ISoundWave wave;
    private AudioTrack currentAudioTrack;

    public ToneGenerator() {
        this.wave = new SinWavePCM16Bit();
    }

    public double getFrequency() {
        return wave.getFrequency();
    }

    public void setFrequency(double frequency) {
        if (currentAudioTrack != null) {
            currentAudioTrack.flush();
        }
        wave.setFrequency(frequency);
    }

    public int getDuration() {
        return wave.getDuration();
    }

    public void setDuration(int duration) {
        wave.setDuration(duration);
    }

    public AudioTrack getTone() {
        if (currentAudioTrack != null) {
            currentAudioTrack.setPlaybackPositionUpdateListener(null);
            currentAudioTrack.release();
        }
        currentAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                wave.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                wave.getByteSize(),
                AudioTrack.MODE_STATIC);
        currentAudioTrack.write(wave.getData(), 0, wave.getSamples());
        currentAudioTrack.setNotificationMarkerPosition(currentAudioTrack.getBufferSizeInFrames());
        return currentAudioTrack;
    }
}
