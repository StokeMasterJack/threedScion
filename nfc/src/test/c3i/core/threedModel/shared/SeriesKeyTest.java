package c3i.core.threedModel.shared;

import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.SeriesKey;
import junit.framework.TestCase;

public class SeriesKeyTest extends TestCase {

    public void test() throws Exception {

        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA, 2010, "Land Cruiser");
        assertEquals("landcruiser", seriesKey.getSeriesName());

    }


}
