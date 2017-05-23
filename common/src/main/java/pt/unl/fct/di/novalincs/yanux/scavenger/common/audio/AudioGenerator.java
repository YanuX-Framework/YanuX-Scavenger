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
    public static final int DEFAULT_FREQUENCY = 440;
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_DURATION = 1;
    public static final int DEFAULT_DURATION_MS = DEFAULT_DURATION * 1000;

    private static final String TAG = "AUDIO_GENERATOR";

    private int frequency;

    private boolean streaming;
    private AudioTrack currentStream;

    public AudioGenerator(int frequency) {
        this.frequency = frequency;
        this.streaming = false;
    }

    public AudioGenerator() {
        this(DEFAULT_FREQUENCY);
    }

    private short[] generateSinWave(int numSamples){
        short[] samples = new short[numSamples];
        for(int i = 0; i < numSamples; i++){
            samples[i] = (short)(Math.sin(2 * Math.PI * i / ((float) DEFAULT_SAMPLE_RATE / (float) frequency)) * 0x7FFF);
        }
        return samples;
    }

    public AudioTrack generateTone(int duration, boolean loop) {
        short[] wave = generateSinWave(duration / 1000 * DEFAULT_SAMPLE_RATE);
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                                          DEFAULT_SAMPLE_RATE,
                                          AudioFormat.CHANNEL_OUT_MONO,
                                          AudioFormat.ENCODING_PCM_16BIT,
                                          wave.length * (Short.SIZE / 8),
                                          AudioTrack.MODE_STATIC);
        track.write(wave, 0, wave.length);
        if(loop) {
            track.setLoopPoints(0, track.getBufferSizeInFrames(),-1);
        }
        return track;
    }

    public AudioTrack generateTone() {
        return this.generateTone(DEFAULT_DURATION_MS, true);
    }

    public AudioTrack getStream() {
        final int bufferSize = AudioTrack.getMinBufferSize(DEFAULT_SAMPLE_RATE,
                                                           AudioFormat.CHANNEL_OUT_MONO,
                                                           AudioFormat.ENCODING_PCM_16BIT);

        currentStream = new AudioTrack(AudioManager.STREAM_MUSIC,
                                       DEFAULT_SAMPLE_RATE,
                                       AudioFormat.CHANNEL_OUT_MONO,
                                       AudioFormat.ENCODING_PCM_16BIT,
                                       bufferSize,
                                       AudioTrack.MODE_STREAM);

        final Runnable generator = new Runnable() {
            public void run() {
                //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                while(streaming){
                    if(currentStream.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        Log.d(TAG, "Buffer Size in Frames: " + currentStream.getBufferSizeInFrames());
                        short[] wave = generateSinWave(DEFAULT_SAMPLE_RATE*2);
                        currentStream.write(wave, 0, wave.length, AudioTrack.WRITE_BLOCKING);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                currentStream.release();
            }
        };

        streaming = true;
        Thread generatorThread = new Thread(generator);
        generatorThread.start();

        currentStream.addOnRoutingChangedListener(new AudioTrack.OnRoutingChangedListener() {
            @Override
            public void onRoutingChanged(AudioTrack audioTrack) {
                switch (audioTrack.getPlayState()) {
                    case AudioTrack.PLAYSTATE_PLAYING:
                        streaming = true;
                        break;
                    case AudioTrack.PLAYSTATE_PAUSED:
                        streaming = true;
                        break;
                    case AudioTrack.PLAYSTATE_STOPPED:
                    default:
                        streaming = false;
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

        return currentStream;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        currentStream.flush();
        this.frequency = frequency;
    }

    public boolean isStreaming() {
        return streaming;
    }
}
