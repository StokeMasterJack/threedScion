package c3i.smartClient.client.service;

import c3i.imageModel.shared.JpgWidth;
import junit.framework.TestCase;
import smartsoft.util.shared.RectSize;

public class TestJpgWidth extends TestCase {

    public void test1() throws Exception {
        JpgWidth jpgWidth = new JpgWidth(200);
        System.out.println(jpgWidth.getJpgSize(RectSize.STD_PNG));
        System.out.println(jpgWidth.getJpgSize(RectSize.STD_PNG));
    }

}
