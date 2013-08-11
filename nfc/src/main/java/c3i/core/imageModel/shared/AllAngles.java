package c3i.core.imageModel.shared;

public class AllAngles implements CacheAheadPolicy {

    private final ImView view;

    public AllAngles(ImView view) {
        this.view = view;
    }

    public AngleList getAnglesToCache(final int currentAngle) {

        int totalAngleCount = view.getAngleCount();
        int prefetchCount = totalAngleCount - 1;

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

            if (anglesToPreCache.size() == prefetchCount) {
                return anglesToPreCache;
            }

            base++;

        }

    }

}
