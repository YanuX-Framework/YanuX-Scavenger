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
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.logging.AbstractFileLogger;

public class WavRecorder {
    public static final String LOG_TAG = "WAVRECORDER";
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final String DEFAULT_PATH = AbstractFileLogger.DEFAULT_DIRECTORY+"/record.wav";

    private int sampleRate;
    private String path;
    private boolean recording;


    public WavRecorder(int sampleRate, String path) {
        this.sampleRate = sampleRate;
        this.path = path;
        this.recording = false;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                                                      AudioFormat.CHANNEL_IN_MONO,
                                                      AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                                  sampleRate,
                                                  AudioFormat.CHANNEL_IN_MONO,
                                                  AudioFormat.ENCODING_PCM_16BIT,
                                                  bufferSize);
    }

    public WavRecorder() {
        this(DEFAULT_SAMPLE_RATE, DEFAULT_PATH);
    }

    public boolean isRecording() {
        return recording;
    }

    public void record() {
        this.recording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                                                             AudioFormat.CHANNEL_IN_MONO,
                                                             AudioFormat.ENCODING_PCM_16BIT);
                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = sampleRate * 2;
                }
                short[] audioBuffer = new short[bufferSize / 2];
                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                                                    sampleRate,
                                                    AudioFormat.CHANNEL_IN_MONO,
                                                    AudioFormat.ENCODING_PCM_16BIT,
                                                    bufferSize);
                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();
                Log.v(LOG_TAG, "Start recording");
                long shortsRead = 0;
                while (recording) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    // Do something with the audioBuffer
                }
                record.stop();
                record.release();
                Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }

    public void stop() {
        this.recording = false;
    }
}
