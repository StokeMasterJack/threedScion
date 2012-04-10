package threed.repo.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Series implements Serializable {

    private static final long serialVersionUID = -5354000371116445237L;
    private String seriesName;
    private ArrayList<Integer> years = new ArrayList<Integer>();

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getName() {
        return seriesName;
    }

    public void addYear(Integer year) {
        years.add(year);
    }

    public ArrayList<Integer> getYears() {
        if (years != null) {
            Collections.sort(years);
        }
        return years;
    }
}
