package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.smartClient.client.ThreedConstants;
import c3i.smartClient.client.service.BrandParser;
import c3i.util.shared.futures.Completer;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import smartsoft.util.shared.Path;

public class BrandLoaderFactoryJsonp implements BrandLoaderFactory, ThreedConstants {

    private final BrandKey brandKey;
    private final Path vtcBaseUrl;

    public BrandLoaderFactoryJsonp(BrandKey brandKey, Path vtcBaseUrl) {
        this.brandKey = brandKey;
        this.vtcBaseUrl = vtcBaseUrl;
    }

    @Override
    public BrandLoader createLoader() {
        BrandLoaderFunction loaderFunction = createLoaderFunction();
        return new BrandLoader(brandKey, loaderFunction);
    }

    public Path getVtcMapBrandBase() {
        return vtcBaseUrl.append(brandKey.getKey());
    }

    public Path getVtcMapUrl() {
        Path base = getVtcMapBrandBase();
        return base.append(LOCAL_NAME_VTC);
    }

    public BrandLoaderFunction createLoaderFunction() {

        return new BrandLoaderFunction() {
            @Override
            public void start(BrandKey arg, final Completer<Brand> completer) throws RuntimeException {

                final Path vtcMapUrl = getVtcMapUrl();

                JsonpRequestBuilder requestBuilder = new JsonpRequestBuilder();
                requestBuilder.requestObject(vtcMapUrl.toString(), new AsyncCallback<JavaScriptObject>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        completer.setException(new RuntimeException("getVtcMap return non-200 response[" + caught + "]"));
                    }

                    @Override
                    public void onSuccess(JavaScriptObject result) {
                        JSONObject jsonObject = new JSONObject(result);
                        Brand brandInitData = BrandParser.parse(jsonObject);
                        completer.setResult(brandInitData);
                    }
                });


            }

        };
    }


}
