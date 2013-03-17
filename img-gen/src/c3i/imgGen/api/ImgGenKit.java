package c3i.imgGen.api;

public interface ImgGenKit<ID> {

    FeatureModelFactory createFeatureModelFactory();

    ImageModelFactory createImageModelFactory();

    ThreedModelFactory<ID> createThreedModelFactory(FeatureModelFactory<ID> featureModelFactory, ImageModelFactory<ID> imageModelFactory);

    SrcPngLoader createSrcPngLoader();
}
