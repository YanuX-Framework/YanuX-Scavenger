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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.audio;

import android.Manifest;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.ToneGenerator;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.WaveMonoPCM16Recorder;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.permissions.PermissionManager;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.InputFilterMinMax;

public class AudioActivity extends AppCompatActivity {
    public static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.RECORD_AUDIO,
                                                                       Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private PermissionManager permissionManager;
    private ToneGenerator toneGenerator;
    private int toneInterval;
    private Handler toneIntervalHandler;
    private AudioTrack audioTrack;
    private WaveMonoPCM16Recorder wavRecorder;

    private Switch toneSwitch;
    private SeekBar toneFrequencySeekBar;
    private EditText toneFrequencyEditText;
    private EditText toneDurationEditText;
    private EditText toneIntervalEditText;
    private Button tonePlayButton;
    private Button toneRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        permissionManager = new PermissionManager(this);

        toneSwitch = (Switch) findViewById(R.id.audio_tone_switch);
        toneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration(), toneInterval);
            }
        });

        toneFrequencySeekBar = (SeekBar) findViewById(R.id.audio_tone_frequency_seek_bar);
        toneFrequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTone(progress, toneGenerator.getDuration(), toneInterval);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        toneGenerator = new ToneGenerator();
        toneGenerator.setFrequency(toneFrequencySeekBar.getProgress());

        toneFrequencyEditText = (EditText) findViewById(R.id.audio_tone_frequency_edit_text);
        toneFrequencyEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, toneFrequencySeekBar.getMax())});
        toneFrequencyEditText.setText(Integer.toString(toneFrequencySeekBar.getProgress()));
        toneFrequencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(Integer.parseInt(s.toString()), toneGenerator.getDuration(), toneInterval);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        toneDurationEditText = (EditText) findViewById(R.id.audio_tone_duration_edit_text);
        toneGenerator.setDuration(Integer.parseInt(toneDurationEditText.getText().toString()));
        toneDurationEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax(Integer.toString(4),toneDurationEditText.getText().toString())
        });
        toneDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(toneGenerator.getFrequency(), Integer.parseInt(s.toString()), toneInterval);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        toneIntervalEditText = (EditText) findViewById(R.id.audio_tone_interval_edit_text);
        toneInterval = Integer.parseInt(toneIntervalEditText.getText().toString());
        toneIntervalHandler = new Handler();
        toneIntervalEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax(Integer.toString(0), toneIntervalEditText.getText().toString())
        });

        toneIntervalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    toneInterval = Integer.parseInt(s.toString());
                    updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration(), toneInterval);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        tonePlayButton = (Button) findViewById(R.id.audio_tone_play_button);
        tonePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration());
                audioTrack.play();
            }
        });

        permissionManager.requestPermissions(REQUIRED_PERMISSIONS);
        toneRecordButton = (Button) findViewById(R.id.audio_tone_record_button);
        toneRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wavRecorder.isRecording()) {
                    try {
                        wavRecorder.record();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        toneRecordButton.setText(R.string.audio_tone_record_stop);
                    }
                } else {
                    try {
                        wavRecorder.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        toneRecordButton.setText(R.string.audio_tone_record_start);
                    }
                }
            }
        });
        updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration(), toneInterval);
        updateAudioRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration(), toneInterval);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioTrack != null) {
            audioTrack.stop();
            toneIntervalHandler.removeCallbacksAndMessages(null);
        }
        if(wavRecorder != null) {
            try {
                wavRecorder.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if(permissionManager.hasPermissions(REQUIRED_PERMISSIONS)) {
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

    private void updateTone(final double toneFrequency, final int duration) {
        toneGenerator.setFrequency(toneFrequency);
        toneGenerator.setDuration(duration);
        if (toneFrequencySeekBar.getProgress() != (int) toneFrequency) {
            toneFrequencySeekBar.setProgress((int) toneFrequency);
        }
        if (Double.parseDouble(toneFrequencyEditText.getText().toString()) != toneFrequency) {
            toneFrequencyEditText.setText(Integer.toString((int) toneFrequency));
        }
        audioTrack = toneGenerator.getTone();
    }

    private void updateTone(final double toneFrequency, final int duration, final int interval) {
        updateTone(toneFrequency, duration);
        if (toneSwitch.isChecked()) {
            audioTrack.play();
        } else if (audioTrack != null) {
            audioTrack.pause();
        }
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.d("AUDIO_ACTIVITY", "Marker Reached");
                audioTrack.stop();
                toneIntervalHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioTrack.play();
                    }
                }, interval);
            }
            @Override
            public void onPeriodicNotification(AudioTrack track) { }
        });
    }
}
