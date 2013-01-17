package c3i.admin.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import c3i.util.shared.events.ChangeListener;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.util.shared.futures.RWValue;

import java.util.List;

public class ProfileSelector extends FlowPanel {

    private final ListBox listBox;
    private final ThreedAdminModel model;
    private boolean suspended;

    public ProfileSelector(final ThreedAdminModel model) {
        this.model = model;

        final RWValue<Profile> profile = model.profile();
        listBox = createListBox();

        this.add(new InlineHTML("Profile: "));
        this.add(listBox);


        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                if (!suspended) {
                    Profile value = getValue();
                    profile.set(value);
                }
            }
        });


        profile.addChangeListener(new ChangeListener<Profile>() {
            @Override
            public void onChange(Profile newValue) {
                setValue(newValue);
            }
        });


    }

    private ListBox createListBox() {
        ListBox b = new ListBox();
        Profiles profiles = model.getProfiles();
        for (Profile profile : profiles.getList()) {
            b.addItem(profile.getKey());
        }
        return b;
    }


    private void setValue(Profile profile) {
        List<Profile> profiles = model.getProfiles().getList();
        int i = profiles.indexOf(profile);
        suspend();
        listBox.setSelectedIndex(i);
        resume();
    }

    private void suspend() {
        this.suspended = true;
    }

    private void resume() {
        this.suspended = false;
    }

    private Profile getValue() {
        List<Profile> profiles = model.getProfiles().getList();
        int i = listBox.getSelectedIndex();
        if (i == -1) {
            throw new IllegalStateException("No profile selected");
        }
        return profiles.get(i);
    }


}
