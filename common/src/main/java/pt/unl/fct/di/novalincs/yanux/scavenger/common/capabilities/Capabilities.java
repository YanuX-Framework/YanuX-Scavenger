/*
 * Copyright (c) 2019 Pedro Albuquerque Santos.
 *
 * This file is part of YanuX Scavenger.
 *
 * YanuX Scavenger is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * YanuX Scavenger is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with YanuX Scavenger. If not, see <https://www.gnu.org/licenses/gpl.html>
 */

package pt.unl.fct.di.novalincs.yanux.scavenger.common.capabilities;


import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

public class Capabilities {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + Capabilities.class.getSimpleName();
    private Context context;
    private String type;
    private List<Display> display;

    public Capabilities(Context context) {
        this.context = context;
        this.display = new ArrayList<>();
        this.getCapabilitiesInformation();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private void getCapabilitiesInformation() {
        getDisplayInformation();
        getSpeakersInformation();
        getCameraInformation();
        getMicrophoneInformation();
        getInputInformation();
        getSensorsInformation();
    }

    private void getDisplayInformation() {
        //Get the display manager.
        DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        //For each of the displays in the display manager...
        for (android.view.Display deviceDisplay : displayManager.getDisplays()) {
            //Declare a new display capabilities object.
            Display d = new Display();

            //Declare display metrics
            DisplayMetrics displayMetrics = new DisplayMetrics();
            //Save the display metrics of the current display into the declared variable
            deviceDisplay.getRealMetrics(displayMetrics);

            //Determine the width of the display in inches
            double sizeWidthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            //Determine the height of the display in inches
            double sizeHeightInches = displayMetrics.heightPixels / displayMetrics.ydpi;
            //Determine the diagonal of the display in inches
            double diagonalInches = Math.sqrt(sizeWidthInches * sizeWidthInches + sizeHeightInches * sizeHeightInches);

            //Setting the default type for the device (TODO: I may have to change the classification in the future).
            d.setType("external");

            //Inferring the device type from the default display
            if (deviceDisplay.getDisplayId() == android.view.Display.DEFAULT_DISPLAY) {
                //If larger than 7 inches we consider it to be a tablet
                if (diagonalInches >= 7.0) {
                    setType("tablet");
                }
                // Otherwise it's a smartphone
                else {
                    setType("smartphone");
                }
                //Logging the Device Type
                Log.d(LOG_TAG, "Device Type: " + getType());

                //If this is the default display and it is touch enabled, we consider this display to be a touch screen.
                if (context.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
                    d.setType("touchscreen");
                }
                //Otherwise it's just a regular built-in display without input capabilities.
                else {
                    d.setType("internal");
                }
            } else {
                d.setType("external");
            }
            //Logging the Display Type
            Log.d(LOG_TAG, "Display Type: " + d.getType());

            //Initialize empty array list for the size pair.
            List<Double> size = new ArrayList<>();
            //Convert the width from inches to millimeters and add it to list.
            size.add(sizeWidthInches * 25.4);
            //Convert the height from inches to millimeters and add it to list.
            size.add(sizeHeightInches * 25.4);
            //Save the size to the display object.
            d.setSize(size);
            //Logging Display Size
            Log.d(LOG_TAG, "Display Size: " + d.getSize());

            //The ORIENTATION can be 0, 90, 180, 270.
            //If the ORIENTATION mod 180 = 0 then the device is upright in portrait mode.
            if (deviceDisplay.getRotation() % 180 == 0) {
                d.setOrientation("portrait");
            }
            //Otherwise is in landscape mode.
            else {
                d.setOrientation("landscape");
            }
            //Logging Display Orientation
            Log.d(LOG_TAG, "Display Orientation: " + d.getOrientation());


            //Initialize empty array list for the resolution pair.
            List<Integer> resolution = new ArrayList<>();
            //Add the width to the list.
            resolution.add(displayMetrics.widthPixels);
            //Add the height to the list.
            resolution.add(displayMetrics.heightPixels);
            //Save the resolution to the display object.
            d.setResolution(resolution);
            //Logging Display Resolution
            Log.d(LOG_TAG, "Display Resolution: " + d.getResolution());

            //TODO: Get color bit depth information.
            //Logging Display Bit Depth
            Log.d(LOG_TAG, "Display Bit Depth: " + d.getBitDepth());

            //Get and save the display's refresh rate.
            d.setRefreshRate(d.getRefreshRate());
            //Logging Display Refresh Rate
            //TODO: Find another way to get the refresh rate since the current one is returning null.
            Log.d(LOG_TAG, "Display Refresh Rate: " + d.getRefreshRate());

            //Get and save the display's pixel density.
            //d.setPixelDensity(displayMetrics.densityDpi);
            d.setPixelDensity(Long.valueOf(Math.round(Math.sqrt(Math.pow(displayMetrics.widthPixels, 2) + Math.pow(displayMetrics.heightPixels, 2)) / diagonalInches)).intValue());
            //Logging Display Pixel Density
            Log.d(LOG_TAG, "Display Pixel Density: " + d.getPixelDensity());

            //Get and save the display's pixel ratio.
            //d.setPixelRatio((double) displayMetrics.density);
            d.setPixelRatio(Math.max(1, d.getPixelDensity() / 150.0));
            //Logging Display Pixel Ration
            Log.d(LOG_TAG, "Display Pixel Ratio: " + d.getPixelRatio());

            //Initialize empty array list for the virtual resolution pair.
            List<Double> virtualResolution = new ArrayList<>();
            //Calculate and add the virtual width to the list.
            virtualResolution.add(displayMetrics.widthPixels / d.getPixelRatio());
            //Calculate and add the virtual height to the list.
            virtualResolution.add(displayMetrics.heightPixels / d.getPixelRatio());
            //Save the virtual resolution to the display object.
            d.setVirtualResolution(virtualResolution);
            //Logging Display Resolution
            Log.d(LOG_TAG, "Display Virtual Resolution: " + d.getVirtualResolution());

            //Add the display to the capabilities array of displays
            display.add(d);
        }
    }

    private void getSpeakersInformation() {

    }

    private void getCameraInformation() {

    }

    private void getMicrophoneInformation() {

    }

    private void getInputInformation() {

    }

    private void getSensorsInformation() {

    }
}