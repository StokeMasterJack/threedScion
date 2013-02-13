package c3i.core.threedModel.shared;

import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.SeriesKey;
import c3i.imageModel.shared.SimpleFeatureModel;
import c3i.imageModel.shared.SimplePicks;

public class ImFeatureModel implements SimpleFeatureModel {

    protected final FeatureModel featureModel;

    public ImFeatureModel(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    @Override
    public Object get(String varCode) {
        return featureModel.get(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return fmToMmSeriesKey(featureModel.getSeriesKey());
    }

    @Override
    public boolean containsCode(String varCode) {
        return featureModel.containsCode(varCode);
    }

    public static c3i.core.common.shared.SeriesKey imToFmSeriesKey(c3i.imageModel.shared.SeriesKey seriesKey) {
        return new c3i.core.common.shared.SeriesKey(seriesKey.getBrand(), seriesKey.getYear(), seriesKey.getName());
    }

    public static c3i.imageModel.shared.SeriesKey fmToMmSeriesKey(c3i.core.common.shared.SeriesKey seriesKey) {
        return new c3i.imageModel.shared.SeriesKey(
                seriesKey.getBrandKey().getKey(),
                seriesKey.getYear(),
                seriesKey.getName());
    }

    public static SimplePicks toSimplePicks(FixedPicks fixedPicks) {
        return new FixedPicksAdapter(fixedPicks);
    }

    public static SimplePicks toSimplePicks(Assignments assignments) {
        return new AssignmentsAdapter(assignments);
    }

    public static class FixedPicksAdapter implements SimplePicks {
        private FixedPicks fixedPicks;

        public FixedPicksAdapter(FixedPicks fixedPicks) {
            this.fixedPicks = fixedPicks;
        }

        @Override
        public boolean isPicked(Object var) {
            return fixedPicks.isPicked((Var) var);
        }

        @Override
        public boolean isValidBuild() {
            return fixedPicks.isValidBuild();
        }
    }

    public static class AssignmentsAdapter implements SimplePicks {
        private Assignments assignments;

        public AssignmentsAdapter(Assignments assignments) {
            this.assignments = assignments;
        }

        @Override
        public boolean isPicked(Object var) {
            return assignments.isPicked((Var) var);
        }

        @Override
        public boolean isValidBuild() {
            return true;
        }
    }

}
