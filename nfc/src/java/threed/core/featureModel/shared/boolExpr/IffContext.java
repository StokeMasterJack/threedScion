package threed.core.featureModel.shared.boolExpr;

public interface IffContext {

    boolean putSingleVarIff(Var var, BoolExpr exp);

    BoolExpr getReplacement(BoolExpr e);
}
