package c3i.imageModel.test;

import c3i.core.threedModel.shared.ImFeatureModel;
import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.RawImageStack;
import com.google.common.io.Resources;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class JsonToImJvmTest {

    Avalon2014FeatureModel fmAvalon = new Avalon2014FeatureModel();
    TundraFeatureModel fmtTundra = new TundraFeatureModel();

    Avalon2014Picks picksAvalon = new Avalon2014Picks();
    TundraPicks picksTundra = new TundraPicks();

    @Test
    public void testAvalon() throws Exception {

        ImageModel imageModel = loadImageModel("avalon-im.json", fmAvalon);
        ImView exterior = imageModel.getView("exterior");
        assert exterior != null;

        long t1 = System.currentTimeMillis();
        RawImageStack imageStack = exterior.getRawImageStack(picksAvalon, 2);
        long t2 = System.currentTimeMillis();
        System.out.println(" Delta: " + (t2 - t1));
        System.out.println(imageStack);


    }

    @Test
    public void testTundra() throws Exception {

        ImageModel imageModel = loadImageModel("tundra-im.json", fmtTundra);
        ImView exterior = imageModel.getView("exterior");
        assert exterior != null;

        RawImageStack imageStack = exterior.getRawImageStack(picksTundra, 2);


        System.out.println(imageStack.getAllPngs1());
        System.out.println(imageStack.getBasePngs1());
        System.out.println(imageStack.getZPngs1());

        System.out.println();

        System.out.println(imageStack.getAllPngs2());
        System.out.println(imageStack.getBasePngs2());
        System.out.println(imageStack.getZPngs2());

        System.out.println();

        System.out.println(imageStack.getBasePngs3());

        System.out.println(imageStack.getContextPath());


    }

    public ImageModel loadImageModel(String localResourceName, ImFeatureModel fm) throws IOException {
        URL urlIm = Resources.getResource(this.getClass(), localResourceName);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);
        return JsonToImJvm.parse(fm, jsImageModel);
    }


    private static Logger log = Logger.getLogger(JsonToImJvmTest.class.getName());


}