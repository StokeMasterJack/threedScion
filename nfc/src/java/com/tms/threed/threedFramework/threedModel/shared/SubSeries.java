package com.tms.threed.threedFramework.threedModel.shared;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.nullNormalize;

public class SubSeries {

    private final String label;
    private final Integer year;

    public SubSeries(String label, int year) {
        this.label = nullNormalize(label);
        if (year == -1) {
            this.year = null;
        } else {
            this.year = year;
        }
    }

    public String getLabel() {
        return label;
    }

    public Integer getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "SubSeries{" +
                "label='" + label + '\'' +
                ", year=" + year +
                '}';
    }
}
