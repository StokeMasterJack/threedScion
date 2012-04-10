package threed.jpgGen.shared;

import threed.core.threedModel.shared.JpgWidth;
import threed.core.threedModel.shared.SeriesId;

import java.io.Serializable;

public class JobSpec implements Serializable {

    private /* final */ SeriesId seriesId;
    private  /* final */ JpgWidth jpgWidth;

    public JobSpec(SeriesId seriesId, JpgWidth jpgWidth) {
        this.seriesId = seriesId;
        this.jpgWidth = jpgWidth;
    }

    private JobSpec() {
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSpec jobSpec = (JobSpec) o;

        if (!jpgWidth.equals(jobSpec.jpgWidth)) return false;
        if (!seriesId.equals(jobSpec.seriesId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesId.hashCode();
        result = 31 * result + jpgWidth.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return seriesId.toString() + " " + jpgWidth.toString();
    }
}
