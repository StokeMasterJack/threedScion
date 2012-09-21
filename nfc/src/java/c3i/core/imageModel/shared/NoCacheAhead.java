package c3i.core.imageModel.shared;

public class NoCacheAhead implements CacheAheadPolicy {

    private final ImView view;

    public NoCacheAhead(ImView view) {
        this.view = view;
    }

    @Override
    public AngleList getAnglesToCache(int currentAngle) {
        return new AngleList(view);
    }
}
