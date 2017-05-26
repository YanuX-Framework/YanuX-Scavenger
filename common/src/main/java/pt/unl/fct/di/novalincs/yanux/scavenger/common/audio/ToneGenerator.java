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
import android.media.AudioTrack;

public class BeepingGenerator {
    private ISoundWave wave;
    private AudioTrack currentAudioTrack;

    public BeepingGenerator(float frequency) {
        this.wave = new SinWavePCM16Bit(frequency);
    }

    public float getFrequency() {
        return wave.getFrequency();
    }

    public void setFrequency(float frequency) {
        currentAudioTrack.flush();
        this.wave.setFrequency(frequency);
        this.wave.setDuration(1000);
    }

    public AudioTrack getTone(int duration, boolean loop) {
        if (currentAudioTrack != null) {
            currentAudioTrack.release();
        }
        currentAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                wave.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                wave.getByteSize(),
                AudioTrack.MODE_STATIC);
        currentAudioTrack.write(wave.getData(), 0, wave.getSamples());
        if (loop) {
            currentAudioTrack.setLoopPoints(0, currentAudioTrack.getBufferSizeInFrames(), -1);
        }
        return currentAudioTrack;
    }

//    /**
//     * NOTE: I'll probably remove this method later on in favor of the implementation which uses
//     * AudioTrack's MODE_STATIC seems to be simpler and easier to manage.
//     *
//     * @return An @{@link AudioTrack} object which playsback a tone.
//     */
//    @Deprecated
//    public AudioTrack getToneStream() {
//        if (currentAudioTrack != null && currentAudioTrackMode == AudioTrack.MODE_STREAM) {
//            return currentAudioTrack;
//        } else {
//            final int bufferSize = AudioTrack.getMinBufferSize(wave.getSampleRate(),
//                    AudioFormat.CHANNEL_OUT_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT);
//
//
//            currentAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                    wave.getSampleRate(),
//                    AudioFormat.CHANNEL_OUT_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    bufferSize,
//                    AudioTrack.MODE_STREAM);
//            currentAudioTrack.addOnRoutingChangedListener(new AudioTrack.OnRoutingChangedListener() {
//                @Override
//                public void onRoutingChanged(AudioTrack audioTrack) {
//                    switch (audioTrack.getPlayState()) {
//                        case AudioTrack.PLAYSTATE_PLAYING:
//                            playing = true;
//                            break;
//                        case AudioTrack.PLAYSTATE_PAUSED:
//                            playing = true;
//                            break;
//                        case AudioTrack.PLAYSTATE_STOPPED:
//                        default:
//                            playing = false;
//                            break;
//                    }
//                }
//
//                @Override
//                public void onRoutingChanged(AudioRouting router) {
//                    if (router instanceof AudioTrack) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            onRoutingChanged((AudioTrack) router);
//                        }
//                    }
//                }
//            }, null);
//
//            final Runnable generator = new Runnable() {
//                public void run() {
//                    while (playing) {
//                        if (currentAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
//                            int result = currentAudioTrack.write(wave.getData(), 0, wave.getSamples());
//                            /* Log.d(TAG, " Write Result: " + result
//                                     + " Length: " + wave.getSamples()
//                                     + " Minimum Buffer Size (Bytes): " + bufferSize
//                                     + " Buffer Size (Frames): " + currentAudioTrack.getBufferSizeInFrames()); */
//                        }
//                    }
//                    currentAudioTrack.release();
//                }
//            };
//
//            Thread generatorThread = new Thread(generator);
//            currentAudioTrackMode = AudioTrack.MODE_STREAM;
//            playing = true;
//            generatorThread.start();
//
//            return currentAudioTrack;
//        }
//    }
}
