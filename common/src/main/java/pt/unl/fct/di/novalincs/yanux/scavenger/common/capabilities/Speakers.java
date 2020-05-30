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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Speakers {
    private SpeakersType type;
    private Integer channels;
    private Integer bitDepth;
    private Double samplingRate;

    public SpeakersType getType() {
        return type;
    }

    public void setType(SpeakersType type) {
        this.type = type;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public Integer getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(Integer bitDepth) {
        this.bitDepth = bitDepth;
    }

    public Double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(Double samplingRate) {
        this.samplingRate = samplingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speakers speakers = (Speakers) o;
        return type == speakers.type &&
                Objects.equals(channels, speakers.channels) &&
                Objects.equals(bitDepth, speakers.bitDepth) &&
                Objects.equals(samplingRate, speakers.samplingRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, channels, bitDepth, samplingRate);
    }
}
