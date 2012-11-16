package c3i.smartClient.client.skins.bytSkin;

import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.skins.Skin;
import c3i.smartClient.client.skins.angleSelectors.exterior.BytExteriorAngleSelector;
import c3i.smartClient.client.skins.angleSelectors.interior.BytInteriorAngleSelector;
import c3i.smartClient.client.skins.angleSelectors.interior.BytInteriorAngleSelector2;
import c3i.smartClient.client.skins.viewSelectors.ThumbViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import java.util.logging.Level;import java.util.logging.Logger;

public class BytMain implements Skin {

    @Override
    public IsWidget createPreviewPanel(ViewModel viewModel) {
        ViewPanel viewPanel = new ViewPanel(viewModel);

        final BytExteriorAngleSelector exteriorAngleSelector = new BytExteriorAngleSelector(viewModel.getViewModel(0));
        final BytInteriorAngleSelector2 interiorAngleSelector = new BytInteriorAngleSelector2(viewModel.getViewModel(1));

        exteriorAngleSelector.addStyleName("over");
        interiorAngleSelector.addStyleName("over");

        exteriorAngleSelector.setVisible(true);
        interiorAngleSelector.setVisible(false);

        viewPanel.add(exteriorAngleSelector);
        viewPanel.add(interiorAngleSelector);

        viewModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey view) {
                exteriorAngleSelector.setVisible(view.getViewIndex() == 0);
                interiorAngleSelector.setVisible(view.getViewIndex() == 1);
            }
        });

        ThumbViewSelector viewSelector = new ThumbViewSelector(viewModel);


        FlowPanel p = new FlowPanel();
        p.add(viewPanel);

        p.add(viewSelector);
        p.getElement().getStyle().setWidth(viewPanel.getPreferredSize().getWidth(), Style.Unit.PX);

        return p;
    }

    @Override
    public String getSkinName() {
        return "Byt Main Page";
    }
}
