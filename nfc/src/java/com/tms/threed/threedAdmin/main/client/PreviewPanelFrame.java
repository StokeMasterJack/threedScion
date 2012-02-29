package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminServiceAsync;
import com.tms.threed.threedAdmin.main.client.toMove.PreviewPaneContext;
import com.tms.threed.threedAdmin.main.client.toMove.SummarySeriesContext;
import com.tms.threed.threedAdmin.featurePicker.client.CurrentUiPicks;
import com.tms.threed.threedAdmin.jpgGen.client.CreateTagDialog;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.previewPanel.client.main.chatPanel.ChatInfo;
import com.tms.threed.threedFramework.previewPanel.client.main.PreviewPanelMainContext;
import com.tms.threed.threedFramework.previewPanel.client.summary.PreviewPanelSummaryContext;
import com.tms.threed.threedFramework.previewPanel.client.main.PreviewPanelMain;
import com.tms.threed.threedFramework.previewPanel.client.summary.PreviewPanelSummary;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.threedFramework.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.gwtUtil.client.events2.ValueChangeHandlers;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

import static com.tms.threed.threedFramework.util.date.shared.StringUtil.isEmpty;

public class PreviewPanelFrame extends FlowPanel {

    private static int counter;

    private final int previewPanelFrameId = counter++;


    private final UiContext ctx;
    private final ThreedAdminServiceAsync service;


    private final JpgWidthListBox jpgWidthListBox;


    private final HeaderPanel headerPanel;
    private final FooterPanel footerPanel;

    private final PreviewPaneContext previewPaneContext;


    private final PreviewPanelMainContext previewPanelContext;
    private final PreviewPanelMain previewPanel;

    private boolean pngMode;
    private CurrentUiPicks currentUiPicks;


    private final ThreedModel threedModel;
    private CommitHistory commit;
    private RtConfig rtConfig;

    private final SeriesKey seriesKey;
    private final FeatureModel featureModel;

    private final ValueChangeHandlers<Boolean> pngModeChangeHandlers = new ValueChangeHandlers<Boolean>(this);

    private final ValueChangeHandlers<CommitHistory> commitChangeHandlers = new ValueChangeHandlers<CommitHistory>(this);

    private Callback callback;


