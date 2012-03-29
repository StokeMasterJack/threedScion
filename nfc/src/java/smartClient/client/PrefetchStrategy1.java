package smartClient.client;

import com.google.common.collect.ImmutableList;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.client.ImageUrlProvider;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.Slice;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class PrefetchStrategy1 implements PrefetchStrategy {

    private final ImageUrlProvider imageUrlProvider;
    private final ViewStates viewStates;
    private final JpgWidth jpgWidth;

    public PrefetchStrategy1(JpgWidth jpgWidth, ImageUrlProvider imageUrlProvider, ViewStates viewStates) {
        this.jpgWidth = jpgWidth;
        this.imageUrlProvider = imageUrlProvider;
        this.viewStates = viewStates;
    }

    public List<Path> getPrefetchUrls() {
        UrlListBuilder urlListBuilder = new UrlListBuilder(viewStates);
        return urlListBuilder.getUrls();
    }


    private class UrlListBuilder {

        private final List<Path> urls = new ArrayList<Path>();

        private final ViewStates viewStatesCopy;

        private UrlListBuilder(ViewStates viewStates) {

            this.viewStatesCopy = new ViewStates(viewStates);

            int span = 6;
            for (int i = 0; i < span; i++) {
                viewStatesCopy.previousAngle();
                addUrl();
            }

            for (int i = 0; i < span; i++) {
                viewStatesCopy.nextAngle();
            }

            for (int i = 0; i < span; i++) {
                viewStatesCopy.nextAngle();
                addUrl();
            }

            viewStatesCopy.nextView();
            addUrl();

        }

        public List<Path> getUrls() {
            return urls;
        }

        private void addUrl() {
            Slice state = viewStatesCopy.getCurrentSlice();
            ImageStack imageStack = imageUrlProvider.getImageUrl(state);
            ImmutableList<Path> urls = imageStack.getUrlListExploded(jpgWidth);
            for (int i = 0; i < urls.size(); i++) {
                Path url = urls.get(i);
                this.urls.add(url);
            }
        }


    }

}