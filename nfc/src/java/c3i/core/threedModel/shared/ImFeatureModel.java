package c3i.core.threedModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.SimpleFeatureModel;
import c3i.imageModel.shared.SimplePicks;

public class ImFeatureModel implements SimpleFeatureModel<Var> {

    protected final FeatureModel featureModel;

    public ImFeatureModel(FeatureModel featureModel) {
        this.featureModel = featureModel;
    }

    @Override
    public Var getVar(String varCode) {
        return featureModel.get(varCode);
    }

    @Override
    public SeriesKey getSeriesKey() {
        return featureModel.getSeriesKey();
    }

    @Override
    public boolean containsVarCode(String varCode) {
        return featureModel.containsCode(varCode);
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
