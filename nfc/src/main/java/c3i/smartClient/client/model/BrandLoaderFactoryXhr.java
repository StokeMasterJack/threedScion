package c3i.smartClient.client.model;

import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.Brand;
import c3i.smartClient.client.ThreedConstants;
import c3i.smartClient.client.service.BrandParser;
import c3i.util.shared.futures.Completer;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import smartsoft.util.shared.Path;

import java.util.logging.Logger;

public class BrandLoaderFactoryXhr implements BrandLoaderFactory, ThreedConstants {

    private final BrandKey brandKey;
    private final Path vtcBaseUrl;

    public BrandLoaderFactoryXhr(BrandKey brandKey, Path vtcBaseUrl) {
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
        Path brandBase = getVtcMapBrandBase();
        return brandBase.append(LOCAL_NAME_VTC);
    }

    public BrandLoaderFunction createLoaderFunction() {
        return new BrandLoaderFunctionXhr();
    }


    private class BrandLoaderFunctionXhr implements BrandLoaderFunction {

        @Override
        public void start(BrandKey arg, final Completer<Brand> completer) throws Exception {

            final Path vtcMapUrl = getVtcMapUrl();

            log.info("Start loading VtcMap from url[" + vtcMapUrl + "]...");

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, vtcMapUrl.toString());
            requestBuilder.setCallback(new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, final Response response) {
                    if (response.getStatusCode() != 200) {
                        completer.setException(new RuntimeException("getVtcMap return non-200 response[" + response.getStatusCode() + "]. Response text: " + response.getText()));
                    } else {
                        log.info("Loading VtcMap complete!");

                        log.info("Start parsing VtcMap...");
                        Brand brandInitData = BrandParser.parse(response.getText());
                        log.info("Complete parsing VtcMap");

                        completer.setResult(brandInitData);

                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    completer.setException(exception);
                }

            });

            try {
                requestBuilder.send();
            } catch (RequestException e) {
                e.printStackTrace();
                completer.setException(e);
            }

        }
    }

    private static Logger log = Logger.getLogger(BrandLoaderFactoryXhr.class.getName());
}
