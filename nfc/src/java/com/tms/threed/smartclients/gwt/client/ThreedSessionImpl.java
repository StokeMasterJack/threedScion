package com.tms.threed.smartClients.gwt.client;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.events2.ValueChangeHandlers;
import smartsoft.util.lang.shared.Path;

import java.util.Collection;
import java.util.List;

public class ThreedSessionImpl {

    private final ValueChangeHandlers<List<Path>> urlChangeHandlers;

    private final Path repoBaseUrl;
    private final SeriesKey seriesKey;
    private final RootTreeId vtc;

//    private final ThreedModelClient service;

    private ThreedModel threedModel;
    private SeriesInfo seriesInfo;
    private ViewStates viewStates;

    private ImmutableSet<String> picksRaw;
    private ImmutableSet<Var> picks;
    private FixResult fixResult;

    private Slice slice;


    private String viewName;
    private int angle;

    private JpgWidth jpgWidth;

    private List<Path> urls;

    public ThreedSessionImpl(Path repoBaseUrl, SeriesKey seriesKey, RootTreeId vtc) {

        this.repoBaseUrl = repoBaseUrl;
        this.seriesKey = seriesKey;
        this.vtc = vtc;

//        service = new ThreedModelClient(repoBaseUrl);
//        service.fetchThreedModel2(seriesKey, vtc, new ThreedModelClient.Callback() {
//
//            @Override
//            public void onThreeModelReceived(ThreedModel response) {
//                threedModel = response;
//                seriesInfo = threedModel.getSeriesInfo();
//                viewStates = new ViewStates(seriesInfo);
//            }
//
//        });

        urlChangeHandlers = new ValueChangeHandlers<List<Path>>(this);

    }

    public Slice getSlice() {
        return slice;
    }

    public void setSlice(Slice slice) {
        this.slice = slice;
    }

    public Collection<String> getPicksRaw() {
        return picksRaw;
    }

    public void setPicksRaw(ImmutableSet<String> newPicksRaw) {
        if (Objects.equal(newPicksRaw, this.picksRaw)) {
            return;
        }

        ImmutableSet<Var> newPicks = threedModel.fixRaw(newPicksRaw);

        if (Objects.equal(newPicks, this.picks)) {
            return;
        }

        this.picks = newPicks;

        FixResult newFixResult = threedModel.fixup(newPicks);
        if (Objects.equal(newFixResult, this.fixResult)) {
            return;
        }

        this.fixResult = newFixResult;

        ImageStack imageStack = threedModel.getImageStack(viewName, angle, fixResult.getAssignments());

        ImmutableList<Path> newUrls = imageStack.getUrlListExploded(jpgWidth);

        if (Objects.equal(newUrls, this.urls)) {
            return;
        }

        this.urls = newUrls;
        fireUrlChangeEvent();

    }


    private void fireUrlChangeEvent() {
        urlChangeHandlers.fire(urls);
    }

    public HandlerRegistration addUrlChangeHandler(ValueChangeHandler<List<Path>> handler) {
        return urlChangeHandlers.addValueChangeHandler(handler);
    }

}
