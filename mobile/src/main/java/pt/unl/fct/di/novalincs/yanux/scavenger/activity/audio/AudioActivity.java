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
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.audio.AudioGenerator;

public class AudioActivity extends AppCompatActivity {

    private Switch toneSwitch;
    private SeekBar toneFrequencySeekBar;
    private TextView toneFrequencyTextView;

    private int toneFrequency;
    private AudioGenerator audioGenerator;
    private AudioTrack audioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        toneSwitch = (Switch) findViewById(R.id.audio_tone_switch);
        toneFrequencySeekBar = (SeekBar) findViewById(R.id.audio_tone_frequency_seek_bar);
        toneFrequencyTextView = (TextView) findViewById(R.id.audio_tone_frequency_text_view);

        toneFrequency = toneFrequencySeekBar.getProgress();
        audioGenerator = new AudioGenerator(toneFrequency);
        audioTrack = audioGenerator.getStream();
        updateTone();

        toneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTone();
            }
        });

        toneFrequencySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                toneFrequency = progress;
                updateTone();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void updateTone() {
        toneFrequencyTextView.setText(getResources().getText(R.string.audio_tone_frequency)+": "+toneFrequency+" Hz");
        audioGenerator.setFrequency(toneFrequency);
        if(toneSwitch.isChecked()) {
            audioTrack.play();
        } else if(audioTrack != null) {
            audioTrack.pause();
        }
    }
}
