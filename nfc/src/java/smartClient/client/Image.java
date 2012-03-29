package smartClient.client;

import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.HashMap;

import static smartsoft.util.lang.shared.Strings.notEmpty;

public class Image implements Exportable {

    private final Path url;
    private final ImageElement imageElement;

    private final Loader<String> loader = new Loader<String>("ImageLoader") {
        protected ImageFuture createFuture(String name) {
            return new ImageFuture();
        }
    };

    private final EventListener domEventListener = new EventListener() {
        @Override
        public void onBrowserEvent(Event event) {
            int eventType = event.getTypeInt();
            if (eventType == Event.ONLOAD) {
                cacheImage(url, imageElement);
                loader.setResult(url.toString());
            } else if (eventType == Event.ONERROR) {
                loader.setException(new ImageLoadException(url));
            }
        }
    };

    private Image() {
        throw new UnsupportedOperationException("This is apparently required by gwt-exporter");
    }

    public Image(Path url) {
        this.url = url;
        Preconditions.checkNotNull(url);
        Preconditions.checkArgument(notEmpty(url.toString()));

        ImageElement cachedImageElement = cache.get(url);
        if (cachedImageElement != null) {
            //image already cached and loaded
            this.imageElement = cachedImageElement;
            loader.setResult(url.toString());
        } else {
            imageElement = Document.get().createImageElement();
            Event.setEventListener(imageElement, domEventListener);
            Event.sinkEvents(imageElement, Event.ONLOAD | Event.ONERROR);
            this.imageElement.setSrc(url.toString());  //start loading
        }
    }


    /**
     * This map is used to store prefetched images. If a reference is not kept to
     * the prefetched image objects, they can get garbage collected, which
     * sometimes keeps them from getting fully fetched.
     */
    private static HashMap<Path, ImageElement> cache = new HashMap<Path, ImageElement>();

    private static void preCacheImage(Path url) {
        new Image(url);
    }

    private static void cacheImage(Path url, ImageElement imageElement) {
        cache.put(url, imageElement);
    }

    private static boolean isCached(Path url) {
        return cache.containsKey(url);
    }

    private static void maybeCacheImage(Path url, ImageElement imageElement) {
        if (!cache.containsKey(url)) {
            cache.put(url, imageElement);
        }
    }


    @Export
    public String toString() {
        return url.toString() + ": " + getStateString();
    }

    @Nonnull
    public ImageFuture ensureLoaded() {
        return (ImageFuture) loader.ensureLoaded();
    }

    @Export
    public String getSrc() {
        return url.toString();
    }

    public Path getUrl() {
        return url;
    }

    @Export
    public boolean isFailed() {
        return loader.isFailed();
    }

    public Future.State getState() {
        return loader.getState();
    }

    @Export
    public int getStateInt() {
        return loader.getState().ordinal();
    }

    @Export
    public String getStateString() {
        return loader.getState().toString();
    }


}
