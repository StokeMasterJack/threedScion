package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.CurrentUiPicks;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanelFactory;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanelModel;
import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.VarPanel;
import com.tms.threed.threedFramework.threedAdmin.main.client.tabLabel.TabLabel;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.featureModel.shared.picks.UiPicksChangeEvent;
import com.tms.threed.threedFramework.featureModel.shared.picks.UiPicksChangeHandler;
import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.imageModel.shared.ILayer;
import com.tms.threed.threedFramework.imageModel.shared.IPng;
import com.tms.threed.threedFramework.imageModel.shared.ImLayer;
import com.tms.threed.threedFramework.imageModel.shared.ImView;
import com.tms.threed.threedFramework.previewPane.client.externalState.picks.SeriesNotSetException;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.*;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;

import java.util.ArrayList;
import java.util.List;

public class SeriesViewPanel extends ResizeComposite implements TabAware {

    private static int counter;

    private final int seriesViewId = counter++;

    private final FeaturePickerPanel featurePickerPanel;
    private final StatusPanel statusPanel;

    private final DockLayoutPanel outer = new DockLayoutPanel(Style.Unit.EM);
    private final DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.PX);

    private final PreviewPanelFrame previewPanelFrame;


    private final FlowPanel footerPanel;

    private final LayersPanel layersPanel;

    private final StackLayoutPanel layersStack;

    private final ThreedModel threedModel;
    private final SeriesKey seriesKey;
    private CommitHistory commit;
    private RtConfig rtConfig;
    private final SeriesId seriesId;
    private final FeatureModel featureModel;

    private final CurrentUiPicks currentUiPicks;
    private TabLabel tabLabel;

    private final String threedModelUrl;

    private Callback callback;

    public SeriesViewPanel(UiContext ctx, final ThreedAdminService1Async service, ThreedModel threedModel, CommitHistory commit,String threedModelUrl ,RtConfig rtConfig) {
        assert service != null;
        assert threedModel != null;
        assert commit != null;


        this.threedModel = threedModel;
        this.commit = commit;
        this.seriesKey = threedModel.getSeriesKey();
        this.seriesId = new SeriesId(seriesKey, commit.getRootTreeId());
        this.featureModel = threedModel.getFeatureModel();
        this.threedModelUrl = threedModelUrl;

        this.currentUiPicks = new CurrentUiPicks(threedModel);
        currentUiPicks.fix();
        featurePickerPanel = initFeaturePickerPanel();


        previewPanelFrame = new PreviewPanelFrame(ctx, service, threedModel, commit,rtConfig);

        previewPanelFrame.addTagCommitChangeHandler(new ValueChangeHandler<CommitHistory>() {
            @Override public void onValueChange(ValueChangeEvent<CommitHistory> ev) {
                SeriesViewPanel.this.commit = ev.getValue();
                tabLabel.setLabel(getTabLabel());
            }
        });


        previewPanelFrame.addPngModeChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                layersStack.setVisible(isPngMode());
                layersPanel.refresh();
                statusPanel.refresh();
            }
        });

        previewPanelFrame.addJpgWidthChangeHandler(new ValueChangeHandler<JpgWidth>() {
            @Override public void onValueChange(ValueChangeEvent<JpgWidth> ev) {
                layersStack.setVisible(isPngMode());
                layersPanel.refresh();
                statusPanel.refresh();
            }
        });


        layersPanel = new LayersPanel(layersPanelModel);
        statusPanel = new StatusPanel(statusPanelModel);


        this.footerPanel = createFooterPanel();

        layersStack = new StackLayoutPanel(Style.Unit.EM);
        layersStack.add(layersPanel, "Layers", 2.2);
        layersStack.setVisible(isPngMode());


        SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        splitLayoutPanel.addWest(featurePickerPanel, 300);
        splitLayoutPanel.add(layersStack);
        splitLayoutPanel.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

