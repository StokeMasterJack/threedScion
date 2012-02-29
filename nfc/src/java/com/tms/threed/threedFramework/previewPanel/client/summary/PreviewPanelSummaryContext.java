package com.tms.threed.threedFramework.previewPanel.client.summary;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.previewPanel.client.ThreedImagePanel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;

public class PreviewPanelSummaryContext {

    private final HandlerManager bus = new HandlerManager(this);

    private ThreedImagePanel topThreedImagePanel;
    private ThreedImagePanel bottomThreedImagePanel;
    private TopImagePanel topImagePanel;
    private BottomImagePanel bottomImagePanel;
    private FooterPanel footerPanel;

    private PreviewPanelSummary summaryPanel;
    private ExteriorButtonPanel exteriorButtonPanel;
    private DragToSpin<ClearGif> dragToSpin;

    private SeriesKey seriesKey;
    private SeriesInfo seriesInfo;
    private ViewStates viewStates;

    public PreviewPanelSummary getSummaryPanel() {
        if (summaryPanel == null) {
            summaryPanel = new PreviewPanelSummary(getTopImagePanel(), getBottomImagePanel());
        }
        return summaryPanel;
    }

    private FooterPanel getFooterPanel() {
        if (footerPanel == null) footerPanel = new FooterPanel(getExteriorButtonPanel());
        return footerPanel;
    }

    public ExteriorButtonPanel getExteriorButtonPanel() {
        if (exteriorButtonPanel == null) {
            exteriorButtonPanel = new ExteriorButtonPanel();
            exteriorButtonPanel.setVisible(true);
            exteriorButtonPanel.setButtonHandler(angleButtonHandler);
        }
        return exteriorButtonPanel;
    }

    public DragToSpin<ClearGif> getDragToSpin() {
        if (dragToSpin == null) {
            dragToSpin = new DragToSpin<ClearGif>();
            dragToSpin.setExteriorButtonHandler(angleButtonHandler);
        }
        return dragToSpin;
    }

    public BottomImagePanel getBottomImagePanel() {
        if (bottomImagePanel == null) {
            bottomImagePanel = new BottomImagePanel(getBottomThreedImagePanel());
        }
        return bottomImagePanel;
    }

    public TopImagePanel getTopImagePanel() {
        if (topImagePanel == null) {
            topImagePanel = new TopImagePanel(getTopThreedImagePanel(), getDragToSpin(), getFooterPanel());
        }
        return topImagePanel;
    }

    public void setSeriesInfo(SeriesInfo seriesInfo) {
        if (seriesInfo == null) {
            this.seriesKey = null;
            this.seriesInfo = null;
            this.viewStates = null;
        } else {
            this.seriesKey = seriesInfo.getSeriesKey();
            this.seriesInfo = seriesInfo;
            this.viewStates = new ViewStates(seriesInfo);
        }
    }

    public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return bus.addHandler(ViewChangeEvent.TYPE, handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return bus.addHandler(AngleChangeEvent.TYPE, handler);
    }

    public Slice getViewSnap() {
        if (seriesInfo == null) return null;
        return viewStates.getCurrentSlice();
    }

    public Slice getViewSnapForPanel(int panelIndex) {
        if (seriesInfo == null) return null;
        return viewStates.getViewSnapForPanel(panelIndex);
    }

    public ThreedImagePanel getTopThreedImagePanel() {
        if (topThreedImagePanel == null) {
            topThreedImagePanel = new ThreedImagePanel(0, new ImageSize(TopImagePanel.PREFERRED_WIDTH_PX, TopImagePanel.PREFERRED_HEIGHT_PX));
        }
        return topThreedImagePanel;
    }

    public ThreedImagePanel getBottomThreedImagePanel() {
        if (bottomThreedImagePanel == null) {
            bottomThreedImagePanel = new ThreedImagePanel(1, new ImageSize(BottomImagePanel.PREFERRED_WIDTH_PX, BottomImagePanel.PREFERRED_HEIGHT_PX));
        }
        return bottomThreedImagePanel;
    }


    public Slice getViewState() {
        return null;
    }

    public ThreedImagePanel getExteriorThreedImagePanel() {
        return getTopThreedImagePanel();
    }

    public ThreedImagePanel getInteriorThreedImagePanel() {
        return getBottomThreedImagePanel();
    }

    private final ExteriorButtonHandler angleButtonHandler = new ExteriorButtonHandler() {
        @Override public void onPrevious() {
            if (viewStates == null) return;
            viewStates.previousAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

        @Override public void onNext() {
            if (viewStates == null) return;
            viewStates.nextAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

    };

    private void fireAngleChangeEvent(int newAngle) {
        bus.fireEvent(new AngleChangeEvent(newAngle));
    }
}