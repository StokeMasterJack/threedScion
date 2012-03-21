package com.tms.threed.previewPanel.client.main;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.client.ViewPanel;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;
import com.tms.threed.previewPanel.client.buttonBars.interior.InteriorButtonHandler;
import com.tms.threed.previewPanel.client.buttonBars.interior.InteriorButtonPanel;
import com.tms.threed.previewPanel.client.main.chatPanel.ChatInfo;
import com.tms.threed.previewPanel.client.main.chatPanel.ChatPanel;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbClickEvent;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbClickHandler;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbPanel;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbsPanel;
import com.tms.threed.previewPanel.shared.viewModel.*;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class PreviewPanelMainContext {

    private final HandlerManager bus = new HandlerManager(this);

    private final ViewPanel previewPanel;
    private final TopImagePanel topImagePanel;
    private final FooterPanel footerPanel;
    private final ChatPanel chatPanel;
    private final HeaderPanel headerPanel;

    private final MsrpPanel msrpPanel;
    private final ThumbsPanel thumbsPanel;
    private final BottomPanel bottomPanel;


    private final PreviewPanelMain previewPanelMain;
    private final ExteriorButtonPanel exteriorButtonPanel;
    private final InteriorButtonPanel interiorButtonPanel;

    private final DefaultAngleButtonHandler angleButtonHandler;
    private final ThumbClickHandler thumbClickHandler;

    private final ThreedModel threedModel;
    private final FeatureModel featureModel;
    private final SeriesKey seriesKey;
    private final SeriesInfo seriesInfo;
    private final ViewStates viewStates;

    public PreviewPanelMainContext(final ThreedModel threedModel) {
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
        headerPanel = new HeaderPanel(chatPanel, featureModel.getDisplayYear(), featureModel.getDisplayName());

        angleButtonHandler = new DefaultAngleButtonHandler();
        thumbClickHandler = new DefaultThumbClickHandler();

        exteriorButtonPanel = new ExteriorButtonPanel();
        exteriorButtonPanel.setButtonHandler(angleButtonHandler);

        interiorButtonPanel = new InteriorButtonPanel();
        interiorButtonPanel.setButtonHandler(angleButtonHandler);

        footerPanel = new FooterPanel(interiorButtonPanel, exteriorButtonPanel);

        previewPanel = new ViewPanel(0, ImageSize.STD_PNG, true);


        topImagePanel = new TopImagePanel(ImageSize.STD_PNG, previewPanel, headerPanel, footerPanel);

        msrpPanel = new MsrpPanel();
        thumbsPanel = new ThumbsPanel();
        thumbsPanel.setThumbCount(seriesInfo.getViewCount() - 1);
        thumbsPanel.addThumbClickHandler(thumbClickHandler);

        bottomPanel = new BottomPanel(msrpPanel, thumbsPanel);


        previewPanelMain = new PreviewPanelMain(ImageSize.STD_PNG, topImagePanel, bottomPanel);

        hideButtonPanels();

        previewPanel.setExteriorButtonHandler(angleButtonHandler);


        previewPanel.setThreedImagePanelListener(new ThreedImagePanel.ThreedImagePanelListener() {
            @Override
            public void allImagesComplete(List<Path> errors, boolean fatal) {
                if (fatal) {
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
    public ViewPanel getPreviewPanel() {
        assert previewPanel != null;
        return previewPanel;
    }

    @Nonnull
    public PreviewPanelMain getPreviewPanelMain() {
        assert previewPanelMain != null;
        return previewPanelMain;
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
        previewPanel.setDragEnabled(false);
        exteriorButtonPanel.setVisible(false);
        interiorButtonPanel.setVisible(false);
    }

    public void refreshButtonPanels() {
        previewPanel.setDragEnabled(viewStates.isExterior());
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
        previewPanel.doFeatureBlink(png);
    }

    public void setDisplayName(String displayName) {
        headerPanel.setDisplayName(displayName);
    }

    private class DefaultAngleButtonHandler implements ExteriorButtonHandler, InteriorButtonHandler {
        @Override
        public void onPrevious() {
            if (viewStates == null) return;
            viewStates.previousAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

        @Override
        public void onNext() {
            if (viewStates == null) return;
            viewStates.nextAngle();
            fireAngleChangeEvent(viewStates.getCurrentAngle());
        }

        @Override
        public void onSelection(int clickedAngle) {
            if (viewStates == null) return;
            if (clickedAngle == viewStates.getCurrentAngle()) return;
            viewStates.setCurrentAngle(clickedAngle);
            fireAngleChangeEvent(clickedAngle);
        }
    }

    private class DefaultThumbClickHandler implements ThumbClickHandler {
        @Override
        public void onThumbClick(ThumbClickEvent event) {
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