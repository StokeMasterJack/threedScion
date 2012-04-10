package threed.core.threedModel.shared;

import junit.framework.TestCase;

public class SeriesKeyTest extends TestCase {

    public void test() throws Exception {

        SeriesKey seriesKey = new SeriesKey(BrandKey.TOYOTA,2010, "Land Cruiser");
        assertEquals("landcruiser",seriesKey.getName());

    }


}
