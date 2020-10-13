/*
 * Copyright (c) 2020 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.audio;

import android.Manifest;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.io.IOException;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.LinearChirpToneGenerator;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.WaveMonoPCM16Recorder;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class AudioActivity extends AppCompatActivity {
    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            //TODO: This was part of the old legacy storage system. Remove it in the future.
            //Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String LOG_TAG = Constants.LOG_TAG + "_AUDIO_ACTIVITY";
    private PermissionManager permissionManager;
    private LinearChirpToneGenerator toneGenerator;
    private int toneInterval;
    private Handler toneIntervalHandler;
    private AudioTrack audioTrack;
    private WaveMonoPCM16Recorder wavRecorder;

    private SwitchCompat toneSwitch;
    private EditText toneFrequency0EditText;
    private EditText toneFrequency1EditText;
    private EditText toneDurationEditText;
    private EditText toneIntervalEditText;
    private EditText toneRecordFilenameEditText;
    private Button tonePlayButton;
    private Button toneRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_audio);
        permissionManager = new PermissionManager(this);

        toneSwitch = findViewById(R.id.audio_tone_cycle_switch);
        toneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTone(toneGenerator.getFrequency0(), toneGenerator.getFrequency1(), toneGenerator.getDuration(), toneInterval);
            }
        });

        toneGenerator = new LinearChirpToneGenerator();
        toneFrequency0EditText = findViewById(R.id.audio_tone_frequency0_edit_text);
        toneFrequency0EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(Integer.parseInt(s.toString()),
                            toneGenerator.getFrequency1(),
                            toneGenerator.getDuration(),
                            toneInterval);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneFrequency1EditText = findViewById(R.id.audio_tone_frequency1_edit_text);
        toneFrequency1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(toneGenerator.getFrequency0(),
                            Integer.parseInt(s.toString()),
                            toneGenerator.getDuration(),
                            toneInterval);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneDurationEditText = findViewById(R.id.audio_tone_duration_edit_text);
        toneGenerator.setDuration(Integer.parseInt(toneDurationEditText.getText().toString()));
        toneDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(toneGenerator.getFrequency0(),
                            toneGenerator.getFrequency1(),
                            Integer.parseInt(s.toString()),
                            toneInterval);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneIntervalEditText = findViewById(R.id.audio_tone_interval_edit_text);
        toneInterval = Integer.parseInt(toneIntervalEditText.getText().toString());
        toneIntervalHandler = new Handler();

        toneIntervalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    toneInterval = Integer.parseInt(s.toString());
                    updateTone(toneGenerator.getFrequency0(),
                            toneGenerator.getFrequency1(),
                            toneGenerator.getDuration(),
                            toneInterval);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneRecordFilenameEditText = findViewById(R.id.audio_recording_filename_edit_text);

        tonePlayButton = findViewById(R.id.audio_tone_play_button);
        tonePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                    updateTone(toneGenerator.getFrequency0(),
                            toneGenerator.getFrequency1(),
                            toneGenerator.getDuration());
                    audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                        @Override
                        public void onMarkerReached(AudioTrack track) {
                            stopTone();
                        }

                        @Override
                        public void onPeriodicNotification(AudioTrack track) {
                        }
                    });
                    audioTrack.play();
                    tonePlayButton.setText(R.string.audio_tone_stop);
                } else {
                    stopTone();
                }
            }
        });

        permissionManager.requestPermissions(REQUIRED_PERMISSIONS);
        toneRecordButton = findViewById(R.id.audio_tone_record_button);
        toneRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wavRecorder.isRecording()) {
                    try {
                        wavRecorder.setFilename(toneRecordFilenameEditText.getText().toString());
                        wavRecorder.record();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        toneRecordButton.setText(R.string.audio_tone_record_stop);
                    }
                } else {
                    wavRecorder.stop();
                    toneRecordButton.setText(R.string.audio_tone_record_start);
                }
            }
        });
        updateTone(Integer.parseInt(toneFrequency0EditText.getText().toString()),
                Integer.parseInt(toneFrequency1EditText.getText().toString()),
                Integer.parseInt(toneDurationEditText.getText().toString()),
                Integer.parseInt(toneIntervalEditText.getText().toString()));
        updateAudioRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateTone(toneGenerator.getFrequency0(),
                toneGenerator.getFrequency1(),
                toneGenerator.getDuration(),
                toneInterval);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioTrack != null) {
            stopTone();
        }
        if (wavRecorder != null) {
            wavRecorder.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionManager.REQUEST_MULTIPLE_PERMISSIONS:
                if (PermissionManager.werePermissionsGranted(grantResults)) {
                    Toast.makeText(getApplicationContext(), R.string.multiple_permission_allowed, Toast.LENGTH_SHORT).show();
                    updateAudioRecording();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.multiple_permission_denied, Toast.LENGTH_SHORT).show();
                    updateAudioRecording();
                }
                break;
            default:
                break;
        }
    }

    private void updateAudioRecording() {
        if (permissionManager.hasPermissions(REQUIRED_PERMISSIONS)) {
            enableAudioRecording();
        } else {
            disableAudioRecording();
        }
    }

    private void enableAudioRecording() {
        toneRecordButton.setEnabled(true);
        wavRecorder = new WaveMonoPCM16Recorder(this);
    }

    private void disableAudioRecording() {
        toneRecordButton.setEnabled(false);
    }

    private void updateTone(final double toneFrequency0, final double toneFrequency1, final int duration) {
        toneGenerator.setFrequency0(toneFrequency0);
        toneGenerator.setFrequency1(toneFrequency1);
        toneGenerator.setDuration(duration);
        audioTrack = toneGenerator.getTone();
    }

    private void updateTone(final double toneFrequency0, final double toneFrequency1, final int duration, final int interval) {
        updateTone(toneFrequency0, toneFrequency1, duration);
        if (toneSwitch.isChecked()) {
            audioTrack.play();
            toneFrequency0EditText.setEnabled(false);
            toneFrequency1EditText.setEnabled(false);
            toneDurationEditText.setEnabled(false);
            toneIntervalEditText.setEnabled(false);
            toneRecordFilenameEditText.setEnabled(false);
        } else if (audioTrack != null) {
            toneFrequency0EditText.setEnabled(true);
            toneFrequency1EditText.setEnabled(true);
            toneDurationEditText.setEnabled(true);
            toneIntervalEditText.setEnabled(true);
            toneRecordFilenameEditText.setEnabled(true);
            audioTrack.pause();
        }
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.v(LOG_TAG, "Marker Reached!");
                if (toneSwitch.isChecked()) {
                    audioTrack.stop();
                    toneIntervalHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            audioTrack.play();
                        }
                    }, interval);
                } else {
                    stopTone();
                }
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
            }
        });
    }

    private void stopTone() {
        audioTrack.stop();
        audioTrack.setPlaybackPositionUpdateListener(null);
        toneIntervalHandler.removeCallbacksAndMessages(null);
        tonePlayButton.setText(R.string.audio_tone_play);
    }
}