//        splitLayoutPanel.getElement().getStyle().setProperty("borderBottom", "blue thin solid");


        dock.getElement().getStyle().setMarginTop(.3, Style.Unit.EM);
        dock.getElement().getStyle().setMarginBottom(.8, Style.Unit.EM);
        dock.getElement().getStyle().setMarginLeft(.8, Style.Unit.EM);
        dock.getElement().getStyle().setMarginRight(.8, Style.Unit.EM);


        dock.addEast(previewPanelFrame, previewPanelFrame.getPreferredWidthPx());
        dock.add(splitLayoutPanel);

        outer.addSouth(footerPanel, 10);
        outer.add(dock);


        initWidget(outer);


        initialRefresh();

        currentUiPicks.addPicksChangeHandler(new UiPicksChangeHandler() {
            @Override
            public void onPicksChange(UiPicksChangeEvent e) {
//              previewPane.picksChanged(e);
//              previewPane.setPicks(currentUiPicks);

                featurePickerPanel.refresh();
                statusPanel.refresh();
                layersPanel.refresh();



                previewPanelFrame.setPicks(currentUiPicks);


            }
        });

        previewPanelFrame.addAngleChangeHandler(new AngleChangeHandler() {
            @Override public void onChange(AngleChangeEvent e) {
                layersPanel.refresh();
                statusPanel.refresh();
            }
        });

        previewPanelFrame.addViewChangeHandler(new ViewChangeHandler() {
            @Override public void onChange(ViewChangeEvent e) {
                layersPanel.refresh();
                statusPanel.refresh();
            }
        });

    }

    public void setCallback(final Callback callback) {
        this.callback = callback;
        previewPanelFrame.setCallback(new PreviewPanelFrame.Callback() {


            @Override
            public void generateJpgsButtonClicked(SeriesKey seriesKey, CommitHistory commitHistory, JpgWidth jpgWidth) {
                callback.generateJpgsButtonClicked(seriesKey, commit,jpgWidth);
            }

            @Override
            public void tagCommitButtonClicked() {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private boolean isPngMode() {
        return previewPanelFrame.isPngMode();
    }


    private FlowPanel createFooterPanel() {
        FlowPanel p = new FlowPanel();

        Style style = p.getElement().getStyle();
//        style.setPaddingTop(.4, Style.Unit.EM);
//        style.setPaddingBottom(.1, Style.Unit.EM);
//        style.setPaddingLeft(1, Style.Unit.EM);
//        style.setPaddingRight(1, Style.Unit.EM);


        p.add(statusPanel);


        return p;
    }


    public PreviewPanelFrame getPreviewPanelFrame() {
        return previewPanelFrame;
    }

    private LayersPanelModel layersPanelModel = new LayersPanelModel() {

        @Override
        public void selectAll() {
            if (threedModel == null) throw new SeriesNotSetException();

            for (ILayer layer : getLayers()) {
                layer.setVisible(true);
            }


            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    layersPanel.refresh();
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            previewPanelFrame.refreshLayerVisibility();
                        }
                    });
                }
            });

        }


        @Override
        public void selectNone() {
            if (threedModel == null) throw new SeriesNotSetException();

            for (ILayer layer : getLayers()) {
                layer.setVisible(false);
            }

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    layersPanel.refresh();

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            previewPanelFrame.refreshLayerVisibility();
                        }
                    });
                }
            });
        }

        @Override
        public List<ILayer> getLayers() {
            if (threedModel == null) throw new SeriesNotSetException();

            ArrayList<ILayer> a = new ArrayList<ILayer>();

            Slice currentSlice = getCurrentSlice();

            ImView view = threedModel.getImageModel().getView(currentSlice.getViewName());

            List<ImLayer> layers = view.getLayers();
            for (ImLayer layer : layers) {
                a.add(layer);
            }

            return a;

//            ImageSlice imageSlice = threedModel.getImageSlice(currentSlice);
//
//            return imageSlice.getAllLayers();
        }


        @Override
        public IPng getPngForLayer(ILayer layer) {
            if (threedModel == null) throw new SeriesNotSetException();

            Slice currentSlice = getCurrentSlice();
            return layer.computePngForPicks(currentUiPicks, currentSlice.getAngle());

//            layer.computePngForPicks(currentUiPicks);
//            return layer.currentPng;
        }

        @Override
        public void toggleLayer(final ILayer layer) {
            if (threedModel == null) throw new SeriesNotSetException();
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    layer.toggleVisibility();
                    previewPanelFrame.refreshLayerVisibility();
                }
            });
        }

        @Override
        public boolean isPngMode() {
            return SeriesViewPanel.this.isPngMode();
        }

        @Override
        public boolean isInvalidBuild() {
            return currentUiPicks.isInvalidBuild();
        }

        @Override
        public boolean isValidBuild() {
            return !isInvalidBuild();
        }
    };

    private StatusPanelModel statusPanelModel = new StatusPanelModel() {

        @Override
        public String getUserPicks() {
            if (currentUiPicks == null) return "";
            else return currentUiPicks.getCurrentTrueUiVars() + "";
        }

        @Override
        public String getFixedPicks() {
            if (currentUiPicks == null) {
                return "";
            } else if (currentUiPicks.isInvalidBuild()) {
                return "Invalid build: " + currentUiPicks.getErrorMessage();
            } else {
                return currentUiPicks.getFixedPicks() + "";
            }
        }

        @Override public IImageStack getImageStack() {
            Slice currentSlice = getCurrentSlice();
            if (currentUiPicks.isValidBuild()) {
                JpgWidth currentJpgWidth = previewPanelFrame.getCurrentJpgWidth();
                return threedModel.getImageStack(currentSlice, currentUiPicks, currentJpgWidth);
            } else {
                return null;
            }

        }

        @Override public CurrentUiPicks getCurrentUiPicks() {
            return currentUiPicks;
        }

        @Override public FeatureModel getFeatureModel() {
            return featureModel;
        }

        @Override public boolean isPngMode() {
            return SeriesViewPanel.this.isPngMode();
        }

        @Override public String getThreedModelUrl() {
            return threedModelUrl;
        }
    };


    private FeaturePickerPanel initFeaturePickerPanel() {
        assert threedModel != null;

        final VarPanelFactory varPanelFactory = new VarPanelFactory();

        class MyVarPanelModel implements VarPanelModel {

            @Override
            public boolean showFieldHeadings() {
                return true;
            }

            @Override
            public boolean hideDerived() {
                return true;
            }

            @Override
            public VarPanel getVarPanel(Var var) {
                if (threedModel == null) throw new SeriesNotSetException();
                return varPanelFactory.getVarPanel(var);
            }


            @Override
            public CurrentUiPicks getPicks() {
                if (threedModel == null) throw new SeriesNotSetException();
                return currentUiPicks;
            }

            @Override public String getRadioGroupPrefix() {
                return seriesViewId + "";
            }
        }

        varPanelFactory.setVarPanelContext(new MyVarPanelModel());

        final VarPanel rootVarPanel = varPanelFactory.getVarPanel(featureModel.getRootVar());


        FeaturePickerPanel fpp = new FeaturePickerPanel();
        fpp.setRootVarPanel(rootVarPanel);


        return fpp;
    }

    private Slice getCurrentSlice() {
        return previewPanelFrame.getCurrentSlice();
    }

    private void initialRefresh() {
        featurePickerPanel.refresh();
        layersPanel.refresh();
        statusPanel.refresh();
    }


    @Override public void setTabLabel(TabLabel tabLabel) {
        this.tabLabel = tabLabel;

    }

    public String getTabLabel() {
        return seriesKey.toStringPretty() + " [" + commit.getDisplayName() + "]";
    }

    public static interface Callback{
        void generateJpgsButtonClicked(SeriesKey seriesKey,CommitHistory commitHistory,JpgWidth jpgWidth);
    }
}