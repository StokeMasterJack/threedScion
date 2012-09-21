package c3i.core.featureModel.shared.boolExpr;

public interface IffContext {

    boolean putSingleVarIff(Var var, BoolExpr exp);

    BoolExpr getReplacement(BoolExpr e);
}
