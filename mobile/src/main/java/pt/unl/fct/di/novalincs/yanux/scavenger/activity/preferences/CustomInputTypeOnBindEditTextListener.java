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

package pt.unl.fct.di.novalincs.yanux.scavenger.activity.preferences;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;

public class CustomInputTypeOnBindEditTextListener implements EditTextPreference.OnBindEditTextListener {
    private int inputType;

    public CustomInputTypeOnBindEditTextListener(int inputType) {
        this.inputType = inputType;
    }

    @Override
    public void onBindEditText(@NonNull EditText editText) {
        editText.setInputType(inputType);
    }
}
