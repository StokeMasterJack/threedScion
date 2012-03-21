package com.tms.threed.previewPanel.client;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.gwt.client.rpc2.CompleteCb;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;

public class ImageLoader {

    private static enum Status {NotStarted, Loading, CompleteSuccess, CompleteError, Open}

    private final Path url;
    private final CompleteCb callback;

    private final Image image = new Image();
    private final HandlerRegistration loadReg;
    private final HandlerRegistration errorReg;
    private Status status = Status.Open;

    private ArrayList<Path> errors;

    public ImageLoader(Path url, CompleteCb callback) {
        this.url = url;
        this.callback = callback;

        image.setVisible(false);

        loadReg = image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                Image img = (Image) event.getSource();
                loadReg.removeHandler();
                errorReg.removeHandler();
                status = Status.CompleteSuccess;
                threedImagePanel.maybeFire(this);
            }
        });
        errorReg = image.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                Image img = (Image) event.getSource();
                img.setVisible(true);
                loadReg.removeHandler();
                errorReg.removeHandler();
                status = Status.CompleteError;
                threedImagePanel.errors.add(url);
                threedImagePanel.maybeFire(this);
            }
        });

        image.setUrl(url.toString());

    }


    boolean isTerminal() {
        return !isOpen();
    }

    boolean isOpen() {
        return status.equals(Status.Open);
    }

    public boolean isError() {
        return status.equals(Status.CompleteError);
    }

    public boolean isLoaded() {
        return status.equals(Status.CompleteSuccess);
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

    public boolean matches(ImageLoader imageTracker) {
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
