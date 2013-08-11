package c3i.smartClient.client.skins.angleSelectors.simple;

import c3i.smartClient.client.widgets.AngleSelector;
import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleSpinControl extends AngleSelector {


    public SimpleSpinControl(final DragToSpinModel pListener) {
        super(pListener);
        initWidget(createMainWidget());
    }

    private Widget createMainWidget() {

        FlexTable t = new FlexTable();

        Button bPrevious = new Button("<<");
        Button bNext = new Button(">>");


        FlowPanel labelBackground = new FlowPanel();
        labelBackground.addStyleName("labelBackground");

        Label labelText = new Label("Drag to spin ");
        labelText.addStyleName("labelText");

        FlowPanel label = new FlowPanel();
        label.add(labelBackground);
        label.add(labelText);
        label.addStyleName("label");

        t = new FlexTable();
        t.setCellPadding(0);
        t.setCellSpacing(0);

//        t.setBorderWidth(1);
        t.setWidget(0, 0, bPrevious);
        t.setWidget(0, 1, label);
        t.setWidget(0, 2, bNext);

        t.addStyleName("AngleSelector");
        t.addStyleName("SpinControl");
        t.addStyleName("Simple");
        t.addStyleName("Exterior");

        bPrevious.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                model.anglePrevious();
            }
        });

        bNext.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                model.angleNext();
            }
        });

        return t;
    }

}
