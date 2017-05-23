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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRouting;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

public class AudioGenerator {
    public static final int SAMPLE_RATE = 44100;
    private static final String TAG = "AUDIO_GENERATOR";

    private int frequency;
    private boolean playing;
    private AudioTrack currentAudioTrack;

    public AudioGenerator(int frequency) {
        this.frequency = frequency;
        this.playing = false;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        currentAudioTrack.flush();
        this.frequency = frequency;
    }

    public boolean isPlaying() {
        return playing;
    }

    public AudioTrack getTone(int duration, boolean loop) {
        if(currentAudioTrack != null) {
            currentAudioTrack.release();
        }
        short[] wave = generateSinWave(duration / 1000 * SAMPLE_RATE);
        currentAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                wave.length * (Short.SIZE / 8),
                AudioTrack.MODE_STATIC);
        currentAudioTrack.write(wave, 0, wave.length);
        if(loop) {
            currentAudioTrack.setLoopPoints(0, currentAudioTrack.getBufferSizeInFrames(),-1);
        }
        return currentAudioTrack;
    }


    /**
     * NOTE: I'll probably remove this method later on in favor of the implementation which uses
     * AudioTrack's MODE_STATIC seems to be simpler and easier to manage.
     *
     * @return An @{@link AudioTrack} object which playsback a tone.
     */
    @Deprecated
    public AudioTrack getToneStream() {
        if(currentAudioTrack != null) {
            return currentAudioTrack;
        }

        final int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);


        currentAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        playing = true;
        final Runnable generator = new Runnable() {
            public void run() {
                while(playing){
                    if(currentAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        short[] wave = generateSinWave(SAMPLE_RATE);
                        int result = currentAudioTrack.write(wave, 0, SAMPLE_RATE);
                        Log.d(TAG, " Write Result: " + result
                                 + " Write Length: " + wave.length
                                 + " Minimum Buffer Size: " + bufferSize
                                 + " Buffer Size in Frames: " + currentAudioTrack.getBufferSizeInFrames());
                    }
                }
                currentAudioTrack.release();
            }
        };

        Thread generatorThread = new Thread(generator);
        generatorThread.start();

        currentAudioTrack.addOnRoutingChangedListener(new AudioTrack.OnRoutingChangedListener() {
            @Override
            public void onRoutingChanged(AudioTrack audioTrack) {
                switch (audioTrack.getPlayState()) {
                    case AudioTrack.PLAYSTATE_PLAYING:
                        playing = true;
                        break;
                    case AudioTrack.PLAYSTATE_PAUSED:
                        playing = true;
                        break;
                    case AudioTrack.PLAYSTATE_STOPPED:
                    default:
                        playing = false;
                        break;
                }
            }
            @Override
            public void onRoutingChanged(AudioRouting router) {
                if (router instanceof AudioTrack) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onRoutingChanged((AudioTrack) router);
                    }
                }
            }

        }, null);

        return currentAudioTrack;
    }

    private short[] generateSinWave(int numSamples){
        short[] samples = new short[numSamples];
        for(int i = 0; i < numSamples; i++){
            samples[i] = (short)(Math.sin(2 * Math.PI * i / ((float) SAMPLE_RATE / (float) frequency)) * 0x7FFF);
        }
        return samples;
    }
}
