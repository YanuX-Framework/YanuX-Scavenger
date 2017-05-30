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

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.AbstractFileOutput;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.StorageType;

public class PCM16Recorder extends AbstractFileOutput {
    public static final String LOG_TAG = "WAVRECORDER";
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final String DEFAULT_FILENAME = "audio.pcm";

    private int sampleRate;
    private boolean recording;
    private FileChannel fileChannel;

    public PCM16Recorder(Context context, int sampleRate, String directory, String filename, StorageType storageType) {
        super(context, directory, filename, storageType);
        this.sampleRate = sampleRate;
        this.recording = false;
    }

    public PCM16Recorder(Context context, int sampleRate, String directory, String filename) {
        this(context, sampleRate, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public PCM16Recorder(Context context) {
        this(context, DEFAULT_SAMPLE_RATE, DEFAULT_DIRECTORY, DEFAULT_FILENAME);
    }

    public boolean isRecording() {
        return recording;
    }

    public void record() throws IOException {
        open();
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
                ByteBuffer audioBuffer = ByteBuffer.allocateDirect(bufferSize);
                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                                    sampleRate,
                                                    AudioFormat.CHANNEL_IN_MONO,
                                                    AudioFormat.ENCODING_PCM_16BIT,
                                                    bufferSize);
                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();
                recording = true;
                Log.v(LOG_TAG, "Start recording");
                try {
                    while (recording) {
                        audioBuffer.clear();
                        int read = record.read(audioBuffer, bufferSize, AudioRecord.READ_BLOCKING);
                        if(read > 0) {
                            int written = fileChannel.write(audioBuffer);
                            Log.v(LOG_TAG, String.format("Recorded %d bytes and written %d bytes", read, written));
                        }
                    }
                    close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    record.stop();
                    record.release();
                    Log.v(LOG_TAG, String.format("Recording stopped"));
                }
            }
        }).start();
    }

    public void stop() throws IOException {
        recording = false;
    }

    @Override
    public void open() throws IOException {
        super.open();
        new FileOutputStream(file, false).close();
        fileChannel = new FileOutputStream(file, true).getChannel();
    }

    @Override
    public void close() throws IOException {
        fileChannel.close();
        super.close();
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return directory + "/" + filename;
    }

    public String getStorageDirectory() {
        return Environment.getExternalStorageDirectory() + "/" + directory;
    }

    public String getStoragePath() {
        return getStorageDirectory() + "/" + filename;
    }
}
