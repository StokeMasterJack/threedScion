package com.tms.threed.threedFramework.threedModel.client;

import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class PrefetchStrategy2 implements PrefetchStrategy {

    private final ImageUrlProvider imageUrlProvider;
    private final SeriesInfo seriesInfo;

    public PrefetchStrategy2(ImageUrlProvider imageUrlProvider, ViewStates viewStates) {
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
                    IImageStack imageStack = imageUrlProvider.getImageUrl(viewSnap);

                    Path jpgUrl = imageStack.getJpgUrl();
                    this.urls.add(jpgUrl);
                }
            }

        }

        public List<Path> getUrls() {
            return urls;
        }


    }

}