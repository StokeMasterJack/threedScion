package com.tms.threed.threedAdmin.client.toMove;

import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.Scheduler;
import com.tms.threed.previewPanel.client.ViewPanel;
import com.tms.threed.previewPanel.client.main.PreviewPanelMain;
import com.tms.threed.previewPanel.client.main.PreviewPanelMainContext;
import com.tms.threed.previewPanel.client.main.chatPanel.ChatInfo;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbPanel;
import com.tms.threed.previewPanel.shared.viewModel.*;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.ImPng;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.client.ImageUrlProvider;
import com.tms.threed.threedCore.threedModel.shared.*;
import smartsoft.util.gwt.client.Browser;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

//import com.tms.threed.imageModel.shared.slice.ImgStack;

public class PreviewPaneContext {


    @Nonnull
    private final PreviewPanelMainContext previewPanelContext;

    @Nonnull
    private final ThreedModel threedModel;
    private final FeatureModel featureModel;

    private JpgWidth jpgWidth;
    private FixResult fixResult;
    private Var maybeBlinkVar;
    private boolean pngMode = false;

    public PreviewPaneContext(@Nonnull final PreviewPanelMainContext previewPanelContext, @Nonnull ThreedModel threedModel) {
        assert previewPanelContext != null;
        assert threedModel != null;


        this.threedModel = threedModel;
        this.featureModel = threedModel.getFeatureModel();
        this.previewPanelContext = previewPanelContext;

        previewPanelContext.addAngleChangeHandler(new AngleChangeHandler() {
            @Override
            public void onChange(AngleChangeEvent e) {
//                System.out.println("AngleChangeEvent[" + previewPanel.getSlice() + "]");
//                picks.setSlice(previewPanel.getSlice());
                refreshImagePanels();
            }
        });

        previewPanelContext.addViewChangeHandler(new ViewChangeHandler() {
            @Override
            public void onChange(ViewChangeEvent e) {
//                picks.setSlice(previewPanel.getSlice());
                refreshImagePanels();
            }
        });

        previewPanelContext.addAngleAndViewChangeHandler(new AngleAndViewChangeHandler() {
            @Override
            public void onChange(AngleAndViewChangeEvent e) {
//                picks.setSlice(previewPanel.getSlice());
                refreshImagePanels();
            }
        });

    }


    public void setFixResult(FixResult fixResult) {
        assert fixResult != null;
        this.fixResult = fixResult;
        String displayName = threedModel.getDisplayName(fixResult);
        this.previewPanelContext.setDisplayName(displayName);

    }

    public void setMaybeBlinkVar(Var maybeBlinkVar) {
        this.maybeBlinkVar = maybeBlinkVar;
    }

    public void setJpgWidth(JpgWidth jpgWidth) {
        this.jpgWidth = jpgWidth;
        assert fixResult != null;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
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

    private ImageUrlProvider imageUrlProvider = new ImageUrlProvider() {
        @Override
        public ImageStack getImageUrl(Slice viewState) {
            return getImageStack(viewState);
        }
    };

    public boolean isPngMode() {
        return pngMode;
    }

    public void setPngMode(boolean pngMode) {
        this.pngMode = pngMode;
//        refreshImagePanels();
    }

    public void refreshLayerVisibility() {
        refreshMainImage();
    }

//    public PrefetchStrategy getPrefetchStrategy() {
//        if (prefetchStrategy == null) {
//            prefetchStrategy = new PrefetchStrategy2(jpgWidth, imageUrlProvider, previewPanelContext.getViewStatesCopy());
//        }
//        return prefetchStrategy;
//    }




    public void refreshImagePanels() {
        refreshMainImage();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                maybeBlink();
            }
        });


        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
//                prefetch();   todo df messed up
                refreshThumbImages();
            }
        });

    }


    private boolean isBlinkSeries() {
        return getSeriesKey().isa(SeriesKey.FOUR_RUNNER) || getSeriesKey().isa(SeriesKey.SIENNA);
    }

