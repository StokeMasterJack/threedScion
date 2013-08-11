package c3i.core.imageModel.shared;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static smartsoft.util.shared.Strings.notEmpty;

public class Profiles implements Serializable {

    private ArrayList<Profile> profileList;

    private HashMap<String, Profile> map;

    public Profiles(ArrayList<Profile> profileList) {
        Preconditions.checkNotNull(profileList, "profileList is null");
        Preconditions.checkArgument(!profileList.isEmpty(), "profileList is empty");

        for (Profile profile : profileList) {
            profile.getBaseImageType();
        }

        this.profileList = profileList;

        map = new HashMap<String, Profile>();
        for (Profile p : profileList) {
            p.getBaseImageType();
            map.put(p.getKey(), p);
        }


    }

    private Profiles() {
    }

    public List<Profile> getList() {
        return profileList;
    }

    public Map<String, Profile> getMap() {
        return map;

    }

    public Profile get(String key) {
        Preconditions.checkArgument(notEmpty(key));
        Profile profile = map.get(key);
        if (profile == null) {
            throw new IllegalArgumentException("Bad profile key[" + key + "]");
        }
        return profile;
    }

    public Profile getDefaultProfile() {
        Profile profile = profileList.get(0);
        profile.getBaseImageType();
        return profile;
    }

}
