package com.tms.threed.smartClients.gwt.client;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.events2.ValueChangeHandlers;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.rpc.UiLog;
import smartsoft.util.lang.shared.Path;

import java.util.*;

public class ThreedSessionImpl2 implements ThreedSession {

    private final ValueChangeHandlers<List<Path>> urlChangeHandlers;
    private final ValueChangeHandlers<String> displayNameChangeHandlers;
    private final ThreedModelClient threedModelClient;


    private VtcMap vtcMap;

    private final Map<SeriesId, ThreedModel> threedModelCache = new HashMap<SeriesId, ThreedModel>();

    private SeriesKey seriesKey;
    private ThreedModel threedModel;
    private ViewStates viewStates;

    //    private Slice slice;
    private ImmutableSet<String> picks;

    private JpgWidth jpgWidth = JpgWidth.W_STD;

    private Path repoBaseUrl;

    private List<Path> urls = ImmutableList.of();

    private FixResult fixResult;

    public ThreedSessionImpl2() {

        repoBaseUrl = new Path("/configurator-content");
        threedModelClient = new ThreedModelClient(uiLog, repoBaseUrl);
        urlChangeHandlers = new ValueChangeHandlers<List<Path>>(this);
        displayNameChangeHandlers = new ValueChangeHandlers<String>(this);
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

        Req<VtcMap> vtcMapRequest = loadVtcMapIfNeeded();
        vtcMapRequest.onSuccess = new SuccessCallback<VtcMap>() {
            @Override
            public void call(Req<VtcMap> request) {
                if (request.result == null) throw new IllegalStateException();
                vtcMap = request.result;

                RootTreeId vtcVersion = vtcMap.getVtcVersion(newSeriesKey);
                if (vtcVersion == null) throw new IllegalStateException();

                final SeriesId seriesId = new SeriesId(newSeriesKey, vtcVersion);
                threedModel = threedModelCache.get(seriesId);
                if (threedModel == null) {
                    Req<ThreedModel> threedModelReq = threedModelClient.fetchThreedModel(seriesId);
                    threedModelReq.onSuccess = new SuccessCallback<ThreedModel>() {
                        @Override
                        public void call(Req<ThreedModel> request) {
                            if (request.result == null) throw new IllegalStateException();
                            threedModelCache.put(seriesId, request.result);
                            threedModel = request.result;
//                            slice = threedModel.getInitialSlice();
                            viewStates = new ViewStates(threedModel.getSeriesInfo());
                            refreshUrls();
                        }
                    };
                } else {
                    refreshUrls();
                }
            }
        };

        vtcMapRequest.onFailure = new FailureCallback<VtcMap>() {
            @Override
            public void call(Req<VtcMap> request) {
                System.out.println(request.exception);
            }
        };


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
        List<Path> urls;

        if (seriesKey == null || threedModel == null || picks == null || picks.isEmpty()) {
            urls = ImmutableList.of();
        } else {

            if (jpgWidth == null) {
                jpgWidth = JpgWidth.W_STD;
            }

            Slice currentSlice = viewStates.getCurrentSlice();
            urls = threedModel.getImageUrls(currentSlice, picks, jpgWidth);

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

    private Req<VtcMap> loadVtcMapIfNeeded() {
        final Req<VtcMap> r = new Req<VtcMap>("loadVtcMapIfNeeded");
        if (vtcMap != null) {
            r.onSuccess(vtcMap);
        } else {
            Req<VtcMap> vtcMapReq = threedModelClient.getVtcMap();
            vtcMapReq.onSuccess = new SuccessCallback<VtcMap>() {
                @Override
                public void call(Req<VtcMap> request) {
                    if (request.result == null) throw new IllegalStateException();
                    r.onSuccess(request.result);
                }
            };

            vtcMapReq.onFailure = new FailureCallback<VtcMap>() {
                @Override
                public void call(Req<VtcMap> request) {
                    request.exception.printStackTrace();
                    r.onFailure(request.exception);
                }
            };
        }

        return r;
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

    @Override
    public void setView(String viewName) {
        if (viewStates != null) {
            viewStates.setCurrentView(viewName);
            refreshUrls();
        } else {
            throw new IllegalStateException();
        }
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
    public void setAngle(int angle) {
        if (viewStates != null) {
            viewStates.setCurrentAngle(angle);
            refreshUrls();
        } else {
            throw new IllegalStateException();
        }
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
        System.out.println("ThreedSessionImpl2.nextAngle");
        if (viewStates != null) {
            viewStates.nextAngle();
            refreshUrls();
        } else {
            throw new IllegalStateException();
        }
    }

    public void previousAngle() {
        if (viewStates != null) {
            viewStates.previousAngle();
            refreshUrls();
        } else {
            throw new IllegalStateException();
        }
    }

    public void setPicks(Set<String> picks) {
        Preconditions.checkNotNull(picks);
        if (!picks.equals(this.picks)) {
            if (picks instanceof ImmutableSet) {
                this.picks = (ImmutableSet<String>) picks;
            } else {
                this.picks = ImmutableSet.copyOf(picks);
            }
            fixResult = threedModel.fixupPicks2(this.picks);
            refreshUrls();
            refreshDisplayName();
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
