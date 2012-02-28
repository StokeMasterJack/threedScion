package com.tms.threed.threedFramework.threedModel.shared;

import junit.framework.TestCase;

public class SeriesInfoTest extends TestCase {

    public void test() throws Exception {

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(SeriesKey.SIENNA_2011);

        for(int orientation=1;orientation<=16;orientation++){
            System.out.println(orientation + "[" + seriesInfo.getViewSnapFromOrientation(orientation) + "]");
        }

    }


}
