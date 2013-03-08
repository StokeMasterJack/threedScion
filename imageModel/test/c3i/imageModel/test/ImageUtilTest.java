package c3i.imageModel.test;

import c3i.imageModel.server.ImageUtil;
import junit.framework.TestCase;

import java.util.Set;

public class ImageUtilTest extends TestCase {

    public void test() throws Exception {
        Set<String> messageDigests = ImageUtil.getMessageDigestAlgorithms();
        for (String digest : messageDigests) {
            System.out.println("digest = [" + digest + "]");
        }

    }


//    public void testIsEmpty() throws Exception {
//        String s = "/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/pngs/2011/tacoma/exterior/21_Acc-CargoDiv/DI03/Regular/2WD/vr_1_01.png";
//        File f = new File(s);
//        boolean empty = ImageUtil.isEmpty(f);
//        System.out.println("empty = [" + empty + "]");
//    }

    public void test4() throws Exception {
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.update();
//        md.digest()
    }

    public void test5() throws Exception {
        byte[] a = {0, 0, 0, 0};
        System.out.println(byteArrayToInt(a));

    }


    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

}