//    public void setImgStack(IImImgStack imgStack) {
//        refreshMainImage();
//    }

    private void maybeBlink() {
        if (maybeBlinkVar == null) {
            return;
        }
        if (!isBlinkSeries()) {
            return;
        }
        if (Browser.isIe6()) {
            return;
        }
        if (!maybeBlinkVar.isAccessory(featureModel)) {
            return;  //is this redundant??
        }

        Slice slice = previewPanelContext.getSlice();

        if (fixResult.isInvalidBuild()) {
            return;
        }

        Path blinkPngUrl = threedModel.getBlinkPngUrl(slice, fixResult.getAssignments(), maybeBlinkVar);

        //null blinkPngUrl means that accessory is not visible at the current angle
        if (blinkPngUrl == null || picksError()) {
            //skip
        } else {
            maybeBlinkVar = null;
            previewPanelContext.doFeatureBlink(blinkPngUrl);
        }
    }


    public void setChatInfo(ChatInfo chatInfo) {
        previewPanelContext.setChatInfo(chatInfo);
    }

    public void setMsrp(String msrp) {
        previewPanelContext.setMsrp(msrp);
    }

    private void refreshImagePanel(ViewPanel threedImagePanel, int panelIndex) {
        Slice slice = previewPanelContext.getSliceForPanel(panelIndex);
        if (picksError()) {
            refreshImagePanelBadPicks(threedImagePanel);
        } else {
            ImageStack imageStack = getImageStack(slice);
            if (pngMode) {
                refreshImagePanelPngMode(threedImagePanel, imageStack);
            } else {
                refreshImagePanelJpgMode(threedImagePanel, imageStack);
            }
        }
    }

    private boolean picksError() {
        return fixResult.isInvalidBuild();
    }

    private boolean picksValid() {
        return fixResult.isValidBuild();
    }

    private void refreshImagePanelBadPicks(ViewPanel threedImagePanel) {
        threedImagePanel.showMessage("Invalid Build", fixResult.getErrorMessage(), "#CCCCCC");
        previewPanelContext.hideButtonPanels();
    }

    private void refreshImagePanelPngMode(ViewPanel threedImagePanel, ImageStack imageStack) {
        ImmutableList<ImPng> pngs = imageStack.getPngs();

        ImmutableList.Builder<Path> builder = ImmutableList.builder();
        for (ImPng png : pngs) {
            if (!png.isVisible() || !png.getLayer().isVisible()) continue;
            Path url = png.getUrl();
            builder.add(url);
        }

        threedImagePanel.setImageUrls(builder.build());
    }

    private void refreshImagePanelJpgMode(ViewPanel threedImagePanel, ImageStack imageStack) {
        boolean includeZPngs = !Browser.isIe6();
        ImmutableList<Path> urls = imageStack.getUrlsJpgMode(jpgWidth, includeZPngs);
        System.out.println("urls = " + urls);
        threedImagePanel.setImageUrls(urls);
    }

    private void refreshMainImage() {
        ViewPanel mainImagePanel = previewPanelContext.getPreviewPanel();
        refreshImagePanel(mainImagePanel, 0);
    }

    private void refreshThumbImages() {
        List<ThumbPanel> thumbPanels = previewPanelContext.getThumbPanels();
        for (ThumbPanel p : thumbPanels) {
            int panelIndex = p.getPanelIndex();
            ViewPanel threedImagePanel = p.getThreedImagePanel();
            refreshImagePanel(threedImagePanel, panelIndex);
        }
    }


    public ImageStack getImageStack(Slice slice) {
        assert fixResult != null;
        ImageStack imageStack = threedModel.getImageStack(slice, fixResult.getAssignments());
        return imageStack;
    }


    private void prefetch() {
        if (picksError()) return;
        if (isPngMode()) return;

//        if (picksValid()) {
//            Prefetcher prefetcher1 = getPrefetcher();
//            prefetcher1.prefetch();
//        }
    }


    public void close() {
        //release event handlers
    }


    public void setViewAndAngle(int orientation) {
        previewPanelContext.setCurrentViewAndAngle(orientation);
    }

    public PreviewPanelMain getPreviewPanel() {
        return previewPanelContext.getPreviewPanelMain();
    }

    public Slice getCurrentSlice() {
        return previewPanelContext.getSlice();
    }

    public PreviewPanelMainContext getPreviewPanelContext() {
        return previewPanelContext;
    }


}
