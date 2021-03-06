package c3i.featureModel.shared.common;

import static smartsoft.util.shared.Strings.nullNormalize;

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
