package c3i.imgGen.repoImpl;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.ImageModel;

public class FmIm<ID> {

    private final ID id;
    private final FeatureModel fm;
    private final ImageModel im;

    public FmIm(ID id, FeatureModel fm, ImageModel im) {
        this.id = id;
        this.fm = fm;
        this.im = im;
    }

    public FeatureModel getFeatureModel() {
        return fm;
    }

    public ImageModel getImageModel() {
        return im;
    }

    public ID getId() {
        return id;
    }




}
