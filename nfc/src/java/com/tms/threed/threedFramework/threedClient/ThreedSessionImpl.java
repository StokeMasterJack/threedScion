package com.tms.threed.threedFramework.threedClient;

import com.google.common.base.Objects;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.Fixer;
import com.tms.threed.threedFramework.featureModel.shared.UnknownVarCodeException;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.ImageStack;
import com.tms.threed.threedFramework.previewPane.client.ThreedModelServiceJson;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedModel.shared.*;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.util.gwtUtil.client.events2.ValueChangeHandlers;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ThreedSessionImpl {



    private final ValueChangeHandlers<List<Path>> urlChangeHandlers;

    private final Path repoBaseUrl;
    private final SeriesKey seriesKey;
    private final RootTreeId vtc;

    private final ThreedModelServiceJson service;

    private ThreedModel threedModel;
    private SeriesInfo seriesInfo;
    private ViewStates viewStates;

    private Collection<String> picks;
    private Collection<Var> vars;
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

        service = new ThreedModelServiceJson(repoBaseUrl);
        service.fetchThreedModel2(seriesKey, vtc, new ThreedModelServiceJson.Callback() {

            @Override
            public void onThreeModelReceived(ThreedModel response) {
                threedModel = response;
                seriesInfo = threedModel.getSeriesInfo();
                viewStates = new ViewStates(seriesInfo);
            }

        });

        urlChangeHandlers = new ValueChangeHandlers<List<Path>>(this);

    }

    public Slice getSlice() {
        return slice;
    }

    public void setSlice(Slice slice) {
        this.slice = slice;
    }

    public Collection<String> getPicks() {
        return picks;
    }

    public void setPicks(Collection<String> newPicks) {
        if (Objects.equal(newPicks, this.picks)) {
            return;
        }
        this.picks = newPicks;


        Collection<Var> newVars = new ArrayList<Var>();
        for (String varCode : newPicks) {
            try {
                Var var = threedModel.getFeatureModel().get(varCode);
                newVars.add(var);
            } catch (UnknownVarCodeException e) {
                //ignore
            }
        }

        if (Objects.equal(newVars, this.vars)) {
            return;
        }
        this.vars = newVars;


        fixResult = Fixer.fix(threedModel.getFeatureModel(), newVars);

        ImageStack imageStack = (ImageStack) threedModel.getImageStack(viewName, angle, fixResult, jpgWidth);

        List<Path> newUrls = imageStack.getUrlsJpgMode();

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
