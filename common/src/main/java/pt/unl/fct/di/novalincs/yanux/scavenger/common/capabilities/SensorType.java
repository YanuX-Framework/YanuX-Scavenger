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

package pt.unl.fct.di.novalincs.yanux.scavenger.common.capabilities;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SensorType {
    @JsonProperty("gps")
    LOCATION,
    @JsonProperty("accelerometer")
    ACCELEROMETER,
    @JsonProperty("gyroscope")
    GYROSCOPE,
    @JsonProperty("magnetometer")
    MAGNETOMETER,
    @JsonProperty("barometer")
    BAROMETER,
    @JsonProperty("thermometer")
    THERMOMETER,
    @JsonProperty("light")
    LIGHT,
    @JsonProperty("proximity")
    PROXIMITY,
    @JsonProperty("other")
    OTHER,
    @JsonProperty("unknown")
    UNKNOWN
}
