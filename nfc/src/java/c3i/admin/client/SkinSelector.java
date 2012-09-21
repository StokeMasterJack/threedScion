package c3i.admin.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import c3i.util.shared.events.ChangeListener;
import c3i.smartClient.client.skins.Skin;

import java.util.List;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class SkinSelector extends FlowPanel {

    private final ListBox listBox;
    private final ImmutableList<Skin> skins;

    private boolean suspended;

    public SkinSelector(final ThreedAdminModel model) {
        this.skins = model.getSkins();

        listBox = createListBox(skins);

        getElement().getStyle().setPadding(.3, Style.Unit.EM);

        this.add(new InlineHTML("Skin: "));
        this.add(listBox);

//            this.setHeight("3em");

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (!suspended) {
                    Skin selectedValue = getValue();
                    model.skin().set(selectedValue);
                }
            }
        });

        addStyleName(getSimpleName(this));


        model.skin().addChangeListener(new ChangeListener<Skin>() {
            @Override
            public void onChange(Skin newValue) {
                setValue(newValue);
            }
        });

        setValue(model.skin().get());

    }


    private ListBox createListBox(List<Skin> skins) {
        ListBox b = new ListBox();
        for (Skin skin : skins) {
            b.addItem(skin.getSkinName());
        }
        return b;
    }

    private Skin getValue() {
        int i = listBox.getSelectedIndex();
        return skins.get(i);
    }

    private void setValue(Skin skin) {
        suspended = true;
        int i = skins.indexOf(skin);
        listBox.setSelectedIndex(i);
        suspended = false;
    }
}
