package threed.admin.client.toMove;

import com.google.common.collect.ImmutableList;
import threed.skin.common.client.ThreedImagePanel;
import threed.skin.summaryPanel.client.PreviewPanelSummaryContext;
import threed.skin.previewPanel.shared.viewModel.AngleChangeEvent;
import threed.skin.previewPanel.shared.viewModel.AngleChangeHandler;
import threed.core.imageModel.shared.ImImageStack;
import threed.core.threedModel.client.SimplePicks2;
import threed.core.threedModel.shared.*;
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
        ImImageStack imageStack = getImageStack(viewState);
        boolean includeZPngs = !Browser.isIe6();
        ImmutableList<Path> urls = imageStack.getUrlsJpgMode(JpgWidth.W_STD, includeZPngs);
        threedImagePanel.setImageUrls(urls);
    }

    private void refreshExteriorImage() {
        refreshImagePanel(previewPanel.getExteriorThreedImagePanel(), 0);
    }

    private void refreshInteriorImage() {
        refreshImagePanel(previewPanel.getInteriorThreedImagePanel(), 1);
    }

    public ImImageStack getImageStack(Slice viewState) {
        return threedModel.getImageStack(viewState, picks);
    }

    public void close() {
        //release event handlers
    }


}