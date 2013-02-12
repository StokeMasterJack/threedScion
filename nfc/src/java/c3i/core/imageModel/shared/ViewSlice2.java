package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;

import java.util.Set;

public class ViewSlice2 implements ViewSlice {

    private final ImView view;
    private final int angle;

    private final Slice slice;

    public ViewSlice2(ImView view, int angle) {
        this.view = view;
        this.angle = angle;
        slice = new Slice(view.getName(), angle);
    }

    @Override
    public Slice getSlice() {
        return slice;
    }

    @Override
    public Set<Var> getPngVars() {
        return view.getPngVars(angle);
    }

    @Override
    public int getAngle() {
        return angle;
    }

    @Override
    public RawImageStack getRawImageStack(AssignmentsForTreeSearch product) {
        FixedPicks fixedPicks = new FixedPicks(null, product, null);
        return view.getRawImageStack(fixedPicks, angle);
    }
}
