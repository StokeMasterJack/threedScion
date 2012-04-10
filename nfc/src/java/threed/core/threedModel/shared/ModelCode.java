package threed.core.threedModel.shared;

import javax.annotation.Nonnull;

import static smartsoft.util.lang.shared.Strings.isInt;

/**
 *
 * A 4 char code that uniquely identifies a series+trimCode combination.
 *      1st 2 chars represent the series
 *      2nd 2 chars are the specific trim-level ()
 *
 *      Ex:
 *          8642 modelCode:
 *              86  seriesCode (4Runner)
 *              42  trimCode (SR5,V6,4x2)
 *
 * Also, each modelCode has an msrp
 *
 * Sometimes the word model is used to refer to 86 (the series)
 *  and sometimes to refer to 8642 (a specific model trim)
 */
public class ModelCode {

    private final String seriesCode;
    private final String trimCode;

    public ModelCode(@Nonnull String seriesCode, @Nonnull String trimCode) {
        assert seriesCode != null;
        assert seriesCode.length() == 2;
        assert isInt(seriesCode);

        assert trimCode != null;
        assert trimCode.length() == 2;
        assert isInt(trimCode);

        this.seriesCode = seriesCode;
        this.trimCode = trimCode;
    }

    public ModelCode(@Nonnull String code) {
        assert code != null;
        assert code.length() == 4;
        assert isInt(code);

        this.seriesCode = code.substring(0, 2);
        this.trimCode = code.substring(2, 3);
    }

    @Override public String toString() {
        return seriesCode + trimCode;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public String getTrimCode() {
        return trimCode;
    }
}
