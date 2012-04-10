package threed.core.imageModel.shared;

public class NumAnglesBothEitherSide implements CacheAheadPolicy {

    private final int numberOfAnglesToCache;

    public NumAnglesBothEitherSide(int numberOfAnglesToCache) {
        this.numberOfAnglesToCache = numberOfAnglesToCache;
    }

    public int getNumberOfAnglesToCache() {
        return numberOfAnglesToCache;
    }

    @Override
    public boolean isNoCacheAhead() {
        return false;
    }
}
