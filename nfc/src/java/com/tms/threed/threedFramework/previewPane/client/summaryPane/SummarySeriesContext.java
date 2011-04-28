package com.tms.threed.threedFramework.previewPane.client.summaryPane;

import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.previewPane.client.SimplePicks2;
import com.tms.threed.threedFramework.previewPanel.client.ThreedImagePanel;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedCore.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Browser;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class SummarySeriesContext {

    @Nonnull
    private final SummaryPanelContext previewPanel;
    @Nonnull
    private final ThreedModel threedModel;

    private SimplePicks2 picks;

    public SummarySeriesContext(@Nonnull ThreedModel threedModel) {
        assert threedModel != null;

        this.threedModel = threedModel;
        this.previewPanel = new SummaryPanelContext();

        this.previewPanel.setSeriesInfo(threedModel.getSeriesInfo());

        previewPanel.addAngleChangeHandler(new AngleChangeHandler() {
            @Override public void onChange(AngleChangeEvent e) {
                refreshExteriorImage();
            }
        });

    }

    public SummaryPanelContext getPreviewPanel() {
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
        IImageStack imageStack = getImageStack(viewState);

        boolean includeZPngs = !Browser.isIe6();

        List<Path> urls = imageStack.getUrlsJpgMode(includeZPngs);

        threedImagePanel.setImageUrls(urls);
    }

    private void refreshExteriorImage() {
        refreshImagePanel(previewPanel.getExteriorThreedImagePanel(), 0);
    }

    private void refreshInteriorImage() {
        refreshImagePanel(previewPanel.getInteriorThreedImagePanel(), 1);
    }

    public IImageStack getImageStack(Slice viewState) {
        return threedModel.getImageStack(viewState, picks, JpgWidth.W_STD);
    }

    public void close() {
        //release event handlers
    }


}