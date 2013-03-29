package c3i.jpgSets;

import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.Slice;

import java.io.File;

/**
 *  JpgSet-2012 avalon-48095f947bed6b46c670aa415a039504cb86a1ff-exterior-1
 */
public class JpgSetKey {

    private static final String JPG_SET = "JpgSet";

    private final SeriesId seriesId;
    private final Slice slice;

    public JpgSetKey(SeriesId seriesId, Slice slice) {
        this.seriesId = seriesId;
        this.slice = slice;
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public Slice getSlice() {
        return slice;
    }

    public String getKey() {
        return JPG_SET + "-" + seriesId.toString() + "-" + slice.getView() + "-" + slice.getAngle();
    }

    public File getFileName(File cacheDir) {
        return new File(cacheDir, getKey() + ".txt");
    }

    public String getView() {
        return slice.getViewName();
    }

    public int getAngle() {
        return slice.getAngle();
    }
}
