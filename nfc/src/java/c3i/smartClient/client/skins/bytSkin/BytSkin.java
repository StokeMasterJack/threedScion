package c3i.smartClient.client.skins.bytSkin;

import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.skins.Skin;
import c3i.smartClient.client.skins.angleSelectors.exterior.BytExteriorAngleSelector;
import c3i.smartClient.client.skins.angleSelectors.interior.BytInteriorAngleSelector;
import c3i.smartClient.client.skins.viewSelectors.ThumbViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import smartsoft.util.gwt.client.Console;

public class BytSkin implements Skin {

    @Override
    public IsWidget createPreviewPanel(ViewModel viewModel) {
        ViewPanel viewPanel = new ViewPanel(viewModel, "BytViewPanel");

        final BytExteriorAngleSelector exteriorAngleSelector = new BytExteriorAngleSelector(viewModel);
        final BytInteriorAngleSelector interiorAngleSelector = new BytInteriorAngleSelector(viewModel);


        exteriorAngleSelector.addStyleName("over");
        interiorAngleSelector.addStyleName("over");

        exteriorAngleSelector.setVisible(true);
        interiorAngleSelector.setVisible(false);

        viewPanel.add(exteriorAngleSelector);
        viewPanel.add(interiorAngleSelector);

        viewModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey view) {
                Console.log("ViewChange: " + view.getViewIndex());
                exteriorAngleSelector.setVisible(view.getViewIndex() == 0);
                interiorAngleSelector.setVisible(view.getViewIndex() == 1);
            }
        });

        ThumbViewSelector viewSelector = new ThumbViewSelector(viewModel);


        FlowPanel p = new FlowPanel();
        p.add(viewPanel);
        p.add(viewSelector);

        return p;
    }

    @Override
    public String getSkinName() {
        return "BytSkin";
    }
}
