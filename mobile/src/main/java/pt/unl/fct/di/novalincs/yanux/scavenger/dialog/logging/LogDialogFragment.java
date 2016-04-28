/*
 * Copyright (c) 2016 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger.  If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.dialog.logging;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.preferences.Preferences;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.TextValidator;

public class LogDialogFragment extends DialogFragment {
    private Preferences preferences;
    // Use this instance of the interface to deliver action events
    private LogDialogListener listener;

    private String logName;
    private int samples;

    private AlertDialog alertDialog;
    private EditText logNameEditText;
    private EditText logSamplesEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(R.string.log_settings);

        // Inflate and set the layout for the alertDialog
        // Pass null as the parent view because its going in the alertDialog layout
        final View view = inflater.inflate(R.layout.fragment_log_dialog, null);

        logNameEditText = (EditText) view.findViewById(R.id.log_name);
        logSamplesEditText = (EditText) view.findViewById(R.id.log_samples);

        preferences = new Preferences(getActivity());
        logNameEditText.setText(preferences.getLogName());
        logSamplesEditText.setText(Integer.toString(preferences.getLogSamples()));

        builder.setView(view)
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String logNameInput = logNameEditText.getText().toString().trim();
                        String logSamplesInput = logSamplesEditText.getText().toString().trim();
                        setLogName(logNameInput);
                        setSamples(Integer.parseInt(logSamplesInput));
                        preferences.setLogName(getLogName());
                        preferences.setLogSamples(getSamples());
                        listener.onDialogPositiveClick(LogDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(LogDialogFragment.this);
                    }
                });
        alertDialog = builder.create();
        logNameEditText.addTextChangedListener(new TextValidator(logNameEditText) {
            @Override
            public void validate(TextView textView, String text) {
                validateDialog();
                if (!isValid() && text.trim().isEmpty()) {
                    textView.setError(getActivity().getString(R.string.required));
                }
            }
        });

        logSamplesEditText.addTextChangedListener(new TextValidator(logSamplesEditText) {
            @Override
            public void validate(TextView textView, String text) {
                validateDialog();
                if (!isValid() && text.trim().isEmpty()) {
                    textView.setError(getActivity().getString(R.string.required));
                }
            }
        });
        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        validateDialog();
    }

    // Override the Fragment.onAttach() method to instantiate the LogDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LogDialogListener so we can send events to the host
            listener = (LogDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement LogDialogListener");
        }
    }

    public int getSamples() {
        return samples;
    }

    private void setSamples(int samples) {
        this.samples = samples;
    }

    public String getLogName() {
        return logName;
    }

    private void setLogName(String logName) {
        this.logName = logName;
    }

    public boolean isValid() {
        return !logNameEditText.getText().toString().trim().isEmpty() && !logSamplesEditText.getText().toString().trim().isEmpty();
    }

    private void validateDialog() {
        if (isValid()) {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setClickable(true);
        } else {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setClickable(false);
        }
    }

    /* The activity that creates an instance of this alertDialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface LogDialogListener {
        void onDialogPositiveClick(LogDialogFragment dialog);

        void onDialogNegativeClick(LogDialogFragment dialog);
    }


}
