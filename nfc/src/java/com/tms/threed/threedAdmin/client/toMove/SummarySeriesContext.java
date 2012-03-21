package com.tms.threed.threedAdmin.client.toMove;

import com.google.common.collect.ImmutableList;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.client.summary.PreviewPanelSummaryContext;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.client.SimplePicks2;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.Browser;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;

public class SummarySeriesContext {

    @Nonnull
    private final PreviewPanelSummaryContext previewPanel;
    @Nonnull
    private final ThreedModel threedModel;

    private SimplePicks2 picks;

    public SummarySeriesContext(@Nonnull ThreedModel threedModel) {
        assert threedModel != null;

        this.threedModel = threedModel;
        this.previewPanel = new PreviewPanelSummaryContext();

        this.previewPanel.setSeriesInfo(threedModel.getSeriesInfo());

        previewPanel.addAngleChangeHandler(new AngleChangeHandler() {
            @Override
            public void onChange(AngleChangeEvent e) {
                refreshExteriorImage();
            }
        });

    }

    public PreviewPanelSummaryContext getPreviewPanel() {
        return previewPanel;
    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public SeriesInfo getSeriesInfo() {
        return threedModel.getSeriesInfo();
    }

    public SeriesKey getSeriesKey() {
        return threedModel.getSeriesKey();
    }

//    public void setPicks(PicksChangeEvent e) {
//        this.picks = e.getNewPicks();
//        refreshExteriorImage();
//        refreshInteriorImage();
//    }

    public void setPicks(SimplePicks2 picks) {
        assert picks != null;
        this.picks = picks;
    }

    public void refreshImagePanels() {
        refreshExteriorImage();
        refreshInteriorImage();
    }

    private void refreshImagePanel(ThreedImagePanel threedImagePanel, int panelIndex) {
        Slice viewState = previewPanel.getViewSnapForPanel(panelIndex);
        ImageStack imageStack = getImageStack(viewState);
        boolean includeZPngs = !Browser.isIe6();
        ImmutableList<Path> urls = imageStack.getUrlListSmart(JpgWidth.W_STD, includeZPngs);
        threedImagePanel.setImageUrls(urls);
    }

    private void refreshExteriorImage() {
        refreshImagePanel(previewPanel.getExteriorThreedImagePanel(), 0);
    }

    private void refreshInteriorImage() {
        refreshImagePanel(previewPanel.getInteriorThreedImagePanel(), 1);
    }

    public ImageStack getImageStack(Slice viewState) {
        return threedModel.getImageStack(viewState, picks);
    }

    public void close() {
        //release event handlers
    }


}