package com.tms.threed.smartClients.gwt.client;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.client.ThreadModelLoaders;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.events2.ValueChangeHandlers;
import smartsoft.util.gwt.client.rpc.UiLog;
import smartsoft.util.gwt.client.rpc2.Future;
import smartsoft.util.gwt.client.rpc2.SuccessCb;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreedSessionImpl2 implements ThreedSession {

    private final ArrayList<Future<ViewStates>> viewStatesFutures = new ArrayList<Future<ViewStates>>();

    private final ValueChangeHandlers<List<Path>> urlChangeHandlers;
    private final ValueChangeHandlers<String> displayNameChangeHandlers;
    private final ThreedModelClient threedModelClient;

    private ThreadModelLoaders threedModelLoaders;

    private SeriesKey seriesKey;
    private ThreedModel threedModel;
    private ViewStates viewStates;

    private ImmutableSet<String> picksRaw = ImmutableSet.of();

    private JpgWidth jpgWidth = JpgWidth.W_STD;

    private Path repoBaseUrl;

    private List<Path> urls = ImmutableList.of();

    private FixResult fixResult;

    public ThreedSessionImpl2() {

        repoBaseUrl = new Path("/configurator-content");
        threedModelClient = new ThreedModelClient(uiLog, repoBaseUrl);
        urlChangeHandlers = new ValueChangeHandlers<List<Path>>(this);
        displayNameChangeHandlers = new ValueChangeHandlers<String>(this);

        threedModelLoaders = new ThreadModelLoaders(threedModelClient);


    }

    private static UiLog uiLog = new UiLog() {
        @Override
        public void log(String msg) {
            System.out.println("UiLog.msg: " + msg);
        }
    };

    public void setSeriesKey(final SeriesKey newSeriesKey) {
        Preconditions.checkNotNull(newSeriesKey);

        if (newSeriesKey == this.seriesKey) {
            return;
        }

        if (newSeriesKey.equals(this.seriesKey)) {
            return;
        }

        this.seriesKey = newSeriesKey;

        final Future<ThreedModel> f = threedModelLoaders.ensureLoaded(seriesKey);
        f.success(new SuccessCb() {
            @Override
            public void call() {
                setThreedModel(f.getResult());
            }
        });


    }


    private void setThreedModel(ThreedModel newThreedModel) {
        if (!Objects.equal(this.threedModel, newThreedModel)) {
            this.threedModel = newThreedModel;
            refreshUrls();
            refreshDisplayName();
            viewStates = new ViewStates(threedModel.getSeriesInfo());

            while (viewStatesFutures.size() > 0) {
                Future<ViewStates> f = viewStatesFutures.remove(0);
                f.setResult(viewStates);
            }

        }
    }


    @Override
    public List<ViewKey> getViewKeys() {
        if (threedModel == null) {
            return null;
        } else {
            return Arrays.asList(threedModel.getViewKeys());
        }
    }

    private void refreshUrls() {
        ImmutableList<Path> urls;

        if (seriesKey == null || threedModel == null || picksRaw == null || picksRaw.isEmpty()) {
            urls = ImmutableList.of();
        } else {

            if (jpgWidth == null) {
                jpgWidth = JpgWidth.W_STD;
            }

            Slice currentSlice = viewStates.getCurrentSlice();
            ImageStack imageStack = threedModel.getImageStack(currentSlice, picksRaw);
            urls = imageStack.getUrlListSmart(jpgWidth);

        }


        if (this.urls == null) throw new IllegalStateException();

        if (!this.urls.equals(urls)) {
            this.urls = urls;
            fireImageUrlsChangeEvent();
        }

    }

    private void fireImageUrlsChangeEvent() {
        urlChangeHandlers.fire(this.urls);
    }


    public List<Path> getUrls() {
        return urls;
    }

    public void setSlice(Slice slice) {
        Preconditions.checkNotNull(slice);
        if (viewStates == null) {
            throw new IllegalStateException("Cannot set slice until viewStates is created");
        } else {
            viewStates.setCurrentViewAndAngle(slice.getView(), slice.getAngle());
            refreshUrls();
        }
    }

    private Future<ViewStates> ensureViewStates() {
        Future<ViewStates> f = new Future<ViewStates>();
        if (viewStates == null) {
            viewStatesFutures.add(f);
        } else {
            f.setResult(viewStates);
        }
        return f;
    }

    @Override
    public void setView(final String viewName) {
        Future<ViewStates> f = ensureViewStates();
        f.success(new SuccessCb() {
            @Override
            public void call() {
                viewStates.setCurrentView(viewName);
                refreshUrls();
            }
        });
    }

    @Override
    public String getView() {
        if (viewStates != null) {
            return viewStates.getCurrentView().getName();
        } else {
            return null;
        }
    }

    @Override
    public void setAngle(final int angle) {
        Future<ViewStates> f = ensureViewStates();
        f.success(new SuccessCb() {
            @Override
            public void call() {
                viewStates.setCurrentAngle(angle);
                refreshUrls();
            }
        });
    }

    @Override
    public int getAngle() {
        if (viewStates != null) {
            return viewStates.getCurrentAngle();
        } else {
            return -1;
        }
    }

    public void nextAngle() {
        Future<ViewStates> f = ensureViewStates();
        f.success(new SuccessCb() {
            @Override
            public void call() {
                viewStates.nextAngle();
                refreshUrls();
            }
        });
    }

    public void previousAngle() {
        Future<ViewStates> f = ensureViewStates();
        f.success(new SuccessCb() {
            @Override
            public void call() {
                viewStates.previousAngle();
                refreshUrls();
            }
        });
    }

    public void setPicksRaw(ImmutableSet<String> newPicks) {
        Preconditions.checkNotNull(newPicks);

        if (!Objects.equal(newPicks, this.picksRaw)) {
            this.picksRaw = newPicks;
            if (threedModel == null) {
                this.fixResult = null;
            } else {
                this.fixResult = threedModel.fixupRaw(newPicks);
                refreshUrls();
                refreshDisplayName();
            }

        }


    }

    private String displayName;

    private void refreshDisplayName() {

        String oldDisplayName = this.displayName;
        String newDisplayName;

        if (threedModel == null) {
            newDisplayName = null;
        } else {
            newDisplayName = threedModel.getDisplayName(fixResult);
        }

        boolean equal = Objects.equal(oldDisplayName, newDisplayName);

        if (!equal) {
            this.displayName = newDisplayName;
            fireImageUrlsChangeEvent();
        }
    }

    public HandlerRegistration addUrlChangeHandler(ValueChangeHandler<List<Path>> handler) {
        return urlChangeHandlers.addValueChangeHandler(handler);
    }

    public HandlerRegistration addDisplayNameChangeHandler(ValueChangeHandler<String> handler) {
        return displayNameChangeHandlers.addValueChangeHandler(handler);
    }

    public String getDisplayName() {
        if (threedModel != null) {
            return threedModel.getDisplayName(fixResult);
        } else {
            return null;
        }
    }


}
