package c3i.smartClient.client.service;

import smartsoft.util.lang.shared.RectSize;
import c3i.core.threedModel.shared.JpgWidth;
import junit.framework.TestCase;

public class TestJpgWidth extends TestCase {

    public void test1() throws Exception {
        JpgWidth jpgWidth = new JpgWidth(200);
        System.out.println(jpgWidth.getJpgSize(RectSize.STD_PNG));
        System.out.println(jpgWidth.getJpgSize(RectSize.STD_PNG));
    }

}