package c3i.smartClient.client.service;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.BaseImageType;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.VtcMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.RectSize;

import java.util.ArrayList;
import java.util.Set;

public class BrandParser {

    public static Brand parse(String jsonText) {
        return new BrandParser().parseBrandInit(jsonText);
    }

    public static Brand parse(JSONObject jsBrandInit) {
        return new BrandParser().parseBrandInit(jsBrandInit);
    }

    private Brand parseBrandInit(String jsonText) {
        JSONObject jsBrandInit = JSONParser.parseStrict(jsonText).isObject();
        Brand brandInitData = parseBrandInit(jsBrandInit);
        return brandInitData;
    }

    private Brand parseBrandInit(JSONObject jsBrandInit) {
        Preconditions.checkNotNull(jsBrandInit);
        JSONValue jsBrandKey = jsBrandInit.get("brandKey");
        Preconditions.checkNotNull(jsBrandKey);
        JSONString jssBrandKey = jsBrandKey.isString();
        Preconditions.checkNotNull(jssBrandKey);
        String sBrandKey = jssBrandKey.stringValue();
        BrandKey brandKey = BrandKey.fromString(sBrandKey);
        Preconditions.checkNotNull(sBrandKey);
        JSONObject jsVtcMap = jsBrandInit.get("vtcMap").isObject();
        JSONObject jsProfileMap = jsBrandInit.get("profileMap").isObject();
        VtcMap vtcMap = parseVtcMap(brandKey, jsVtcMap);
        Profiles profiles = parseProfileMap(jsProfileMap);

        return new Brand(brandKey, vtcMap, profiles);
    }

    private VtcMap parseVtcMap(BrandKey brandKey, JSONObject json) {
        ImmutableMap.Builder<SeriesKey, RootTreeId> builder = ImmutableMap.builder();
        Set<String> sSeriesKeys = json.keySet();
        for (String sSeriesKey : sSeriesKeys) {
            SeriesKey seriesKey = SeriesKey.parse(brandKey + " " + sSeriesKey);
            String sRootTreeId = json.get(sSeriesKey).isString().stringValue();
            RootTreeId rootTreeId = new RootTreeId(sRootTreeId);
            builder.put(seriesKey, rootTreeId);
        }
        return new VtcMap(builder.build());
    }

    private Profiles parseProfileMap(JSONObject json) {
        ArrayList<Profile> a = new ArrayList<Profile>();
        Set<String> profileKeys = json.keySet();
        for (String profileKey : profileKeys) {
            JSONObject jsProfile = json.get(profileKey).isObject();
            RectSize image = parseImage(jsProfile.get("imageSize").isObject());
            BaseImageType baseImageType = parseBaseImageType(jsProfile.get("baseImageType").isString());
            Profile profile = new Profile(profileKey, image, baseImageType);
            a.add(profile);
        }
        return new Profiles(a);
    }

    private RectSize parseImage(JSONObject json) {
        JSONValue jsWidth = json.get("w");
        if (jsWidth == null) {
            Console.error("imageSize.w is null in profile");
            throw new IllegalStateException();
        }

        JSONValue jsHeight = json.get("h");
        if (jsHeight == null) {
            Console.error("imageSize.h is null in profile");
            throw new IllegalStateException();
        }

        int w = (int) jsWidth.isNumber().doubleValue();
        int h = (int) jsHeight.isNumber().doubleValue();
        return new RectSize(w, h);
    }

    private BaseImageType parseBaseImageType(JSONString json) {
        String s = json.stringValue();
        return BaseImageType.valueOf(s);
    }

}
