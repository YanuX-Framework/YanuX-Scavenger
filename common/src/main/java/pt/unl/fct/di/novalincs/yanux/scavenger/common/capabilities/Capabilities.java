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
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Sensor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.ImageReader;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.InputDevice;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import pt.unl.fct.di.novalincs.yanux.scavenger.common.sensors.SensorCollector;
import pt.unl.fct.di.novalincs.yanux.scavenger.common.utilities.Constants;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Capabilities {
    private static final String LOG_TAG = Constants.LOG_TAG + "_" + Capabilities.class.getSimpleName();
    private Context context;
    private DeviceType type;
    private Set<Display> display;
    private Set<Speakers> speakers;
    private Set<Camera> camera;
    private Set<Microphone> microphone;
    private Set<InputType> input;
    private Set<SensorType> sensors;

    public Capabilities(Context context) {
        this.context = context;
        this.display = new LinkedHashSet<>();
        this.speakers = new LinkedHashSet<>();
        this.camera = new LinkedHashSet<>();
        this.microphone = new LinkedHashSet<>();
        this.input = new LinkedHashSet<>();
        this.sensors = new LinkedHashSet<>();
        this.getCapabilitiesInformation();
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public Set<Display> getDisplay() {
        return display;
    }

    public void setDisplay(Set<Display> display) {
        this.display = display;
    }

    public Set<Speakers> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Speakers> speakers) {
        this.speakers = speakers;
    }

    public Set<Camera> getCamera() {
        return camera;
    }

    public void setCamera(Set<Camera> camera) {
        this.camera = camera;
    }

    public Set<Microphone> getMicrophone() {
        return microphone;
    }

    public void setMicrophone(Set<Microphone> microphone) {
        this.microphone = microphone;
    }

    public Set<InputType> getInput() {
        return input;
    }

    public void setInput(Set<InputType> input) {
        this.input = input;
    }

    public Set<SensorType> getSensors() {
        return sensors;
    }

    public void setSensors(Set<SensorType> sensors) {
        this.sensors = sensors;
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
            d.setType(DisplayType.EXTERNAL);

            //Inferring the device type from the default display
            if (deviceDisplay.getDisplayId() == android.view.Display.DEFAULT_DISPLAY) {
                // If it has feature watch than it's a watch!
                if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH)) {
                    setType(DeviceType.SMARTWATCH);
                }
                //If larger than 7 inches we consider it to be a tablet
                if (diagonalInches >= 7.0) {
                    setType(DeviceType.TABLET);
                }
                // Otherwise it's a smartphone
                else {
                    setType(DeviceType.SMARTPHONE);
                }
                //Logging the Device Type
                Log.d(LOG_TAG, "Device Type: " + getType());

                //If this is the default display and it is touch enabled, we consider this display to be a touch screen.
                if (context.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
                    d.setType(DisplayType.TOUCHSCREEN);
                }
                //Otherwise it's just a regular built-in display without input capabilities.
                else {
                    d.setType(DisplayType.INTERNAL);
                }
            } else {
                d.setType(DisplayType.EXTERNAL);
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
                d.setOrientation(DisplayOrientation.PORTRAIT);
            }
            //Otherwise is in landscape mode.
            else {
                d.setOrientation(DisplayOrientation.LANDSCAPE);
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

    //TODO: Detect when speakers/headphones/headsets/etc. are added or removed to update the current devices' capabilities automatically
    //https://developer.android.com/reference/android/media/AudioManager.html#ACTION_AUDIO_BECOMING_NOISY
    //https://developer.android.com/guide/topics/media-apps/volume-and-earphones#becoming-noisy
    private void getSpeakersInformation() {
        //Get the audio manager to get information about speakers.
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //Get the information about each speaker.
        for (AudioDeviceInfo adi : audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {
            //Log the audio device information.
            logAudioDevice(adi);

            //Init the speakers information object
            Speakers s = new Speakers();
            //Set the speakers type accordingly
            //TODO: What other device types should I consider?
            switch (adi.getType()) {
                case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:
                    s.setType(SpeakersType.LOUDSPEAKER);
                    break;
                case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                    s.setType(SpeakersType.HEADPHONES);
                    break;
                case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:
                    //case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:
                    s.setType(SpeakersType.BLUETOOTH);
                    break;
                default:
                    s.setType(null);
                    break;
            }
            //If the type is set get the remaining details, otherwise don't add the speakers since they are probably "garbage"!
            if (s.getType() != null) {
                //If there are channels counts
                if (adi.getChannelCounts().length != 0) {
                    //Get the maximum value and set it to the speakers object
                    s.setChannels(Collections.max(Arrays.asList(ArrayUtils.toObject(adi.getChannelCounts()))));
                }
                //If there are sample rates
                if (adi.getSampleRates().length != 0) {
                    //Get the maximum value and set it to the speakers object
                    s.setSamplingRate(Collections.max(Arrays.asList(ArrayUtils.toObject(adi.getSampleRates()))).doubleValue());
                }
                //Get the supported encodings
                List<Integer> encodings = Arrays.asList(ArrayUtils.toObject(adi.getEncodings()));
                //Check if encodings contains each of the relevant PCM formats and set the bit depth accordingly.
                if (encodings.contains(AudioFormat.ENCODING_PCM_FLOAT)) {
                    s.setBitDepth(24);
                } else if (encodings.contains(AudioFormat.ENCODING_PCM_16BIT)) {
                    s.setBitDepth(16);
                } else if (encodings.contains(AudioFormat.ENCODING_PCM_8BIT)) {
                    s.setBitDepth(8);
                }
                //Add the speakers object to speakers list
                speakers.add(s);
            }
        }
    }

    private void getCameraInformation() {
        //Get the camera manager
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            //Iterate over each camera
            // TODO: Add proper support to the new Multi-camera API which should enable access to physical cameras (e.g., a back camera may be composed of a main camera and a telephoto camera)
            for (String cameraId : manager.getCameraIdList()) {
                //Init the camera object
                Camera c = new Camera();
                // Get the camera characteristics
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
                // Log the camera id
                Log.d(LOG_TAG, "[ Camera Id: " + cameraId + " ]");
                // Check the type of camera based on where it's facing
                Integer cameraType = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                // Null check on the camera type information.
                if (cameraType != null) {
                    //Switch on the camera type
                    switch (cameraType) {
                        //If it's a back facing camera
                        case CameraCharacteristics.LENS_FACING_BACK:
                            //Log it
                            Log.d(LOG_TAG, "Back Camera");
                            //And set the type
                            c.setType(CameraType.BACK);
                            break;
                        //If it's a front facing camera
                        case CameraCharacteristics.LENS_FACING_FRONT:
                            //Log it
                            Log.d(LOG_TAG, "Front Camera");
                            //And set the type
                            c.setType(CameraType.FRONT);
                            break;
                        //If it's an external camera
                        case CameraCharacteristics.LENS_FACING_EXTERNAL:
                            //Log it
                            Log.d(LOG_TAG, "External Camera");
                            //And set the type
                            c.setType(CameraType.EXTERNAL);
                            break;
                        //Otherwise, just ignore the camera
                        default:
                            //But log it as unknown anyway!
                            Log.d(LOG_TAG, "Unknown Camera");
                            break;
                    }
                }
                //Check if the camera type is set. If that's the case get the remaining details about the camera.
                if (c.getType() != null) {
                    //Get a map of the configurations supported by the camera to get information from it.
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    //Get an int[] of the output format codes and convert it into a List<Integer> to make it easier to manipulate.
                    List<Integer> outputFormats = Arrays.asList(ArrayUtils.toObject(streamConfigurationMap.getOutputFormats()));
                    //A variable to store the code of the best/top of the line format supported by the camera among a couple of options.
                    int preferredFormat;
                    //Check if RAW sensor information (16-bit per channel) is supported
                    if (outputFormats.contains(ImageFormat.RAW_SENSOR)) {
                        //If it is, save that as the preferred format
                        preferredFormat = ImageFormat.RAW_SENSOR;
                        //And set the bit depth accordingly
                        c.setBitDepth(48);
                        //Otherwise, check if RAW 12-bit per channel is supported.
                    } else if (outputFormats.contains(ImageFormat.RAW12)) {
                        //If it is, save that as the preferred format
                        preferredFormat = ImageFormat.RAW12;
                        //And set the bit depth accordingly
                        c.setBitDepth(36);
                        //Otherwise, check if RAW 10-bit per channel is supported.
                    } else if (outputFormats.contains(ImageFormat.RAW10)) {
                        //If it is, save that as the preferred format
                        preferredFormat = ImageFormat.RAW10;
                        //And set the bit depth accordingly
                        c.setBitDepth(30);
                        //Otherwise, check if all else fails check if the JPEG format (8-bit per channel) is supported, which it should always be.
                    } else if (outputFormats.contains(ImageFormat.JPEG)) {
                        //If it is, save that as the preferred format
                        preferredFormat = ImageFormat.JPEG;
                        //And set the bit depth accordingly
                        c.setBitDepth(24);
                    }

                    //Get the supported camera output sizes.
                    Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                    //Initialize a list to store the camera resolution information.
                    List<Integer> resolution = new ArrayList<>();
                    //Add the width to the first position.
                    resolution.add(sizes[0].getWidth());
                    //Add the height to the second.
                    resolution.add(sizes[0].getHeight());
                    //Set the resolution of the camera object.
                    c.setResolution(resolution);

                    //Get the minimum time between camera frames in nanoseconds.
                    long minFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(ImageReader.class, sizes[0]);

                    //If the value is known (different from 0)
                    if (minFrameDuration != 0) {
                        //Convert it to FPS (frames per second) and add it to the camera object;
                        c.setRefreshRate(1.0e9 / streamConfigurationMap.getOutputMinFrameDuration(ImageReader.class, sizes[0]));
                    }

                    //Just log all of the information that we just gathered
                    Log.d(LOG_TAG, "Resolution: " + c.getResolution());
                    Log.d(LOG_TAG, "Bit Depth: " + c.getBitDepth());
                    Log.d(LOG_TAG, "Refresh Rate : " + c.getRefreshRate());
                    //Add the camera to the list of cameras
                    camera.add(c);
                }
            }
        } catch (CameraAccessException e) {
            Log.e(LOG_TAG, "Camera Exception: " + e);
        }
    }

    //TODO: Detect when microphones/headsets/etc. are added or removed to update the current devices' capabilities automatically
    //https://developer.android.com/reference/android/media/AudioManager.html#ACTION_AUDIO_BECOMING_NOISY
    //https://developer.android.com/guide/topics/media-apps/volume-and-earphones#becoming-noisy
    private void getMicrophoneInformation() {
        //Get the audio manager to get information about microphones.
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //Get the information about each microphone.
        for (AudioDeviceInfo adi : audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)) {
            //Init the microphone information object
            Microphone m = new Microphone();
            //If the type is builtin microphone proceed. Otherwise, ignore this audio device.
            //TODO: What other device types should I consider?
            if (adi.getType() == AudioDeviceInfo.TYPE_BUILTIN_MIC) {
                //Log the audio device information.
                logAudioDevice(adi);
                //If the channel masks support stereo, then we assume that we can record stereo sound.
                if (Arrays.asList(ArrayUtils.toObject(adi.getChannelMasks())).contains(AudioFormat.CHANNEL_IN_STEREO)) {
                    m.setChannels(2);
                    //Otherwise, default to mono sound.
                } else {
                    m.setChannels(1);
                }

                //If there are sample rates
                if (adi.getSampleRates().length != 0) {
                    //Get the maximum value and set it to the microphone object
                    m.setSamplingRate(Collections.max(Arrays.asList(ArrayUtils.toObject(adi.getSampleRates()))).doubleValue());
                }

                //Get the supported encodings
                List<Integer> encodings = Arrays.asList(ArrayUtils.toObject(adi.getEncodings()));
                //Check if encodings contains each of the relevant PCM formats and set the bit depth accordingly.
                if (encodings.contains(AudioFormat.ENCODING_PCM_FLOAT)) {
                    m.setBitDepth(24);
                } else if (encodings.contains(AudioFormat.ENCODING_PCM_16BIT)) {
                    m.setBitDepth(16);
                } else if (encodings.contains(AudioFormat.ENCODING_PCM_8BIT)) {
                    m.setBitDepth(8);
                }
                //Add the microphone object to the microphones list
                microphone.add(m);
                //Exit the loop
                //TODO: Find a more a elegant way of dealing with duplicated internal microphones. Besises, how do I access other types of microphones reliably?
                break;
            }
        }
    }

    //TODO: Detect when keyboards/mice/styluses/etc. are added or removed to update the current devices' capabilities automatically
    //https://developer.android.com/reference/android/hardware/input/InputManager.InputDeviceListener.html
    private void getInputInformation() {
        InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        Log.d(LOG_TAG, "[ Keyboard: " + context.getResources().getConfiguration().keyboard + " Keyboard Hidden: " + context.getResources().getConfiguration().hardKeyboardHidden + " ]");
        if (context.getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY
                && context.getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Log.d(LOG_TAG, "Input Type: Keyboard");
            input.add(InputType.KEYBOARD);
        }
        for (int inputDevId : inputManager.getInputDeviceIds()) {
            InputDevice inputDev = inputManager.getInputDevice(inputDevId);
            Log.d(LOG_TAG, "[ Input Id:" + inputDev.getId() + " Name: " + inputDev.getName() + " ]");
            if ((inputDev.getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
                Log.d(LOG_TAG, "Input Type: Mouse");
                input.add(InputType.MOUSE);
            }
            if ((inputDev.getSources() & InputDevice.SOURCE_BLUETOOTH_STYLUS) == InputDevice.SOURCE_BLUETOOTH_STYLUS) {
                Log.d(LOG_TAG, "Input Type: Stylus");
                input.add(InputType.STYLUS);
            }
            if ((inputDev.getSources() & InputDevice.SOURCE_TOUCHSCREEN) == InputDevice.SOURCE_TOUCHSCREEN) {
                Log.d(LOG_TAG, "Input Type: Touchscreen");
                input.add(InputType.TOUCHSCREEN);
            }
        }
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.d(LOG_TAG, "Input Type: Speech Input");
            input.add(InputType.SPEECH_INPUT);
        }
    }

    private void getSensorsInformation() {
        //Check if the device supports reportins its location.
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
            sensors.add(SensorType.LOCATION);
        }
        //Instiate the SensorCollector that was previously developed for other purposes.
        SensorCollector sensorCollector = new SensorCollector(context);
        //Add check if each of the sensors we are most interested in are present.
        //If so, add them to the list of sensors.

        //Accelerometer
        if (sensorCollector.getSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            sensors.add(SensorType.ACCELEROMETER);
        }

        //Gyroscope
        if (sensorCollector.getSensor(Sensor.TYPE_GYROSCOPE) != null) {
            sensors.add(SensorType.GYROSCOPE);
        }

        //Magnetometer
        if (sensorCollector.getSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            sensors.add(SensorType.MAGNETOMETER);
        }

        //Barometer
        if (sensorCollector.getSensor(Sensor.TYPE_PRESSURE) != null) {
            sensors.add(SensorType.BAROMETER);
        }

        //Thermometer
        if (sensorCollector.getSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            sensors.add(SensorType.THERMOMETER);
        }

        //Light
        if (sensorCollector.getSensor(Sensor.TYPE_LIGHT) != null) {
            sensors.add(SensorType.LIGHT);
        }

        //Proximity
        if (sensorCollector.getSensor(Sensor.TYPE_PROXIMITY) != null) {
            sensors.add(SensorType.PROXIMITY);
        }
    }

    private void logAudioDevice(AudioDeviceInfo adi) {
        //A string builder used to support logging.
        StringBuilder sb = new StringBuilder();
        //Log general information about speakers
        sb.append("[ Id: " + adi.getId() + " Product Name: " + adi.getProductName() + " Type: " + adi.getType() + " ]");
        Log.d(LOG_TAG, sb.toString());

        //Log the channel counts
        sb = new StringBuilder();
        sb.append("Channel Counts: ");
        for (int i = 0; i < adi.getChannelCounts().length; i++) {
            sb.append(adi.getChannelCounts()[i]);
            if (i < adi.getChannelCounts().length - 1) {
                sb.append(", ");
            }
        }
        Log.d(LOG_TAG, sb.toString());

        //Log the supported sample rates
        sb = new StringBuilder();
        sb.append("Sample Rates: ");
        for (int i = 0; i < adi.getSampleRates().length; i++) {
            sb.append(adi.getSampleRates()[i]);
            if (i < adi.getSampleRates().length - 1) {
                sb.append(", ");
            }
        }
        Log.d(LOG_TAG, sb.toString());

        //Log the supported channel masks
        sb = new StringBuilder();
        sb.append("Channel Masks: ");
        for (int i = 0; i < adi.getChannelMasks().length; i++) {
            sb.append(adi.getChannelMasks()[i]);
            if (i < adi.getChannelMasks().length - 1) {
                sb.append(", ");
            }
        }
        Log.d(LOG_TAG, sb.toString());

        //Log the supported channel index masks
        sb = new StringBuilder();
        sb.append("Channel Index Masks: ");
        for (int i = 0; i < adi.getChannelIndexMasks().length; i++) {
            sb.append(adi.getChannelIndexMasks()[i]);
            if (i < adi.getChannelIndexMasks().length - 1) {
                sb.append(", ");
            }
        }
        Log.d(LOG_TAG, sb.toString());

        //Log supported encodings
        sb = new StringBuilder();
        sb.append("Encodings: ");
        for (int i = 0; i < adi.getEncodings().length; i++) {
            sb.append(adi.getEncodings()[i]);
            if (i < adi.getEncodings().length - 1) {
                sb.append(", ");
            }
        }

        //Log the string builder
        Log.d(LOG_TAG, sb.toString());
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return toJsonString();
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }

    public String toJsonString() throws JsonProcessingException {
        return Constants.OBJECT_MAPPER.writeValueAsString(this);
    }

    public void saveToFile(File file) throws IOException {
        Constants.OBJECT_MAPPER.writeValue(file, this);
    }
}
