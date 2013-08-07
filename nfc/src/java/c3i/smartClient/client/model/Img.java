package c3i.smartClient.client.model;

import c3i.core.imageModel.shared.ImImage;
import c3i.core.imageModel.shared.LayerImage;
import c3i.core.imageModel.shared.PngSpec;
import c3i.smartClient.client.ThreedConstants;
import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.CompleterImpl;
import c3i.util.shared.futures.Future;
import smartsoft.util.shared.Path;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class Img implements Exportable,ThreedConstants {



    private final ImImage imImage;
    private final Path url;
    private final LayerState m;

    private final ImageElement imageElement;


    private final Completer<Img> loader = new CompleterImpl<Img>();

    private final EventListener domEventListener = new EventListener() {
        @Override
        public void onBrowserEvent(Event event) {
            int eventType = event.getTypeInt();
            if (eventType == Event.ONLOAD) {
                cacheImage(url, imageElement);
                loader.setResult(Img.this);
            } else if (eventType == Event.ONERROR) {
                loader.setException(new ImageLoadException(url));
            }
        }
    };

    private Img() {
        throw new UnsupportedOperationException("This is apparently required by gwt-exporter");
    }

    public Img(Path repoBaseUrl, ImImage imImage, LayerState m) {
        Preconditions.checkNotNull(imImage);
        this.imImage = imImage;

//        if (imImage.isScionImage()) {
//            this.url = imImage.getUrl(SCION_IMAGE_REPO_BASE);
//        } else {
//            this.url = imImage.getUrl(repoBaseUrl);
//        }

        this.url = imImage.getUrl(repoBaseUrl);

        this.m = m;

        ImageElement cachedImageElement = cache.get(url);
        if (cachedImageElement != null) {
            //image already cached and loaded
            this.imageElement = cachedImageElement;
            loader.setResult(Img.this);
        } else {
            imageElement = Document.get().createImageElement();
            Event.setEventListener(imageElement, domEventListener);
            Event.sinkEvents(imageElement, Event.ONLOAD | Event.ONERROR);
//            log.log(Level.INFO, "loading: " + url);
            cache.put(url, imageElement);
            this.imageElement.setSrc(url.toString());  //start loading
        }


//        refresh();

    }

    public boolean isPngMode() {
        return m != null;
    }


    /**
     * This map is used to store prefetched images. If a reference is not kept to
     * the prefetched image objects, they can get garbage collected, which
     * sometimes keeps them from getting fully fetched.
     */
    private static HashMap<Path, ImageElement> cache = new HashMap<Path, ImageElement>();

    private static void cacheImage(Path url, ImageElement imageElement) {
        cache.put(url, imageElement);
    }

    private static boolean isCached(Path url) {
        return cache.containsKey(url);
    }

//    public static void maybeCacheImage(ContextImage contextImage) {
//        Path url = contextImage.getUrl();
//        if (!cache.containsKey(url)) {
//            ImageElement imageElement = Document.get().createImageElement();
//            imageElement.setSrc(url.toString());
//            cacheImage(url, imageElement);
//        }
//    }

    /**
     * @return src + " " + stateString
     */
    @Export
    public String toString() {
        return url.toString() + ": " + getStateString();
    }

    @Nonnull
    public Future<Img> ensureLoaded() {
        return loader.getFuture();
    }

    /**
     * @return src + " " + stateString
     */
    @Export
    public String getSrc() {
        return url.toString();
    }

    public Path getUrl() {
        return url;
    }


    @Export
    public boolean isFailed() {
        return loader.getFuture().isFailed();
    }

    @Export
    public boolean isLoaded() {
        return loader.getFuture().isLoaded();
    }

    @Export
    public boolean isLoading() {
        return loader.getFuture().isLoading();
    }

    /**
     *
     * @return !loading (i.e. failed or loaded)
     */
    @Export
    public boolean isComplete() {
        return loader.getFuture().isComplete();
    }


    /**
     * @return 0:FAILED, 1:LOADED or 2:LOADING
     */
    @Export
    public int getStateInt() {
        return loader.getFuture().getState().ordinal();
    }

    /**
     * @return "FAILED", "LOADED" or "LOADING"
     */
    @Export
    public String getStateString() {
        return loader.getFuture().getState().name();
    }

    public void setVisible(boolean newValue) {
        imageElement.getStyle().setVisibility(newValue ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN);
    }

//    public LoadState getState(){
//        if(isLoaded() && isLoaded()) return
//                LoadState.
//                return loader.getFuture().get.ordinal();
//    }

    public static class ImageLoadException extends RuntimeException {

        private final Path url;

        public ImageLoadException(Path url) {
            super(url.toString());
            this.url = url;
        }

        public Path getUrl() {
            return url;
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Img image = (Img) o;

        if (!url.equals(image.url)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public boolean isEnabled() {
        if (m == null) return true;
        if (imImage.isLayerPng()) {
            LayerImage png = (LayerImage) imImage;
            PngSpec srcPng = png.getSrcPng();
            return m.isEnabled(srcPng);
        } else {
            return true;
        }

    }

//    private void refresh() {
//        boolean v = isEnabled();
//        imageElement.getStyle().setVisibility(v ? Style.Visibility.VISIBLE : Style.Visibility.HIDDEN);
//    }
}
