package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.imageModel.shared.ILayer;
import com.tms.threed.threedFramework.imageModel.shared.IPng;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.Set;

public class LayerInfoPanel extends Composite {

    public LayerInfoPanel(ILayer layer, IPng png) {

        FlexTable t = new FlexTable();


        t.getElement().getStyle().setMarginLeft(1, Style.Unit.EM);


        t.setWidget(0, 0, new HTML("<b>PNG: </b>"));
        t.setWidget(1, 0, new HTML("<b>Picks: </b>"));

        t.setText(0, 1, png.getUrl(new Path()) + "");
        Set<Var> features = png.getFeatures();

        String featuresString;
        if (features.isEmpty()) {
            featuresString = "None";
        } else {
            featuresString = features.toString();
            featuresString = featuresString.replace("[","").replace("]","");
        }
        t.setText(1, 1, featuresString);


//        t.getFlexCellFormatter().getElement(0, 0).getStyle().setWidth(8, Style.Unit.EM);

        t.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        t.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

        t.getFlexCellFormatter().getElement(0, 0).getStyle().setPaddingRight(1, Style.Unit.EM);
        t.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingRight(1, Style.Unit.EM);


        t.getElement().getStyle().setMargin(1, Style.Unit.EM);

        initWidget(t);

    }


}

