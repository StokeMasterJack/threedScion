package c3i.admin.client;

import c3i.core.imageModel.shared.Profile;
import c3i.repo.shared.CommitHistory;
import c3i.smartClient.client.service.ThreedModelClient;
import c3i.smartClient.client.skins.Skin;
import c3i.util.shared.events.ChangeListener;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import smartsoft.util.gwt.client.Console;

//import c3i.skin.previewPanel.client.chatPanel.ChatInfo;

public class ThreedAdminPanel extends DockLayoutPanel {

    private static int counter;

    private final int previewPanelFrameId = counter++;


    private final ThreedAdminModel model;


    //main ui widgets
    private final ContentPanel contentPanel;
    private final LayersPanels layersPanels;

    private final HeaderPanel headerPanel;
    private final MiddlePanel middlePanel;
    private final StatusPanel statusPanel;

    public ThreedAdminPanel(ThreedAdminModel model) {
        super(Style.Unit.PX);
        this.model = model;

        contentPanel = new ContentPanel();
        layersPanels = new LayersPanels(model);
        middlePanel = new MiddlePanel(contentPanel, layersPanels);

        headerPanel = new HeaderPanel();
        statusPanel = new StatusPanel(model.getSeries());


        addNorth(headerPanel, 35);

        SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        splitLayoutPanel.addSouth(statusPanel,150);
        splitLayoutPanel.add(middlePanel);

        add(splitLayoutPanel);

//        getElement().getStyle().setBackgroundColor("yellow");


    }


    private class HeaderPanel extends FlexTable {

        private final ModeSelector modeSelector;
        private final ProfileSelector profileSelector;
        private final SkinSelector skinSelector;

        private final GenerateJpgsButton genJpgButton = new GenerateJpgsButton();
        private final TagCommitButton tagCommitButton = new TagCommitButton();
        private final VtcButton vtcButton = new VtcButton();

        private HeaderPanel() {

            skinSelector = new SkinSelector(model);
            modeSelector = new ModeSelector(model.imageMode());
            profileSelector = new ProfileSelector(model);

            FlexTable selectors = new FlexTable();
            selectors.setWidget(0, 0, modeSelector);
            selectors.setWidget(0, 1, profileSelector);
            selectors.setWidget(0, 2, skinSelector);
            selectors.setHeight("35px");

            FlexTable jpg = new FlexTable();
            jpg.setWidget(0, 0, genJpgButton);
            jpg.setHeight("35px");

            FlexTable version = new FlexTable();
            version.setWidget(0, 0, tagCommitButton);
            version.setWidget(0, 1, vtcButton);
            version.setHeight("35px");


            getColumnFormatter().setWidth(0, "50%");
            getColumnFormatter().setWidth(1, "15%");
            getColumnFormatter().setWidth(2, "*");

            getElement().getStyle().setPadding(0, Style.Unit.PX);

            setWidget(0, 0, selectors);
            setWidget(0, 1, jpg);
            setWidget(0, 2, version);


            skinSelector.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);
            modeSelector.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);
            profileSelector.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);

            tagCommitButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);
            vtcButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

            getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
            getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
            getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);


//            getElement().getStyle().setProperty("borderTop", "solid 1px black");
//            getElement().getStyle().setProperty("borderLeft", "solid 1px black");
//            getElement().getStyle().setProperty("borderRight", "solid 1px black");

            getElement().getStyle().setBackgroundColor("#DDDDDD");
            setWidth("100%");
            setHeight("35px");

        }


    }

    private class GenerateJpgsButton extends Anchor {
        private GenerateJpgsButton() {
            super("Generate JPGs");
            getElement().getStyle().setPaddingLeft(.8, Style.Unit.EM);
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    model.generateJpgsButtonOnClick();
                }
            });
        }
    }

    private class TagCommitButton extends AbsolutePanel {

        private Anchor anchor;
        private Label label;

        TagCommitButton() {
            anchor = new Anchor("Filler");
            label = new Label();

            add(anchor);
            add(label);

            getElement().getStyle().setMarginLeft(.8, Style.Unit.EM);

            refresh();

            model.commitHistory().addChangeListener(new ChangeListener<CommitHistory>() {
                @Override
                public void onChange(CommitHistory newValue) {
                    Console.log("commitHistory.change: " + newValue.isTagged());
                    refresh();
                }
            });


            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    model.tagCommitButtonOnClick();
                }
            });


        }

        private void refresh() {
            CommitHistory commitHistory = model.commitHistory().get();

            if (commitHistory == null) {
                throw new IllegalStateException();
            }

            String tag = commitHistory.getTag();
            if (tag != null) {
                anchor.setVisible(false);
                label.setVisible(true);
                label.setText("Tag: " + tag);
            } else {
                anchor.setVisible(true);
                label.setVisible(false);
                anchor.setText("Tag this version");
            }
        }

    }


    class VtcButton extends AbsolutePanel {

        private Anchor anchor;
        private Label label;


        VtcButton() {
            anchor = new Anchor("Filler");
            label = new Label();

            add(anchor);
            add(label);

            refresh();

            model.commitHistory().addChangeListener(new ChangeListener<CommitHistory>() {
                @Override
                public void onChange(CommitHistory newValue) {
                    refresh();
                }
            });

            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    model.vtcButtonOnClick();
                }
            });

            getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        }

        private void refresh() {

            CommitHistory commitHistory = model.commitHistory().get();
            if (commitHistory == null) throw new IllegalStateException();

            boolean vtc = commitHistory.isVtc();

            if (!vtc) {
                anchor.setVisible(true);
                label.setVisible(false);
                anchor.setText("Make this version VTC");
            } else {
                anchor.setVisible(false);
                label.setVisible(true);
                label.setText("This version is VTC");
            }

        }

    }

//
//    private String initMsrp(SeriesKey seriesKey) {
//        if (seriesKey.isa(SeriesKey.CAMRY)) {
//            return "$33,333";
//        } else if (seriesKey.isa(SeriesKey.VENZA)) {
//            return "$22,222";
//        } else if (seriesKey.isa(SeriesKey.AVALON)) {
//            return "$44,444";
//        } else if (seriesKey.isa(SeriesKey.YARIS)) {
//            return "$11,1111";
//        } else {
//            return "$12,345";
//        }
//    }
//
//    private ChatInfo initChatInfo(SeriesKey seriesKey) {
//        if (seriesKey.isa(SeriesKey.VENZA)) {
//            return new ChatInfo();
//        } else {
//            return null;
//        }
//    }


    private class MiddlePanel extends DockLayoutPanel {

        private MiddlePanel(ContentPanel contentPanel, LayersPanels layersPanels) {
            super(Style.Unit.PX);

            addWest(contentPanel, model.profile().get().getImageSize().getWidth() + 12);
            add(layersPanels);

            addStyleName("MiddlePanel");

            model.getViewsSession().profile().addChangeListener(new ChangeListener<Profile>() {
                @Override
                public void onChange(Profile newValue) {
                    MiddlePanel.this.forceLayout();
                }
            });
        }
    }

    private class ContentPanel extends ScrollPanel {
        private ContentPanel() {
            model.skin().addChangeListener(new ChangeListener<Skin>() {
                @Override
                public void onChange(Skin newValue) {
                    refresh();
                }
            });
            addStyleName("ContentPanel");
            refresh();
        }

        private void refresh() {
            Skin currentSkin = model.skin().get();
            IsWidget previewPanel = currentSkin.createPreviewPanel(model.getViewsSession());
            setWidget(previewPanel);
        }

    }


}
