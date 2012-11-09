package c3i.admin.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import c3i.util.shared.events.ChangeListener;
import c3i.core.imageModel.shared.ImageMode;
import c3i.util.shared.futures.RWValue;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

class ModeSelector extends FlowPanel {

    private final ListBox listBox;

    private boolean suspended;

    ModeSelector(final RWValue<ImageMode> imageMode) {
        listBox = createListBox();

        getElement().getStyle().setPadding(.3, Style.Unit.EM);

        this.add(new InlineHTML("Mode: "));
        this.add(listBox);

//            this.setHeight("3em");

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (!suspended) {
                    ImageMode selectedValue = getValue();
                    imageMode.set(selectedValue);
                }
            }
        });

        addStyleName(getSimpleName(this));

        imageMode.addChangeListener(new ChangeListener<ImageMode>() {
            @Override
            public void onChange(ImageMode newValue) {
                setValue(newValue);
            }
        });

        setValue(imageMode.get());

    }


    private ListBox createListBox() {
        ListBox b = new ListBox();
        ImageMode[] values = ImageMode.values();
        for (ImageMode value : values) {
            b.addItem(value.name());
        }
        return b;
    }

    private ImageMode getValue() {
        int i = listBox.getSelectedIndex();
        String itemText = listBox.getItemText(i);
        return ImageMode.valueOf(itemText);
    }

    private void setValue(ImageMode imageMode) {
        suspended = true;
        listBox.setSelectedIndex(imageMode.ordinal());
        suspended = false;
    }
}
