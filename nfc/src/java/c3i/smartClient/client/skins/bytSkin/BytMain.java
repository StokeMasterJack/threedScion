package c3i.smartClient.client.skins.bytSkin;

import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.skins.Skin;
import c3i.smartClient.client.skins.angleSelectors.exterior.BytExteriorAngleSelector;
import c3i.smartClient.client.skins.angleSelectors.interior.BytInteriorAngleSelector2;
import c3i.smartClient.client.skins.viewSelectors.ThumbViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class BytMain implements Skin {

    ViewModel viewModel;
    BytExteriorAngleSelector exteriorAngleSelector;
    BytInteriorAngleSelector2 interiorAngleSelector;

    @Override
    public IsWidget createPreviewPanel(ViewModel viewModel) {
        this.viewModel = viewModel;
        ViewPanel viewPanel = new ViewPanel(viewModel);


        exteriorAngleSelector = new BytExteriorAngleSelector(viewModel.getViewModel(0));

        if (viewModel.getViews().size() > 1) {
            ViewModel viewModel1 = viewModel.getViewModel(1);
            interiorAngleSelector = new BytInteriorAngleSelector2(viewModel1);

        } else {
            interiorAngleSelector = null;
        }

        exteriorAngleSelector.addStyleName("over");
        if (interiorAngleSelector != null) {
            interiorAngleSelector.addStyleName("over");
        }

        exteriorAngleSelector.setVisible(true);
        if (interiorAngleSelector != null) {
            interiorAngleSelector.setVisible(false);
        }

        viewPanel.add(exteriorAngleSelector);

        if (interiorAngleSelector != null) {
            viewPanel.add(interiorAngleSelector);
        }

        viewModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey view) {
                refreshAngleSelectors();
            }
        });

        ThumbViewSelector viewSelector = new ThumbViewSelector(viewModel);


        FlowPanel p = new FlowPanel();
        p.add(viewPanel);

        p.add(viewSelector);
        p.getElement().getStyle().setWidth(viewPanel.getPreferredSize().getWidth(), Style.Unit.PX);

        refreshAngleSelectors();


        return p;
    }

    private void refreshAngleSelectors() {
        ImView view = viewModel.getView();
        ViewKey viewKey = view.getViewKey();
        exteriorAngleSelector.setVisible(viewKey.getViewIndex() == 0);
        if (interiorAngleSelector != null) {
            interiorAngleSelector.setVisible(viewKey.getViewIndex() == 1);
        }
    }

    @Override
    public String getSkinName() {
        return "Byt Main Page";
    }
}
