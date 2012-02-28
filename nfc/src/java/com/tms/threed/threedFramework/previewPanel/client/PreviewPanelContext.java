package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.interior.InteriorButtonHandler;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.interior.InteriorButtonPanel;
import com.tms.threed.threedFramework.previewPanel.client.chatPanel.ChatPanel;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.threedFramework.previewPanel.client.headerPanel.HeaderPanel;
import com.tms.threed.threedFramework.previewPanel.client.thumbsPanel.ThumbClickEvent;
import com.tms.threed.threedFramework.previewPanel.client.thumbsPanel.ThumbClickHandler;
import com.tms.threed.threedFramework.previewPanel.client.thumbsPanel.ThumbPanel;
import com.tms.threed.threedFramework.previewPanel.client.thumbsPanel.ThumbsPanel;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleAndViewChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleAndViewChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewStates;
import com.tms.threed.threedFramework.threedModel.shared.*;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;
import com.tms.threed.threedFramework.threedModel.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class PreviewPanelContext {

    private final HandlerManager bus = new HandlerManager(this);

    private final BlinkOverlay blinkOverlay;
    private final ThreedImagePanel mainThreedImagePanel;
    private final TopImagePanel topImagePanel;
    private final FooterPanel footerPanel;
    private final ChatPanel chatPanel;
    private final HeaderPanel headerPanel;

    private final MsrpPanel msrpPanel;
    private final ThumbsPanel thumbsPanel;
    private final BottomPanel bottomPanel;


    private final PreviewPanel previewPanel;
    private final ExteriorButtonPanel exteriorButtonPanel;
    private final InteriorButtonPanel interiorButtonPanel;
    private final DragToSpin<ClearGif> dragToSpin;

    private final DefaultAngleButtonHandler angleButtonHandler;
    private final ThumbClickHandler thumbClickHandler;

    private final ThreedModel threedModel;
    private final FeatureModel featureModel;
    private final SeriesKey seriesKey;
    private final SeriesInfo seriesInfo;
    private final ViewStates viewStates;

    public PreviewPanelContext(final ThreedModel threedModel) {
        assert threedModel != null;

        this.threedModel = threedModel;
        this.featureModel = threedModel.getFeatureModel();
        this.seriesKey = threedModel.getSeriesKey();
        this.seriesInfo = threedModel.getSeriesInfo();
        this.viewStates = new ViewStates(seriesInfo);

        assert seriesKey != null;
        assert seriesInfo != null;

        assert assertNonNullSeries();



        chatPanel = new ChatPanel(featureModel.getDisplayName());
        headerPanel = new HeaderPanel(chatPanel,featureModel.getDisplayYear(),featureModel.getDisplayName());

        angleButtonHandler = new DefaultAngleButtonHandler();
        thumbClickHandler = new DefaultThumbClickHandler();

        exteriorButtonPanel = new ExteriorButtonPanel();
        exteriorButtonPanel.setButtonHandler(angleButtonHandler);

        interiorButtonPanel = new InteriorButtonPanel();
        interiorButtonPanel.setButtonHandler(angleButtonHandler);

        footerPanel = new FooterPanel(interiorButtonPanel, exteriorButtonPanel);

        mainThreedImagePanel = new ThreedImagePanel(0, ImageSize.STD_PNG);

        dragToSpin = new DragToSpin<ClearGif>();
        dragToSpin.setExteriorButtonHandler(angleButtonHandler);

        blinkOverlay = new BlinkOverlay();

        topImagePanel = new TopImagePanel(ImageSize.STD_PNG, mainThreedImagePanel, blinkOverlay, dragToSpin, headerPanel, footerPanel);

        msrpPanel = new MsrpPanel();
        thumbsPanel = new ThumbsPanel();
        thumbsPanel.setThumbCount(seriesInfo.getViewCount() - 1);
        thumbsPanel.addThumbClickHandler(thumbClickHandler);

        bottomPanel = new BottomPanel(msrpPanel, thumbsPanel);


        previewPanel = new PreviewPanel(ImageSize.STD_PNG, topImagePanel, bottomPanel);

        hideButtonPanels();


        mainThreedImagePanel.addLoadingCompleteHandler(new ThreedImagePanel.LoadingCompleteHandler() {
            @Override public void onLoadingComplete(ThreedImagePanel.LoadingCompleteEvent e) {
                if (e.isFatal()) {
                    hideButtonPanels();
                } else {
                    refreshButtonPanels();
                }
            }
        });

        refreshAfterViewUpdate();

    }


    public void setCurrentViewAndAngle(@Nonnull Slice slice) {
        setCurrentViewAndAngle(slice.getViewName(), slice.getAngle());
    }

    public void setCurrentViewAndAngle(@Nonnull String view, int angle) {
        Slice oldViewSnap = viewStates.getCurrentSlice();

        Slice newViewSnap = new Slice(view, angle);

        if (newViewSnap.equals(oldViewSnap)) return;
        boolean angleChanged = newViewSnap.getAngle() != oldViewSnap.getAngle();
        boolean viewChanged = !newViewSnap.getView().equals(oldViewSnap.getView());


        if (angleChanged && viewChanged) {
            ViewKey viewKey = threedModel.getViewKey(newViewSnap.getViewName());
            viewStates.setCurrentViewAndAngle(viewKey, newViewSnap.getAngle());
            fireAngleAndViewChangeEvent(newViewSnap);
        } else if (angleChanged) {
            viewStates.setCurrentAngle(newViewSnap.getAngle());
            fireAngleChangeEvent(newViewSnap.getAngle());
        } else if (viewChanged) {
            ViewKey viewKey = threedModel.getViewKey(newViewSnap.getView());
            viewStates.setCurrentView(viewKey);

            fireViewChangeEvent(viewKey);
        }

    }

    public void setCurrentViewAndAngle(int orientation) {
        Slice viewSnap = seriesInfo.getViewSnapFromOrientation(orientation);
        setCurrentViewAndAngle(viewSnap);
    }

    public ViewStates getViewStatesCopy() {
        if (viewStates == null) return null;
        return new ViewStates(viewStates);
    }

    @Nonnull
    public ThreedImagePanel getMainThreedImagePanel() {
        assert mainThreedImagePanel != null;
        return mainThreedImagePanel;
    }

    @Nonnull
    public PreviewPanel getPreviewPanel() {
        assert previewPanel != null;
        return previewPanel;
    }

    @Nonnull
    public HeaderPanel getHeaderPanel() {
        assert headerPanel != null;
        return headerPanel;
    }

    @Nonnull
    public ChatPanel getChatPanel() {
        assert chatPanel != null;
        return chatPanel;
    }

    @Nonnull
    public ExteriorButtonPanel getExteriorButtonPanel() {
        assert exteriorButtonPanel != null;
        return exteriorButtonPanel;
    }

    @Nonnull
    public InteriorButtonPanel getInteriorButtonPanel() {
        assert interiorButtonPanel != null;
        return interiorButtonPanel;
    }


    @Nonnull
    public DragToSpin<ClearGif> getDragToSpin() {
        assert dragToSpin != null;
        return dragToSpin;
    }


    @Nonnull
    public BlinkOverlay getBlinkOverlay() {
        assert blinkOverlay != null;
        return blinkOverlay;
    }


    @Nonnull
    public ThumbsPanel getThumbsPanel() {
        return thumbsPanel;
    }

    public List<ThumbPanel> getThumbPanels() {
        return thumbsPanel.getThumbPanels();
    }


    private boolean assertNonNullSeries() {
        assert seriesKey != null;
        assert seriesInfo != null;
        assert viewStates != null;
        return true;
    }

    private void refreshAfterViewUpdate() {
        assertNonNullSeries();
        hideButtonPanels();

        for (ThumbPanel thumbPanel : getThumbPanels()) {
            int panelIndex = thumbPanel.getPanelIndex();
            ViewKey viewForPanel = viewStates.getCurrentViewForPanel(panelIndex);
            thumbPanel.setViewKey(viewForPanel);
        }
    }

    public void hideButtonPanels() {
        dragToSpin.setEnabled(false);
        exteriorButtonPanel.setVisible(false);
        interiorButtonPanel.setVisible(false);
    }

    public void refreshButtonPanels() {
        dragToSpin.setEnabled(viewStates.isExterior());
        exteriorButtonPanel.setVisible(viewStates.isExterior());
        interiorButtonPanel.setVisible(viewStates.isInterior());
    }

    public Slice getSlice() {
        if (seriesInfo == null) return null;
        return viewStates.getCurrentSlice();
    }

    public Slice getSliceForPanel(int panelIndex) {
        if (seriesInfo == null) return null;
        return viewStates.getViewSnapForPanel(panelIndex);
    }

    public void setMsrp(String msrp) {
        msrpPanel.setMsrp(msrp);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        chatPanel.setChatInfo(chatInfo);
    }

    public void doFeatureBlink(Path png) {
        assert png != null;
        getBlinkOverlay().doFeatureBlink(png);
    }

    private class DefaultAngleButtonHandler implements ExteriorButtonHandler, InteriorButtonHandler {
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

        @Override public void onSelection(int clickedAngle) {
            if (viewStates == null) return;
            if (clickedAngle == viewStates.getCurrentAngle()) return;
            viewStates.setCurrentAngle(clickedAngle);
            fireAngleChangeEvent(clickedAngle);
        }
    }

    private class DefaultThumbClickHandler implements ThumbClickHandler {
        @Override public void onThumbClick(ThumbClickEvent event) {
            if (viewStates == null) return;
            int thumbIndex = event.getThumbIndex();
            viewStates.thumbClicked(thumbIndex);
            refreshAfterViewUpdate();
            fireViewChangeEvent(viewStates.getCurrentView());
        }
    }

    private void fireViewChangeEvent(ViewKey newViewKey) {
        bus.fireEvent(new ViewChangeEvent(newViewKey));
    }


    private void fireAngleChangeEvent(int newAngle) {
        bus.fireEvent(new AngleChangeEvent(newAngle));
    }

    private void fireAngleAndViewChangeEvent(Slice newViewSnap) {
        bus.fireEvent(new AngleAndViewChangeEvent(newViewSnap));
    }

    public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return bus.addHandler(ViewChangeEvent.TYPE, handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return bus.addHandler(AngleChangeEvent.TYPE, handler);
    }

    public HandlerRegistration addAngleAndViewChangeHandler(AngleAndViewChangeHandler handler) {
        return bus.addHandler(AngleAndViewChangeEvent.TYPE, handler);
    }


}