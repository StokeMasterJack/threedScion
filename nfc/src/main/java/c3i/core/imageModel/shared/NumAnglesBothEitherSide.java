package c3i.core.imageModel.shared;

public class NumAnglesBothEitherSide implements CacheAheadPolicy {

    private final ImView view;
    private final int numberOfAnglesToCachePerSide;

    public NumAnglesBothEitherSide(ImView view, int numberOfAnglesToCachePerSide) {
        this.view = view;
        this.numberOfAnglesToCachePerSide = numberOfAnglesToCachePerSide;
    }

    public AngleList getAnglesToCache(final int currentAngle) {

        int totalAngleCount = view.getAngleCount();
        int maxPrefetchCount = totalAngleCount - 1;

        AngleList anglesToPreCache = new AngleList(view);

        int failSafeStopCount = 30;

        int base = 1;
        while (true) {

            if (anglesToPreCache.size() > failSafeStopCount) {
                throw new IllegalStateException();
            }

            int af = view.getNext(currentAngle, base);
            if (!anglesToPreCache.contains(af) && af != currentAngle) {
                anglesToPreCache.add(af);
            }


            int ap = view.getPrevious(currentAngle, base);
            if (!anglesToPreCache.contains(ap) && ap != currentAngle) {
                anglesToPreCache.add(ap);
            }

            if (base >= numberOfAnglesToCachePerSide || anglesToPreCache.size() > maxPrefetchCount) {
                return anglesToPreCache;
            }

            base++;

        }
    }
}
