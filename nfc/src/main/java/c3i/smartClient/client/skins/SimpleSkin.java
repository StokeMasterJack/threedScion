package c3i.smartClient.client.skins;

import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.skins.angleSelectors.simple.SimpleSpinControl;
import c3i.smartClient.client.skins.viewSelectors.SimpleViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class SimpleSkin implements Skin {

    @Override
    public IsWidget createPreviewPanel(ViewModel viewModel) {

        ViewPanel viewPanel = new ViewPanel(viewModel);

        final SimpleSpinControl spinControl = new SimpleSpinControl(viewModel);
        spinControl.addStyleName("over");
        viewPanel.add(spinControl);

        viewModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey view) {
                spinControl.setVisible(view.getViewIndex() == 0);
            }
        });

        SimpleViewSelector viewSelector = new SimpleViewSelector(viewModel);

        FlowPanel p = new FlowPanel();
        p.add(viewPanel);
        p.add(viewSelector);

        return p;
    }

    @Override
    public String getSkinName() {
        return "SimpleSkin";
    }
}
