package c3i.core.imageModel.shared;

import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.boolExpr.Var;

import java.util.Set;

public interface ViewSlice {

    Slice getSlice();

    Set<Var> getPngVars();

    int getAngle();

    RawImageStack getRawImageStack(AssignmentsForTreeSearch product);
}
