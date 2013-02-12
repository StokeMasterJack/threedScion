package c3i.core.imageModel.test;

import c3i.core.imageModel.server.JsonToImJvm;
import c3i.core.imageModel.shared.ImSeries;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.imageModel.shared.SimpleFeatureModel;
import c3i.core.imageModel.shared.SimplePicks;
import com.google.common.io.Resources;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

public class JsonToImJvmTest {

    @Test
    public void test1() throws Exception {

        URL urlIm = Resources.getResource(this.getClass(), "avalon-im.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsImageModel = mapper.readValue(urlIm, JsonNode.class);

        ImSeries im = JsonToImJvm.parse(getSimpleFeatureModel(), jsImageModel);

        ImView exterior = im.getView("exterior");
        assert exterior != null;

        RawImageStack imageStack = exterior.getRawImageStack(getSimplePicks(), 2);
        System.out.println(imageStack);


    }

    private SimplePicks getSimplePicks() throws IOException {
        return new Avalon2014Picks();
    }

    private SimpleFeatureModel getSimpleFeatureModel() throws IOException {
        return new Avalon2014FeatureModel();
    }

    private static Logger log = Logger.getLogger(JsonToImJvmTest.class.getName());


}