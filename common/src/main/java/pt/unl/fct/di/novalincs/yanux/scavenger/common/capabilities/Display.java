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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Display {
    private String type;
    private List<Double> size;
    private String orientation;
    private List<Integer> resolution;
    private Integer bitDepth;
    private Double refreshRate;
    private Integer pixelDensity;
    private Double pixelRatio;
    private List<Double> virtualResolution;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getSize() {
        return size;
    }

    public void setSize(List<Double> size) {
        this.size = size;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public List<Integer> getResolution() {
        return resolution;
    }

    public void setResolution(List<Integer> resolution) {
        this.resolution = resolution;
    }

    public Integer getBitDepth() {
        return bitDepth;
    }

    public void setBitDepth(Integer bitDepth) {
        this.bitDepth = bitDepth;
    }

    public Double getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(Double refreshRate) {
        this.refreshRate = refreshRate;
    }

    public Integer getPixelDensity() {
        return pixelDensity;
    }

    public void setPixelDensity(Integer pixelDensity) {
        this.pixelDensity = pixelDensity;
    }

    public Double getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(Double pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public List<Double> getVirtualResolution() {
        return virtualResolution;
    }

    public void setVirtualResolution(List<Double> virtualResolution) {
        this.virtualResolution = virtualResolution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Display display = (Display) o;
        return type.equals(display.type) &&
                Objects.equals(size, display.size) &&
                Objects.equals(orientation, display.orientation) &&
                Objects.equals(resolution, display.resolution) &&
                Objects.equals(bitDepth, display.bitDepth) &&
                Objects.equals(refreshRate, display.refreshRate) &&
                Objects.equals(pixelDensity, display.pixelDensity) &&
                Objects.equals(pixelRatio, display.pixelRatio) &&
                Objects.equals(virtualResolution, display.virtualResolution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, size, orientation, resolution, bitDepth, refreshRate, pixelDensity, pixelRatio, virtualResolution);
    }
}
