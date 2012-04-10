package threed.core.imageModel.shared;

public class NoCacheAhead implements CacheAheadPolicy {

    @Override
    public boolean isNoCacheAhead() {
        return true;
    }

}
