package c3i.smartClient.client.skins.viewSelectors;

import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ImageStack;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class SimpleViewSelector extends ViewSelector {

    private final ViewModel viewPanelModel;
    private static final int WIDTH = 150;
    private final ListBox listBox;

    public SimpleViewSelector(final ViewModel viewPanelModel) {
        this.viewPanelModel = viewPanelModel;
        listBox = new ListBox();
        initWidget(listBox);
        listBox.setWidth(WIDTH + "px");

        for (ImView view : viewPanelModel.getViews()) {
            listBox.addItem(view.getName(), view.getIndex() + "");
        }

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                viewPanelModel.setViewIndex(getValue());
            }
        });

        viewPanelModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey newValue) {
                refresh();
            }
        });

        listBox.addStyleName("ViewSelector");
        listBox.addStyleName("Simple");

        refresh();


    }

    private void setValue(int viewIndex) {
        listBox.setSelectedIndex(viewIndex);
    }

    private int getValue() {
        int viewIndex = listBox.getSelectedIndex();
        return viewIndex;
    }


    private void refresh() {
        int viewIndex = viewPanelModel.getViewIndex();
        setValue(viewIndex);
        ImageStack imageStack = viewPanelModel.getImageStack();
        if (imageStack == null || imageStack.getFixedPicks() == null || !imageStack.getFixedPicks().isValidBuild()) {
            asWidget().setVisible(true);
        } else {
            asWidget().setVisible(true);
        }
    }


}
