package smartClient.client;

import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import junit.framework.TestCase;
import smartsoft.util.lang.shared.ImageSize;

public class TestJpgWidth extends TestCase {

    public void test1() throws Exception {
        JpgWidth jpgWidth = new JpgWidth(200);
        System.out.println(jpgWidth.getJpgSize(ImageSize.STD_PNG));
        System.out.println(jpgWidth.getJpgSize(ImageSize.STD_PNG));
    }

}
