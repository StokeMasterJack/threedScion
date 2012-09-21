package c3i.repo.shared;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import c3i.core.imageModel.shared.Profile;

import java.io.Serializable;
import java.util.ArrayList;

public class Settings implements Serializable {

//    private static final long serialVersionUID = -8175875818895006502L;

    private int threadCount;
    private ArrayList<Profile> profiles;

    public Settings(Integer threadCount, ArrayList<Profile> profiles) {
        Preconditions.checkNotNull(profiles);
//        Preconditions.checkArgument(profiles.size() > 0);
        this.threadCount = threadCount;
        this.profiles = profiles;
    }

    private Settings() {
    }

    public int getThreadCount() {
        return threadCount;
    }

    public ImmutableList<Profile> getProfiles() {
        Preconditions.checkArgument(profiles != null);
        Preconditions.checkArgument(profiles.size() > 0);
        ImmutableList.Builder<Profile> builder = ImmutableList.builder();
        for (Profile profile : profiles) {
            if (profile == null) throw new IllegalStateException();
            builder.add(profile);
        }
        return builder.build();
    }

//    public static Settings createDefault() {
//        ArrayList<Profile> a = new ArrayList<Profile>();
//        a.add(new Profile(JpgWidth.W_STD));
//
//
//        return new Settings(5, a);
//    }

    @Override
    public String toString() {
        return "Settings{" +
                "threadCount=" + threadCount +
                ", profiles=" + profiles +
                '}';
    }


    public static Settings createDefault() {
        return new Settings(5, new ArrayList<Profile>());
    }
}
