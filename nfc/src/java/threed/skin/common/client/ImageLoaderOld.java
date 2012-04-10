package threed.skin.common.client;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

public class ImageLoaderOld {

    private static enum Status {
        NotStarted,
        Loading,
        CompleteSuccess,
        CompleteError;

        boolean isNotStarted() {
            return this.equals(NotStarted);
        }

        boolean isComplete() {
            return isCompleteSuccess() || isCompleteError();
        }

        boolean isCompleteSuccess() {
            return this.equals(CompleteSuccess);
        }

        boolean isCompleteError() {
            return this.equals(CompleteError);
        }
    }


    private final Image image = new Image();

    private final int index;
    private final Path url;
    private final HandlerRegistration loadReg;
    private final HandlerRegistration errorReg;
    private Status status = Status.NotStarted;

    private FinalOutcome finalOutcome;

    public ImageLoaderOld(final int index, final Path url, ImageSize imageSize, final CompleteCallback callback) {
        this.index = index;
        this.url = url;

        image.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
        image.setVisible(false);

        loadReg = image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                status = Status.CompleteSuccess;
                finalOutcome = new FinalOutcome(index, url, true);
                callback.call(finalOutcome);
                loadReg.removeHandler();
                errorReg.removeHandler();
            }
        });

        errorReg = image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                status = Status.CompleteError;
                finalOutcome = new FinalOutcome(index, url, false);
                callback.call(finalOutcome);
                loadReg.removeHandler();
                errorReg.removeHandler();
            }
        });

        image.setUrl(url.toString());

    }

    public Image getImage() {
        return image;
    }

    public int getIndex() {
        return index;
    }


    boolean isComplete() {
        return status.isComplete();
    }

    public boolean isCompleteError() {
        return status.isCompleteError();
    }

    public boolean isCompleteSuccess() {
        return status.isCompleteSuccess();
    }

    public Path getUrl() {
        return url;
    }

    public boolean matches(Image image) {
        if (image == null || image.getUrl() == null) {
            return false;
        }
        return matches(new Path(image.getUrl()));
    }

    public boolean matches(ImageLoaderOld imageTracker) {
        if (imageTracker == null) {
            return false;
        }
        return imageTracker.matches(url);
    }

    public boolean matches(String url) {
        if (url == null) {
            return false;
        }
        return matches(new Path(url));
    }

    public boolean matches(Path url) {
        if (url == null) {
            return false;
        }
        return this.url.equals(url);
    }

    public static class FinalOutcome {

        private final int index;
        private final Path url;
        private final boolean success;

        public FinalOutcome(int index, Path url, boolean success) {
            this.index = index;
            this.url = url;
            this.success = success;
        }

        public Path getUrl() {
            return url;
        }

        public boolean isCompleteError() {
            return !success;
        }

        public boolean isCompleteSuccess() {
            return success;
        }

        public int getIndex() {
            return index;
        }

        public boolean isBase() {
            return index == 0;
        }

        @Override
        public String toString() {
            return url + " " + success;
        }
    }

    public static interface CompleteCallback {
        void call(FinalOutcome finalOutcome);
    }

    public void setVisible() {
        image.setVisible(true);
    }

    @Override
    public String toString() {
        return url + "  status[" + status + "]";
    }

    private static native void daveTest(ImageElement img, String url)/*-{
        img.src = url;
        img.addEventListener("load", function (e) {
            console.log(e);
            console.log(img);
        });

    }-*/;
}
