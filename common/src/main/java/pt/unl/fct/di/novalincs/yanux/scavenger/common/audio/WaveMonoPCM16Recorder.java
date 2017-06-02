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
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.AbstractFileOutput;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.file.StorageType;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class WaveMonoPCM16Recorder extends AbstractFileOutput {
    public static final String LOG_TAG = "WAVRECORDER";
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final String DEFAULT_FILENAME = "audio.wav";
    public static final int WAVE_HEADER_SIZE = 44;
    public static final int WAVE_CHANNELS = 1;

    private int sampleRate;
    private boolean recording;
    private FileChannel fileChannel;

    public WaveMonoPCM16Recorder(Context context, int sampleRate, String directory, String filename, StorageType storageType) {
        super(context, directory, filename, storageType);
        this.sampleRate = sampleRate;
        this.recording = false;
    }

    public WaveMonoPCM16Recorder(Context context, int sampleRate, String directory, String filename) {
        this(context, sampleRate, directory, filename, DEFAULT_STORAGE_TYPE);
    }

    public WaveMonoPCM16Recorder(Context context) {
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
                ByteBuffer audioBuffer = ByteBuffer.allocateDirect(bufferSize);
                audioBuffer.order(ByteOrder.LITTLE_ENDIAN);
                short[] readBuffer = new short[bufferSize / Constants.SHORT_BYTES];
                recording = true;
                Log.v(LOG_TAG, "Start recording at native endianness: " + ByteOrder.nativeOrder());
                try {
                    fileChannel.position(WAVE_HEADER_SIZE);
                    int totalSize = 0;
                    while (recording) {
                        audioBuffer.clear();
                        int read;
                        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                            read = record.read(audioBuffer, bufferSize, AudioRecord.READ_BLOCKING);
                        } else {
                            read = record.read(readBuffer, 0, readBuffer.length, AudioRecord.READ_BLOCKING);
                            for (short sample : readBuffer) {
                                audioBuffer.putShort(sample);
                            }
                            audioBuffer.flip();
                        }
                        if (read > 0) {
                            totalSize += fileChannel.write(audioBuffer);
                        }
                    }
                    writeHeader(totalSize);
                    close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    record.stop();
                    record.release();
                    Log.v(LOG_TAG, "Recording stopped");
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
        fileChannel = new FileOutputStream(file, false).getChannel();
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

    private void writeHeader(int rawSize) throws IOException {
        ByteBuffer headerBuffer = ByteBuffer.allocateDirect(WAVE_HEADER_SIZE);
        headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // WAVE Header
        headerBuffer.put("RIFF".getBytes());                                        // Chunk ID
        headerBuffer.putInt((int) fileChannel.size() - 8);                            // Chunk size
        headerBuffer.put("WAVE".getBytes());                                        // Format
        headerBuffer.put("fmt ".getBytes());                                         // Sub-chunk 1 id
        headerBuffer.putInt(16);                                                    // Sub-chunk 1 size
        headerBuffer.putShort((short) 1);                                           // Audio format (1 = PCM)
        headerBuffer.putShort((short) WAVE_CHANNELS);                               // Number of channels
        headerBuffer.putInt(sampleRate);                                            // Sample rate
        headerBuffer.putInt(sampleRate * Constants.SHORT_BYTES * WAVE_CHANNELS);    // Byte rate
        headerBuffer.putShort((short) (Constants.SHORT_BYTES * WAVE_CHANNELS));     // Bytes per sample (all channels)
        headerBuffer.putShort((short) Short.SIZE);                                  // Bits per sample (single channel)
        headerBuffer.put("data".getBytes());                                        // subchunk 2 id
        headerBuffer.putInt(rawSize);                                               // subchunk 2 size

        headerBuffer.flip();
        fileChannel.write(headerBuffer, 0);
    }
}
