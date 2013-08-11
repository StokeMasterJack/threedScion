package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.VtcMap;
import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.JSONPObject;
import org.codehaus.jackson.node.ObjectNode;
import smartsoft.util.shared.RectSize;

import java.util.List;
import java.util.Map;

public class BrandSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectNode toJson(Brand brand) {
        BrandKey brandKey = brand.getBrandKey();
        ObjectNode o = objectMapper.createObjectNode();
        o.put("brandKey", brandKey.toJson());
        o.put("vtcMap", toJson(brand.getVtcMap()));
        o.put("profileMap", toJson(brand.getProfiles()));

        ObjectNode configJson = objectMapper.createObjectNode();
        Map<String,String> configMap = brand.getConfig();
        for (String propName : configMap.keySet()) {
            String propValue = configMap.get(propName);
            configJson.put(propName, propValue);
        }

        o.put("config",configJson);
        return o;
    }

    private ObjectNode toJson(Profiles profiles) {
        ObjectNode o = objectMapper.createObjectNode();
        List<Profile> list = profiles.getList();
        for (Profile profile : list) {
            ObjectNode jsProfile = toJson(profile);
            o.put(profile.getKey(), jsProfile);
        }
        return o;
    }

    private ObjectNode toJson(Profile profile) {
        ObjectNode o = objectMapper.createObjectNode();
        o.put("imageSize", toJson(profile.getImageSize()));
        o.put("baseImageType", profile.getBaseImageType().name());
        return o;
    }

    private ObjectNode toJson(RectSize rectSize) {
        ObjectNode o = objectMapper.createObjectNode();
        o.put("w", rectSize.getWidth());
        o.put("h", rectSize.getHeight());
        return o;
    }


    private ObjectNode toJson(VtcMap vtcMap) {
        ObjectNode o = objectMapper.createObjectNode();
        for (SeriesKey seriesKey : vtcMap.toMap().keySet()) {
            RootTreeId rootTreeId = vtcMap.getRootTreeId(seriesKey);
            o.put(seriesKey.serialize(), rootTreeId.stringValue());
        }
        return o;
    }

}
