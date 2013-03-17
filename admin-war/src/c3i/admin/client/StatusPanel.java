package c3i.admin.client;

import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.featureModel.shared.IAssignments;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.FixedPicks;
import c3i.featureModel.shared.Tri;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SeriesId;
import c3i.imageModel.shared.ViewKey;
import c3i.threedModel.shared.ThreedModel;
import c3i.smartClient.client.model.ImageStack;
import c3i.smartClient.client.model.ImageStackChangeListener;
import c3i.smartClient.client.model.Img;
import c3i.smartClient.client.model.ViewSession;
import c3i.smartClient.client.model.ViewsSession;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.util.shared.events.ChangeListener;
import com.google.common.collect.ImmutableList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;
import smartsoft.util.shared.Path;

import java.util.Set;

import static smartsoft.util.shared.GwtSafe.getSimpleName;

public class StatusPanel extends ScrollPanel {


    private final Series series;
    private final SeriesId seriesId;
    private final ViewsSession viewsSession;

    private final ThreedModel threedModel;
    private final FeatureModel featureModel;
    private final CurrentUiPicks currentUiPicks;

    private ViewSession viewSession;
    private StatusPanelModel model;

    private final FlexTable t;


    public StatusPanel(Series series) {

        this.series = series;
        this.viewsSession = series.getViewsSession();

        this.seriesId = series.getSeriesId();

        this.threedModel = viewsSession.getThreedModel();
        this.featureModel = threedModel.getFeatureModel();
        this.currentUiPicks = series.getCurrentUiPicks();

        currentUiPicks.addChangeListener(new ChangeListener<FixedPicks>() {
            @Override
            public void onChange(FixedPicks newPicks) {
                refresh();
            }
        });

        for (ViewSession vs : viewsSession.getViewSessions()) {
            vs.addImageStackChangeListener(new ImageStackChangeListener() {
                @Override
                public void onChange(ImageStack newValue) {
                    refresh();
                }
            });
        }

        viewsSession.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey newValue) {
                refresh();
            }
        });

        t = new FlexTable();



        Style style = t.getElement().getStyle();
        style.setMarginLeft(1, Style.Unit.EM);
        style.setMarginBottom(1, Style.Unit.EM);
//        style.setBackgroundColor("#DDDDDD");
        t.getColumnFormatter().setWidth(0, "120");
        t.getColumnFormatter().setWidth(1, "*");


        FlowPanel fp = new FlowPanel();
        fp.add(t);

//        t.setHeight("100em");

        getElement().getStyle().setBorderColor("black");
        getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);

        setWidget(fp);
//        setHeight("100%");

        addStyleName(getSimpleName(StatusPanel.class));


        this.setAlwaysShowScrollBars(false);
        refresh();
    }


    public void refresh() {
        this.viewSession = viewsSession.getViewSession();
        this.model = new StatusPanelModel(viewSession, series);


        t.clear();

        t.removeAllRows();

        t.setWidget(0, 0, new HTML("<b>User Picks: </b>"));


        Anchor anchor = new Anchor("<b>Fixed Picks: </b>", true);
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showPicksPopup();
            }
        });


        HTML plain = new HTML("<b>Fixed Picks: </b>");


        FixedPicks fixedPicks = model.getFixedPicks();
        if (fixedPicks.isValidBuild()) {
            t.setWidget(1, 0, anchor);
        } else {
            t.setWidget(1, 0, plain);
        }

        t.setWidget(2, 0, new HTML("<b>Image url(s): </b>"));

        t.getFlexCellFormatter().getElement(0, 0).getStyle().setWidth(8, Style.Unit.EM);
        t.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        t.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        t.getFlexCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);


        Set<Var> currentTrueUiVars = currentUiPicks.getCurrentTrueUiVars();

        t.setText(0, 1, currentTrueUiVars + "");
        t.setText(1, 1, model.getFixedPicksAsString());


        ImageStack imageStack = viewSession.getImageStack();


        if (imageStack != null) {
            Widget imageStackWidget;
            if (imageStack == null) {
                imageStackWidget = new Label("Invalid build");
            } else {
                imageStackWidget = createImageStackWidget(imageStack);
            }
            t.setWidget(2, 1, imageStackWidget);


//            String threedModelUrl = threedModelClient.getThreedModelUrl(seriesId).toString();
            Path threedModelUrl = series.getThreedModelUrl();
            t.setWidget(3, 0, new HTML("<b>3D Model: </b>"));
            t.setHTML(3, 1, "<a href='" + threedModelUrl + "' target='_blank'>" + threedModelUrl + "</a>");
        }


        t.setWidth("100%");
//        t.getElement().getStyle().setProperty("marginLeft","auto");
//        t.getElement().getStyle().setProperty("marginRight","auto");
//

    }

    Widget createImageStackWidget(ImageStack imageStack) {
        FlexTable t = new FlexTable();
        ImmutableList<Img> images = imageStack.getImages();

        for (final Img img : images) {
            int r = t.getRowCount();

            final Path path = img.getUrl();
            Anchor anchor = new Anchor(path.toString());
            anchor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(path.toString(), null, null);
                }
            });


            String imageUrl = path.toString();


            t.setHTML(r, 0, "<a href='" + imageUrl + "' target='_blank'>" + imageUrl + "</a>");
        }
        return t;
    }

    private void showPicksPopup() {
        PicksPopup picksPopup = new PicksPopup(currentUiPicks.get());
        picksPopup.getElement().getStyle().setZIndex(100000);
        picksPopup.center();
        picksPopup.show();
    }

    private class PicksPopup extends MyDialogBox {

        FlexTable t = new FlexTable();

        private PicksPopup(FixedPicks fixedPicks) {

            super("Picks");
            t.setBorderWidth(1);

            t.setText(0, 0, "Feature Code");
            t.setText(0, 1, "Feature Name");
            t.setText(0, 2, "Value");

            this.setAnimationEnabled(true);
            int varCount = featureModel.size();

            if (fixedPicks.isValidBuild()) {
                IAssignments IAssignments = fixedPicks.getIAssignments();

                for (int varIndex = 0; varIndex < varCount; varIndex++) {
                    Var var = featureModel.getVar(varIndex);

                    Tri value = IAssignments.getValue(var);
                    int row = varIndex + 1;
                    t.setText(row, 0, var.getCode());
                    t.setText(row, 1, var.getName());
                    t.setText(row, 2, value.toString());

                }

            } else {

                for (int varIndex = 0; varIndex < varCount; varIndex++) {
                    Var var = featureModel.getVar(varIndex);

                    Set<Var> currentTrueVars = currentUiPicks.getCurrentTrueUiVars();
                    boolean value = currentTrueVars.contains(var);
                    String v = value ? "TRUE" : "";
                    int row = varIndex + 1;
                    t.setText(row, 0, var.getCode());
                    t.setText(row, 1, var.getName());
                    t.setText(row, 2, v);

                }

            }

            t.getColumnFormatter().setWidth(0, "200px");
            t.getColumnFormatter().setWidth(0, "200px");
            t.getColumnFormatter().setWidth(1, "250px");
            t.getColumnFormatter().setWidth(2, "150px");
            t.getColumnFormatter().setWidth(3, "150px");

//            ScrollPanel scrollPanel = new ScrollPanel(t);
//            scrollPanel.setHeight("20em");
//            scrollPanel.setWidth("700px");
            setWidget(t);


        }

    }


}

