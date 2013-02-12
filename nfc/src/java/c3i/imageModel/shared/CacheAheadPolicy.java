package c3i.imageModel.shared;

import java.util.ArrayList;

public interface CacheAheadPolicy {

    public AngleList getAnglesToCache(int currentAngle);

    public static class AngleList extends ArrayList<Integer> {

        private final ImView view;

        public AngleList(ImView view) {
            this.view = view;
        }

        @Override
        public boolean add(Integer angle) {
            int max = view.getAngleCount();
            if (angle == null) throw new IllegalStateException("Null angle for view[" + view + "]");
            if (angle <= 0) throw new IllegalStateException("Bad angle[" + angle + "] for view[" + view + "]");
            if (angle > max) {
                throw new IllegalStateException("Bad angle[" + angle + "] for view[" + view + "] max[" + max + "]");
            }
            return super.add(angle);
        }
    }

}
