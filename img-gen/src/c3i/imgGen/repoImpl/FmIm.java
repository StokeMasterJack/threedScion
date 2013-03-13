package c3i.imgGen.repoImpl;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.ImageModel;

public class FmIm<ID> {

    private final ID id;
    private final FeatureModel fm;
    private final ImageModel<Var> im;

    public FmIm(ID id, FeatureModel fm, ImageModel<Var> im) {
        this.id = id;
        this.fm = fm;
        this.im = im;
    }

    public FeatureModel getFeatureModel() {
        return fm;
    }

    public ImageModel<Var> getImageModel() {
        return im;
    }

    public ID getId() {
        return id;
    }




}
