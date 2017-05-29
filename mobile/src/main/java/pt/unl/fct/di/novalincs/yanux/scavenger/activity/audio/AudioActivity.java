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

import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.ToneGenerator;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.InputFilterMinMax;

public class AudioActivity extends AppCompatActivity {

    private Switch toneSwitch;
    private SeekBar toneFrequencySeekBar;
    private EditText toneFrequencyEditText;
    private EditText toneDurationEditText;
    private EditText toneIntervalEditText;

    private ToneGenerator toneGenerator;
    private int toneInterval;
    private Handler toneIntervalHandler;
    private AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        toneSwitch = (Switch) findViewById(R.id.audio_tone_switch);

        toneFrequencySeekBar = (SeekBar) findViewById(R.id.audio_tone_frequency_seek_bar);
        toneGenerator = new ToneGenerator();
        toneGenerator.setFrequency(toneFrequencySeekBar.getProgress());

        toneFrequencyEditText = (EditText) findViewById(R.id.audio_tone_frequency_edit_text);
        toneFrequencyEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, toneFrequencySeekBar.getMax())});
        toneFrequencyEditText.setText(Integer.toString(toneFrequencySeekBar.getProgress()));

        toneFrequencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(Integer.parseInt(s.toString()), toneGenerator.getDuration());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneDurationEditText = (EditText) findViewById(R.id.audio_tone_duration_edit_text);
        toneGenerator.setDuration(Integer.parseInt(toneDurationEditText.getText().toString()));
        toneDurationEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax(Integer.toString(4),
                        toneDurationEditText.getText().toString())
        });
        toneDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(toneGenerator.getFrequency(), Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        toneIntervalEditText = (EditText) findViewById(R.id.audio_tone_interval_edit_text);
        toneInterval = Integer.parseInt(toneIntervalEditText.getText().toString());
        toneIntervalHandler = new Handler();
        toneIntervalEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax(Integer.toString(0),
                        toneIntervalEditText.getText().toString())
        });

        toneIntervalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    toneInterval = Integer.parseInt(s.toString());
                    updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration());
        toneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration());
            }
        });
        toneFrequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTone(progress, toneGenerator.getDuration());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateTone(toneGenerator.getFrequency(), toneGenerator.getDuration());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioTrack != null) {
            audioTrack.stop();
            toneIntervalHandler.removeCallbacksAndMessages(null);
        }
    }

    private void updateTone(double toneFrequency, int duration) {
        toneGenerator.setFrequency(toneFrequency);
        toneGenerator.setDuration(duration);
        if (toneFrequencySeekBar.getProgress() != (int) toneFrequency) {
            toneFrequencySeekBar.setProgress((int) toneFrequency);
        }
        if (Double.parseDouble(toneFrequencyEditText.getText().toString()) != toneFrequency) {
            toneFrequencyEditText.setText(Integer.toString((int) toneFrequency));
        }

        audioTrack = toneGenerator.getTone();
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
                }, toneInterval);
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
            }
        });
        if (toneSwitch.isChecked()) {
            audioTrack.play();
        } else if (audioTrack != null) {
            audioTrack.pause();
        }
    }
}
