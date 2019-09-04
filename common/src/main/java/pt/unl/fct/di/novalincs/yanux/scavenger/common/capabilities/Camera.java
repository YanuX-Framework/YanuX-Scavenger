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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Camera {
    private CameraType type;
    private List<Integer> resolution;
    private Integer bitDepth;
    private Double refreshRate;

    public CameraType getType() {
        return type;
    }

    public void setType(CameraType type) {
        this.type = type;
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
}
