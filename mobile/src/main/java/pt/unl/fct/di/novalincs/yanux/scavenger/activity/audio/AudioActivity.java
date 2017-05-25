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
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.AudioGenerator;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.InputFilterMinMax;

public class AudioActivity extends AppCompatActivity {

    private Switch toneSwitch;
    private SeekBar toneFrequencySeekBar;
    private EditText toneFrequencyEditText;

    private AudioGenerator audioGenerator;
    private AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        toneSwitch = (Switch) findViewById(R.id.audio_tone_switch);
        toneFrequencySeekBar = (SeekBar) findViewById(R.id.audio_tone_frequency_seek_bar);
        toneFrequencyEditText = (EditText) findViewById(R.id.audio_tone_frequency_edit_text);

        audioGenerator = new AudioGenerator(toneFrequencySeekBar.getProgress());
        toneFrequencyEditText.setText(Integer.toString(toneFrequencySeekBar.getProgress()));
        toneFrequencyEditText.setFilters(new InputFilter[]{
                new InputFilterMinMax(Integer.toString(0),
                        Integer.toString(toneFrequencySeekBar.getMax()))
        });

        toneFrequencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    updateTone(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        audioTrack = audioGenerator.getTone(1000, true);
        updateTone(toneFrequencySeekBar.getProgress());

        toneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTone(toneFrequencySeekBar.getProgress());
            }
        });

        toneFrequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTone(progress);
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
        updateTone(audioGenerator.getFrequency());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioTrack != null) {
            audioTrack.pause();
        }
    }

    private void updateTone(float toneFrequency) {
        audioGenerator.setFrequency(toneFrequency);
        if (toneFrequencySeekBar.getProgress() != (int) toneFrequency) {
            toneFrequencySeekBar.setProgress((int) toneFrequency);
        }
        if (Float.parseFloat(toneFrequencyEditText.getText().toString()) != toneFrequency) {
            toneFrequencyEditText.setText(Integer.toString((int) toneFrequency));
        }
        audioTrack = audioGenerator.getTone(1000, true);
        if (toneSwitch.isChecked()) {
            audioTrack.play();
        } else if (audioTrack != null) {
            audioTrack.pause();
        }
    }
}
