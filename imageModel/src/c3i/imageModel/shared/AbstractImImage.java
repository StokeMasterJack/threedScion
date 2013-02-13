package c3i.imageModel.shared;

abstract public class AbstractImImage implements ImImage {

    protected final Profile profile;

    public AbstractImImage(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }


}
