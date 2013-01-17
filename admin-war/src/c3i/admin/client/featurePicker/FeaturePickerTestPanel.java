package c3i.admin.client.featurePicker;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class FeaturePickerTestPanel extends DockLayoutPanel {

    private final VarPanelModel context;
    private final VarPanel rootVarPanel;
    private final PicksInfoPanel picksInfo;

    public FeaturePickerTestPanel(VarPanel rootVarPanel, VarPanelModel context, PicksInfoPanel picksInfo) {
        super(Style.Unit.EM);
        this.context = context;
        this.rootVarPanel = rootVarPanel;
        this.picksInfo = picksInfo;

        addWest(new FillerPanel(), 1);
        addEast(new FillerPanel(), 1);
        addNorth(new FillerPanel(), 1);
        addSouth(new FillerPanel(), 1);
        add(new MainPanel());


    }


    public class MainPanel extends DockLayoutPanel {
        public MainPanel() {
            super(Style.Unit.EM);

            addWest(rootVarPanel, 30);
            addEast(picksInfo, 30);

            getElement().getStyle().setBackgroundColor("green");
        }
    }

    public class FillerPanel extends FlowPanel {
        public FillerPanel() {
            getElement().getStyle().setBackgroundColor("#CCCCCC");
        }
    }

}
