package c3i.imgGen.api;

public interface Kit<ID> {

    FeatureModelFactory<ID> createFeatureModelFactory();

    ImageModelFactory<ID> createImageModelFactory();

    SrcPngLoader createSrcPngLoader();

}
