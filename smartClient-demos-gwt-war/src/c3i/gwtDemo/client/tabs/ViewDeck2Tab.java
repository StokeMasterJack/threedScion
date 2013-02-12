package c3i.gwtDemo.client.tabs;

import c3i.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.skins.angleSelectors.exterior.BytExteriorAngleSelector;
import c3i.smartClient.client.skins.angleSelectors.interior.BytInteriorAngleSelector;
import c3i.smartClient.client.skins.viewSelectors.ThumbViewSelector;
import c3i.smartClient.client.skins.viewSelectors.ViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class ViewDeck2Tab extends FlowPanel {

    public ViewDeck2Tab(ThreedSession threedSession) {
        ViewPanel viewPanel = new ViewPanel(threedSession);
        ViewSelector viewSelector = new ThumbViewSelector(threedSession);
        add(viewPanel);
        add(viewSelector);


        final BytExteriorAngleSelector exteriorAngleSelector = new BytExteriorAngleSelector(threedSession);
        exteriorAngleSelector.addStyleName("over");

        final BytInteriorAngleSelector interiorAngleSelector = new BytInteriorAngleSelector(threedSession);
        interiorAngleSelector.addStyleName("over");

        exteriorAngleSelector.setVisible(true);
        interiorAngleSelector.setVisible(false);


        threedSession.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey view) {
                exteriorAngleSelector.setVisible(view.getViewIndex() == 0);
                interiorAngleSelector.setVisible(view.getViewIndex() == 1);
            }
        });
        viewPanel.add(exteriorAngleSelector);
        viewPanel.add(interiorAngleSelector);
    }


}