    public PreviewPanelFrame(UiContext ctx, final ThreedAdminServiceAsync service, ThreedModel threedModel, CommitHistory commit, RtConfig rtConfig) {
        assert service != null;
        this.ctx = ctx;
        this.service = service;
        this.threedModel = threedModel;
        this.commit = commit;
        this.rtConfig = rtConfig;
        this.seriesKey = threedModel.getSeriesKey();
        this.featureModel = threedModel.getFeatureModel();

        jpgWidthListBox = new JpgWidthListBox(rtConfig);

        jpgWidthListBox.addJpgWidthChangeHandler(new ValueChangeHandler<JpgWidth>() {
            @Override
            public void onValueChange(ValueChangeEvent<JpgWidth> ev) {
                setJpgWidth(ev.getValue());
            }
        });


        headerPanel = new HeaderPanel();
        footerPanel = new FooterPanel();

        this.currentUiPicks = new CurrentUiPicks(threedModel);
        currentUiPicks.fix();

        previewPaneContext = initPreviewPaneContext();


        previewPanel = previewPaneContext.getPreviewPanel();

        previewPanelContext = previewPaneContext.getPreviewPanelContext();

        previewPanelContext.addViewChangeHandler(new ViewChangeHandler() {
            @Override
            public void onChange(ViewChangeEvent e) {
                footerPanel.refresh();
            }
        });

        previewPanelContext.addAngleChangeHandler(new AngleChangeHandler() {
            @Override
            public void onChange(AngleChangeEvent e) {
                footerPanel.refresh();
            }
        });

        add(headerPanel);

        add(previewPanel);
        add(footerPanel);

//        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
//        getElement().getStyle().setBorderColor("black");
//        getElement().getStyle().setBorderWidth(1, Style.Unit.PX);

        previewPanel.getElement().getStyle().setProperty("border", "solid 1px black");

        initialRefresh();


    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public HandlerRegistration addPngModeChangeHandler(ValueChangeHandler<Boolean> handler) {
        return pngModeChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addJpgWidthChangeHandler(ValueChangeHandler<JpgWidth> handler) {
        return jpgWidthListBox.addJpgWidthChangeHandler(handler);
    }

    public HandlerRegistration addTagCommitChangeHandler(ValueChangeHandler<CommitHistory> handler) {
        return commitChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }


    public void setPicks(CurrentUiPicks currentUiPicks) {
        this.currentUiPicks = currentUiPicks;
        previewPaneContext.setPicks(currentUiPicks);

        Var potentialBlinkVar = currentUiPicks.getPotentialBlinkVar();
        previewPaneContext.setMaybeBlinkVar(potentialBlinkVar);

        previewPaneContext.refreshImagePanels();
    }

    public void refreshLayerVisibility() {
        previewPaneContext.refreshLayerVisibility();
//        summaryPane.refreshLayerVisibility();
    }

    public Slice getCurrentSlice() {
        return previewPaneContext.getCurrentSlice();
    }

    public boolean isPngMode() {
        return pngMode;
    }

    public JpgWidth getCurrentJpgWidth() {
        return jpgWidthListBox.getSelectedJpgWidth();
    }

    private void setJpgWidth(JpgWidth jpgWidth) {
        JpgWidth w = getCurrentJpgWidth();
        previewPaneContext.setJpgWidth(w);

        ImageSize jpgSize = jpgWidth.getJpgSize();
        previewPanel.setMainImageSize(jpgSize);
//
        previewPaneContext.refreshImagePanels();
        previewPaneContext.setJpgWidth(jpgWidth);

//        footerPanel.setWidth(jpgWidth.intValueNoFail()+"px");
    }


    private class ModeRadioGroup extends FlowPanel {

        private final String radioGroup = previewPanelFrameId + "Mode";
        private final RadioButton jpgModeRadio = new RadioButton(radioGroup, "&nbsp;JPG Mode", true);
        private final RadioButton pngModeRadio = new RadioButton(radioGroup, "&nbsp;PNG Mode", true);

        private ModeRadioGroup() {


            getElement().getStyle().setPadding(.3, Style.Unit.EM);

            jpgModeRadio.getElement().getStyle().setDisplay(Style.Display.INLINE);
            pngModeRadio.getElement().getStyle().setDisplay(Style.Display.INLINE);

            jpgModeRadio.getElement().getStyle().setMarginLeft(.5, Style.Unit.EM);
            pngModeRadio.getElement().getStyle().setMarginLeft(.5, Style.Unit.EM);

            this.add(jpgModeRadio);
            this.add(pngModeRadio);


//            this.setHeight("3em");

            jpgModeRadio.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    pngMode = false;
                    onPngModeChange();
                }
            });

            pngModeRadio.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    pngMode = true;
                    onPngModeChange();
                }
            });
        }

        public void refresh() {
            pngModeRadio.setValue(pngMode);
            jpgModeRadio.setValue(!pngMode);
        }
    }

    private void onPngModeChange() {
        previewPaneContext.setPngMode(pngMode);
        previewPaneContext.refreshImagePanels();
        headerPanel.refresh();
        footerPanel.refresh();

        pngModeChangeHandlers.fire(pngMode);
    }


    private void openSummaryPanel() {

        SummarySeriesContext summaryPane = new SummarySeriesContext(threedModel);

        PreviewPanelSummaryContext summaryPanelContext = summaryPane.getPreviewPanel();

        PreviewPanelSummary summaryPanel = summaryPanelContext.getSummaryPanel();


        summaryPane.setPicks(currentUiPicks);

        summaryPane.setPicks(currentUiPicks);

        summaryPane.refreshImagePanels();

        DialogBox popup = new MyDialogBox("Summary");
        popup.setAnimationEnabled(true);


        popup.setWidget(summaryPanel);

        popup.getElement().getStyle().setZIndex(2000);

        popup.center();
        popup.show();


    }


    private class HeaderPanel extends Grid {

        ModeRadioGroup modeRadioGroup;

        Anchor summaryButton = new Anchor("Summary View");

        private HeaderPanel() {
            super(1, 3);
//            setBorderWidth(1);


            setWidth("100%");


            getColumnFormatter().setWidth(0, "33%");
            getColumnFormatter().setWidth(1, "33%");
            getColumnFormatter().setWidth(2, "33%");

            getElement().getStyle().setPadding(.5, Style.Unit.EM);
            modeRadioGroup = new ModeRadioGroup();

            summaryButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    openSummaryPanel();

                }
            });


            assert jpgWidthListBox != null;
            setWidget(0, 0, modeRadioGroup);
            setWidget(0, 1, createJpgWidthWidget());
            setWidget(0, 2, summaryButton);

            summaryButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);
            getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
            getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
            getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);


            getElement().getStyle().setProperty("borderTop", "solid 1px black");
            getElement().getStyle().setProperty("borderLeft", "solid 1px black");
            getElement().getStyle().setProperty("borderRight", "solid 1px black");
        }

        public void refresh() {
            modeRadioGroup.refresh();
        }

        FlowPanel createJpgWidthWidget() {
            FlowPanel p = new FlowPanel();
            p.add(new InlineHTML("JPG Width:&nbsp;"));
            p.add(jpgWidthListBox);
            return p;
        }
    }

    private static class GenerateJpgsButton extends Anchor{
        private GenerateJpgsButton() {
            super("Generate JPGs");
            getElement().getStyle().setPaddingLeft(.8, Style.Unit.EM);


        }

    }

    private class FooterPanel extends Grid {

        InlineLabel sliceLabel = new InlineLabel();
        GenerateJpgsButton genJpgButton = new GenerateJpgsButton();
        TagCommitButton tagCommitButton = new TagCommitButton();
        VtcButton vtcButton = new VtcButton();


        private FooterPanel() {
            super(1, 3);
//            setBorderWidth(1);


            setWidth("100%");


            getColumnFormatter().setWidth(0, "33%");
            getColumnFormatter().setWidth(1, "33%");
            getColumnFormatter().setWidth(2, "33%");

//            getElement().getStyle().setPadding(.5, Style.Unit.EM);

            tagCommitButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    tagCommitButtonOnClick();
                }
            });

            genJpgButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if(callback!=null){
                        JpgWidth jw = getCurrentJpgWidth();
                        callback.generateJpgsButtonClicked(seriesKey,commit,jw);
                    }
                }
            });


            assert jpgWidthListBox != null;
            setWidget(0, 0, genJpgButton);
            setWidget(0, 1, tagCommitButton);
            setWidget(0, 2, vtcButton);





            sliceLabel.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

            getCellFormatter().getElement(0, 1).getStyle().setPaddingTop(.2, Style.Unit.EM);
            getCellFormatter().getElement(0, 1).getStyle().setPaddingBottom(.2, Style.Unit.EM);

            getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
            getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
            getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);


            getElement().getStyle().setProperty("borderBottom", "solid 1px black");
            getElement().getStyle().setProperty("borderLeft", "solid 1px black");
            getElement().getStyle().setProperty("borderRight", "solid 1px black");


        }

        private void tagCommitButtonOnClick() {
            final CreateTagDialog createTagDialog = new CreateTagDialog();
            createTagDialog.center();
            createTagDialog.show();

            createTagDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

                @Override
                public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    if (createTagDialog.isCanceled()) return;
                    final String newTagName = createTagDialog.getTagName();
                    if (isEmpty(newTagName)) return;

                    ctx.showMessage("Creating tag[" + newTagName + "] ...");

                    final CommitId commitId = commit.getCommitId();

                    service.tagCommit(seriesKey, newTagName, commitId, new AsyncCallback<CommitHistory>() {
                        @Override
                        public void onFailure(Throwable e) {
                            ctx.showMessage("Failure creating tag: " + e);
                            e.printStackTrace();
                        }

                        @Override
                        public void onSuccess(CommitHistory result) {
                            ctx.showMessage("Tag[" + result.getTag() + "] created");
                            commit = result;
                            commitChangeHandlers.fire(commit);
                            footerPanel.refresh();
                        }
                    });


                }
            });
        }


        public void refresh() {
            Slice slice = getCurrentSlice();
            String sliceText = Strings.capFirstLetter(slice.getViewName()) + " angle " + slice.getAnglePadded();
            sliceLabel.setText(sliceText);

            tagCommitButton.refresh();


        }

    }

    class TagCommitButton extends AbsolutePanel {

        private Anchor anchor;
        private Label label;

        TagCommitButton() {
            assert commit != null;
            anchor = new Anchor("Filler");
            label = new Label();

            add(anchor);
            add(label);


            getElement().getStyle().setMarginLeft(.8, Style.Unit.EM);

            refresh();
        }

        void refresh() {
            if (commit.isTagged()) {
                anchor.setVisible(false);
                label.setVisible(true);
                label.setText("Version: " + commit.getDisplayName());
            } else {
                anchor.setVisible(true);
                label.setVisible(false);
                anchor.setText("Tag this version");
            }
        }

        public void addClickHandler(ClickHandler clickHandler) {
            anchor.addClickHandler(clickHandler);
        }
    }


    class VtcButton extends AbsolutePanel {

        private Anchor anchor;
        private Label label;

        private Boolean vtc;

        VtcButton() {
            anchor = new Anchor("Filler");
            label = new Label();

            add(anchor);
            add(label);

            Console.log("Fetching vtcRootTreeId..");
            service.getVtcRootTreeId(seriesKey, new AsyncCallback<RootTreeId>() {
                @Override
                public void onFailure(Throwable e) {
                    String msg = "Problem fetching vtc: " + e + ". Try checking server log.";
                    Console.error(msg);
                    ctx.showMessage(msg);
                    vtc = null;
                    refresh();
                }

                @Override
                public void onSuccess(RootTreeId vtcRootTreeId) {
                    RootTreeId rootTreeId = commit.getRootTreeId();
                    Console.log("vtcCommitId received: [" + vtcRootTreeId + "]");
                    vtc = rootTreeId.equals(vtcRootTreeId);
                    refresh();
                }
            });


            refresh();

            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    vtcOnClick();
                }
            });

            getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        }

        void refresh() {
            if (vtc == null) {
                anchor.setVisible(false);
                label.setVisible(false);
            } else if (!vtc) {
                anchor.setVisible(true);
                label.setVisible(false);
                anchor.setText("Make this version VTC");
            } else {
                anchor.setVisible(false);
                label.setVisible(true);
                label.setText("This version is VTC");
            }

        }

        private void vtcOnClick() {

            boolean confirm = Window.confirm("Are you sure?");

            if (!confirm) return;

            service.setVtcRootTreeId(seriesKey, commit.getRootTreeId(), new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable e) {
                    String msg = "Problem setting vtc: " + e + ". Try checking server log.";
                    Console.error(msg);
                    ctx.showMessage(msg);
                }

                @Override
                public void onSuccess(Void result) {
                    ctx.showMessage("VTC successfully set");
                    vtc = true;
                    refresh();
                }
            });
        }
    }


    private PreviewPaneContext initPreviewPaneContext() {

        String msrp = initMsrp(seriesKey);
        ChatInfo chatInfo = initChatInfo(seriesKey);

        PreviewPanelMainContext previewPanelContext = new PreviewPanelMainContext(threedModel);

        final PreviewPaneContext previewPaneContext = new PreviewPaneContext(previewPanelContext, threedModel);

        previewPaneContext.setPicks(currentUiPicks);
        previewPaneContext.setJpgWidth(getCurrentJpgWidth());

        previewPaneContext.setMsrp(msrp);
        previewPaneContext.setChatInfo(chatInfo);


        return previewPaneContext;
    }

    private String initMsrp(SeriesKey seriesKey) {
        if (seriesKey.isa(SeriesKey.CAMRY)) {
            return "$33,333";
        } else if (seriesKey.isa(SeriesKey.VENZA)) {
            return "$22,222";
        } else if (seriesKey.isa(SeriesKey.AVALON)) {
            return "$44,444";
        } else if (seriesKey.isa(SeriesKey.YARIS)) {
            return "$11,1111";
        } else {
            return "$12,345";
        }
    }

    private ChatInfo initChatInfo(SeriesKey seriesKey) {
        if (seriesKey.isa(SeriesKey.VENZA)) {
            return new ChatInfo();
        } else {
            return null;
        }
    }


    private void initialRefresh() {
        headerPanel.refresh();
        footerPanel.refresh();
        previewPaneContext.refreshImagePanels();

    }

    public int getPreferredWidthPx() {
        return previewPanel.getPreferredWidthPx() + 2;
    }

    public HandlerRegistration addViewChangeHandler(ViewChangeHandler handler) {
        return previewPanelContext.addViewChangeHandler(handler);
    }

    public HandlerRegistration addAngleChangeHandler(AngleChangeHandler handler) {
        return previewPanelContext.addAngleChangeHandler(handler);
    }

    public static interface Callback{
        void generateJpgsButtonClicked(SeriesKey seriesKey,CommitHistory commitHistory, JpgWidth jpgWidth);
        void tagCommitButtonClicked();
    }


}
