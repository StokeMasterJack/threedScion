package com.tms.threed.threedFramework.threedModel.shared;

import junit.framework.TestCase;

public class SeriesKeyTest extends TestCase {

    public void test() throws Exception {

        SeriesKey seriesKey = new SeriesKey(2010, "Land Cruiser");
        assertEquals("landcruiser",seriesKey.getName());

    }


}
