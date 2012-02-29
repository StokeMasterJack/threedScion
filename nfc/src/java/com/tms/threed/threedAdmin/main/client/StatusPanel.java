package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedAdmin.featurePicker.client.CurrentUiPicks;
import com.tms.threed.threedCore.featureModel.shared.Assignments;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.Tri;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.IImageStack;
import com.tms.threed.util.gwtUtil.client.dialogs.MyDialogBox;
import com.tms.threed.util.lang.shared.Path;

import java.util.List;
import java.util.Set;

public class StatusPanel extends Composite {

    private final StatusPanelModel model;
    private final FeatureModel featureModel;
    private final CurrentUiPicks currentUiPicks;

    private final FlexTable t;

    private final ScrollPanel scrollPanel;

    public StatusPanel(StatusPanelModel model) {
        this.model = model;
        this.featureModel = model.getFeatureModel();
        this.currentUiPicks = model.getCurrentUiPicks();

        t = new FlexTable();


        t.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);
        t.getColumnFormatter().setWidth(0,"120");

        refresh();

        scrollPanel = new ScrollPanel(t);
        scrollPanel.setHeight("100%");


        initWidget(scrollPanel);

//        getElement().getStyle().setBorderColor("black");
//        getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
//        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
    }


    public void refresh() {
        t.clear();

        t.removeAllRows();

        t.setWidget(0, 0, new HTML("<b>User Picks: </b>"));


        Anchor anchor = new Anchor("<b>Fixed Picks: </b>", true);
        anchor.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                showPicksPopup();
            }
        });


        HTML plain = new HTML("<b>Fixed Picks: </b>");

        if (currentUiPicks.isValidBuild()) {
            t.setWidget(1, 0, anchor);
        } else {
            t.setWidget(1, 0, plain);
        }

        t.setWidget(2, 0, new HTML("<b>Image url(s): </b>"));

        t.getFlexCellFormatter().getElement(0, 0).getStyle().setWidth(8, Style.Unit.EM);
        t.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        t.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        t.getFlexCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);


//        t.getFlexCellFormatter().getElement(0, 0).getStyle().setPaddingLeft(.5, Style.Unit.EM);
//        t.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingLeft(.5, Style.Unit.EM);

//        t.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingRight(.5, Style.Unit.EM);
//        t.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingRight(.5, Style.Unit.EM);

        t.setText(0, 1, model.getUserPicks() + "");
        t.setText(1, 1, model.getFixedPicks() + "");


        IImageStack imageStack = model.getImageStack();

//        Path jpgUrl = imageStack.getUrlsJpgMode().get(0);


//        Label headerWidget = new Label(jpgUrl.toString());
//        DisclosurePanel disclosurePanel = new DisclosurePanel();
//        disclosurePanel.setHeader(headerWidget);
//        disclosurePanel.setContent();

        Widget imageStackWidget;
        if (imageStack == null) {
            imageStackWidget = new Label("Invalid build");
        } else {
            imageStackWidget = createImageStackWidget(imageStack, model.isPngMode());
        }
        t.setWidget(2, 1, imageStackWidget);


        String threedModelUrl = model.getThreedModelUrl();
        t.setWidget(3, 0, new HTML("<b>3D Model: </b>"));
        t.setHTML(3, 1, "<a href='" + threedModelUrl + "' target='_blank'>" + threedModelUrl + "</a>");
//        t.setText(2, 1, jpgUrl.toString());


//        t.setWidget(2, 0, anchor);
//        t.getFlexCellFormatter().setColSpan(2, 0, 2);
//        t.getFlexCellFormatter().getElement(2, 0).getStyle().setPaddingLeft(.5, Style.Unit.EM);


    }

    Widget createImageStackWidget(IImageStack imageStack, boolean pngMode) {
        FlexTable t = new FlexTable();
        List<Path> urls;
        if (pngMode) {
            urls = imageStack.getUrlsPngMode();
        } else {
            urls = imageStack.getUrlsJpgMode();
        }
        for (final Path path : urls) {
            int r = t.getRowCount();

            Anchor anchor = new Anchor(path.toString());
            anchor.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    Window.open(path.toString(), null, null);
                }
            });


            String imageUrl = path.toString();


            t.setHTML(r, 0, "<a href='" + imageUrl + "' target='_blank'>" + imageUrl + "</a>");
        }
        return t;
    }

    private void showPicksPopup() {
        PicksPopup picksPopup = new PicksPopup();
        picksPopup.center();
        picksPopup.show();
    }

    private class PicksPopup extends MyDialogBox {

        FlexTable t = new FlexTable();

        private PicksPopup() {

            super("Picks");
            t.setBorderWidth(1);
//            t.setStyleName("picksPopupTable");


            t.setText(0, 0, "Feature Code");
            t.setText(0, 1, "Feature Name");
            t.setText(0, 2, "Value");

            this.setAnimationEnabled(true);
            int varCount = featureModel.size();

            FixResult fixResult = currentUiPicks.getFixResult();
            if (fixResult.isValidBuild()) {
                Assignments assignments = fixResult.getAssignments();

                for (int varIndex = 0; varIndex < varCount; varIndex++) {
                    Var var = featureModel.get(varIndex);

                    Tri value = assignments.getValue(var);
                    int row = varIndex + 1;
                    t.setText(row, 0, var.getCode());
                    t.setText(row, 1, var.getName());
                    t.setText(row, 2, value.toString());

                }

            } else {

                for (int varIndex = 0; varIndex < varCount; varIndex++) {
                    Var var = featureModel.get(varIndex);

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

            ScrollPanel scrollPanel = new ScrollPanel(t);
            scrollPanel.setHeight("20em");
//            scrollPanel.setWidth("700px");
            setWidget(scrollPanel);


        }

    }


}

