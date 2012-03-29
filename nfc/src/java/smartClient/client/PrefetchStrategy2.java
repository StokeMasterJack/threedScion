package smartClient.client;

import com.google.common.collect.ImmutableList;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.client.ImageUrlProvider;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.SeriesInfo;
import com.tms.threed.threedCore.threedModel.shared.Slice;
import com.tms.threed.threedCore.threedModel.shared.ViewKey;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class PrefetchStrategy2 implements PrefetchStrategy {

    private final ImageUrlProvider imageUrlProvider;
    private final SeriesInfo seriesInfo;
    private final JpgWidth jpgWidth;

    public PrefetchStrategy2(JpgWidth jpgWidth, ImageUrlProvider imageUrlProvider, ViewStates viewStates) {
        this.jpgWidth = jpgWidth;
        this.imageUrlProvider = imageUrlProvider;
        this.seriesInfo = viewStates.getSeriesInfo();
    }

    public List<Path> getPrefetchUrls() {
        UrlListBuilder urlListBuilder = new UrlListBuilder(seriesInfo);
        return urlListBuilder.getUrls();
    }

    private class UrlListBuilder {

        private final List<Path> urls = new ArrayList<Path>();

        private UrlListBuilder(SeriesInfo seriesInfo) {

            ViewKey[] viewsKeys = seriesInfo.getViewKeys();
            for (int i = 0; i < viewsKeys.length; i++) {
                ViewKey viewsKey = viewsKeys[i];
                for (int angle = 1; angle <= viewsKey.getAngleCount(); angle++) {
                    Slice viewSnap = new Slice(viewsKey.getName(), angle);
                    ImageStack imageStack = imageUrlProvider.getImageUrl(viewSnap);
                    ImmutableList<Path> urls = imageStack.getUrlListExploded(jpgWidth);
                    this.urls.addAll(urls);
                }
            }

        }

        public List<Path> getUrls() {
            return urls;
        }


    }

}