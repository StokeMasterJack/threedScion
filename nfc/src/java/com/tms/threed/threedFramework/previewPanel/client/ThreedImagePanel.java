package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ThreedImagePanel extends Composite {

    private final SimpleEventBus loadingCompleteHandlers = new SimpleEventBus();

    private final AbsolutePanel absPanel = new AbsolutePanel();

    private ImageTracker baseImage;
    private List<ImageTracker> nonBaseImages;
    private ArrayList<Path> errors;

    private final int panelIndex;

    private ImageSize imageSize;

    public ThreedImagePanel(int panelIndex, ImageSize imageSize) {
        this.panelIndex = panelIndex;
        this.imageSize = imageSize;

        absPanel.setPixelSize(imageSize.getWidth(), imageSize.getHeight());

        initWidget(absPanel);

        getElement().setId("gwt-debug-ThreedImagePanel");
    }

    public HandlerRegistration addLoadingCompleteHandler(LoadingCompleteHandler handler) {
        return loadingCompleteHandlers.addHandler(LoadingCompleteEvent.TYPE, handler);
    }


    public void setPlaceHolder(Widget widget) {
        absPanel.clear();
        widget.setSize("100%", "100%");
        absPanel.add(widget, 0, 0);
    }

    public void refreshImageSizes() {
        int widgetCount = absPanel.getWidgetCount();
        for (int i = 0; i < widgetCount; i++) {
            Widget widget = absPanel.getWidget(i);
            if (widget instanceof Image) {
                Image image = (Image) widget;
                image.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
            }
        }
    }

    public void setImageUrls(@Nonnull List<Path> urls) {
        assert urls != null;
        assert assertAllNonNull(urls);

        Console.log("Fetching jpgs: ");
        for (Path url : urls) {
            Console.log("\t" + url);
        }

        if (urls.size() == 0) {
            absPanel.clear();
            baseImage = null;
            nonBaseImages = null;
            errors = new ArrayList<Path>();
        } else if (urls.size() == 1) {
            baseImage = new ImageTracker(urls.get(0));
            nonBaseImages = null;
            errors = new ArrayList<Path>();
        } else {
            baseImage = new ImageTracker(urls.get(0));
            nonBaseImages = new ArrayList<ImageTracker>();
            for (int i = 1; i < urls.size(); i++) {
                nonBaseImages.add(new ImageTracker(urls.get(i)));
            }
            errors = new ArrayList<Path>();
        }


    }

    private boolean isActive(Image image) {
        if (image == null) {
            throw new IllegalStateException("cannot pass null image to isOld");
        }

        if (baseImage == null) {
            return false;
        }

        if (baseImage.matches(image)) {
            return true;
        }

        if (nonBaseImages == null) {
            return false;
        }

        for (ImageTracker nonBaseImage : nonBaseImages) {
            if (nonBaseImage.matches(image)) {
                return true;
            }
        }

        return false;
    }

    public boolean isMain() {
        return panelIndex == 0;
    }

    private void fireImageLoadingCompleteEvent(List<Path> errors, boolean fatal) {
        loadingCompleteHandlers.fireEvent(new LoadingCompleteEvent(errors, fatal));
    }

    private static enum Status {Loaded, Error, Open}

    private int getLoadedCount() {
        int c = 0;
        if (baseImage != null && baseImage.isLoaded()) {
            c++;
        }
        if (nonBaseImages != null) {
            for (ImageTracker nonBaseImage : nonBaseImages) {
                if (nonBaseImage.isLoaded()) {
                    c++;
                }
            }
        }
        return c;
    }


    private int getTerminalCount() {
        return getLoadedCount() + errors.size();
    }

    private boolean isTerminal() {

        return getTerminalCount() == getImageCount();
    }

    private int getImageCount() {
        int c = 0;
        if (baseImage != null) {
            c++;
        }
        if (nonBaseImages != null) {
            c += nonBaseImages.size();
        }
        return c;
    }

    private boolean allImagesHadError() {
        return errors.size() == getImageCount();
    }

    private boolean baseImageHadError() {
        if (baseImage != null) {
            return baseImage.isError();
        } else {
            return false;
        }
    }

    private boolean isFatal() {
        return allImagesHadError() || baseImageHadError();
    }

    private void maybeFire(ImageTracker tracker) {
        if (baseImage == null) {
            return;
        }
        if (tracker.matches(baseImage)) {
            onFirstImageLoaded();
        }
        if (isTerminal()) {
            onAllImagesComplete();
        }
    }

    private void onFirstImageLoaded() {
        clearNonActiveImages();
    }

    protected void onAllImagesComplete() {
        if (isFatal()) {
            show404Message(errors);
        }
        fireImageLoadingCompleteEvent(errors, isFatal());
    }

    private void clearNonActiveImages() {
        int L = absPanel.getWidgetCount();
        ArrayList<Widget> toBeRemoved = new ArrayList<Widget>();
        for (int i = 0; i < L; i++) {
            Widget w = absPanel.getWidget(i);
            if (w instanceof Image && !isActive((Image) w)) {
                toBeRemoved.add(w);
            }
        }

        for (Widget w : toBeRemoved) {
            absPanel.remove(w);
        }
    }

    private class ImageTracker implements LoadHandler, ErrorHandler {

        private final Image image = new Image();
        private final Path url;
        private final HandlerRegistration loadReg;
        private final HandlerRegistration errorReg;
        private Status status = Status.Open;

        private ImageTracker(Path url) {
            this.url = url;
            image.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
            image.setVisible(false);
            loadReg = image.addLoadHandler(this);
            errorReg = image.addErrorHandler(this);
            image.setUrl(url.toString());
            absPanel.add(image, 0, 0);
        }

        @Override public void onLoad(LoadEvent event) {
            Image img = (Image) event.getSource();
            img.setVisible(true);
            loadReg.removeHandler();
            errorReg.removeHandler();
            status = Status.Loaded;
            maybeFire(this);
        }

        @Override public void onError(ErrorEvent event) {
            Image img = (Image) event.getSource();
            img.setVisible(true);
            loadReg.removeHandler();
            errorReg.removeHandler();
            status = Status.Error;
            errors.add(url);
            maybeFire(this);
        }

        boolean isTerminal() {
            return !isOpen();
        }

        boolean isOpen() {
            return status.equals(Status.Open);
        }

        public boolean isError() {
            return status.equals(Status.Error);
        }

        public boolean isLoaded() {
            return status.equals(Status.Loaded);
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

        public boolean matches(ImageTracker imageTracker) {
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
    }

    private void show404Message(String msg) {
        showMessage("404", msg, "#dddddd");
    }

    private void show404Message(List<Path> badUrls) {
        showMessage("404", badUrls.toString(), "#dddddd");
    }


    public void showMessage(String shortMessage, final String longMessage, String color) {
        final InlineLabel label = new InlineLabel(shortMessage);
        label.getElement().getStyle().setPadding(.2, Style.Unit.EM);
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
//        label.getElement().getStyle().setBackgroundColor("yellow");
        final PopupPanel pp = new PopupPanel(true);
        pp.setWidget(new Label(longMessage));
        pp.getElement().getStyle().setZIndex(1000);

        label.addClickHandler(new ClickHandler() {


            @Override public void onClick(ClickEvent event) {
                pp.showRelativeTo(label);
                event.preventDefault();
                event.stopPropagation();
            }
        });

        Grid grid = new Grid(1, 1);
        grid.setWidget(0, 0, label);
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        Style style = grid.getElement().getStyle();
        style.setBackgroundColor(color);
        this.setPlaceHolder(grid);
    }


    private static boolean assertAllNonNull(List<Path> urls) {
        for (Path url : urls) {
            if (url == null) return false;
        }
        return true;
    }

    public void setImageSize(ImageSize imageSize) {
        this.imageSize = imageSize;
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());
        refreshImageSizes();
    }


    public static class LoadingCompleteEvent extends GwtEvent<LoadingCompleteHandler> {

        private final List<Path> errors;
        private final boolean fatal;

        public LoadingCompleteEvent(List<Path> errors, boolean fatal) {
            this.errors = errors;
            this.fatal = fatal;
        }

        public static final Type<LoadingCompleteHandler> TYPE = new Type<LoadingCompleteHandler>();

        @Override public Type<LoadingCompleteHandler> getAssociatedType() {
            return TYPE;
        }

        @Override protected void dispatch(LoadingCompleteHandler handler) {
            handler.onLoadingComplete(this);
        }

        public Collection<Path> getErrors() {
            return Collections.unmodifiableCollection(errors);
        }

        @Override public String toString() {
            return "LoadingCompleteEvent [" + errors + "]";
        }

        public boolean isFatal() {
            return fatal;
        }

        public boolean hasErrors() {
            return errors != null && errors.size() > 0;
        }

    }

    public static interface LoadingCompleteHandler extends EventHandler {
        void onLoadingComplete(LoadingCompleteEvent e);
    }

}
