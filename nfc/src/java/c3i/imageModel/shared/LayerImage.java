package c3i.imageModel.shared;

import smartsoft.util.shared.Path;

public class LayerImage extends AbstractImImage {

    private final PngSpec pngSpec;

    public LayerImage(Profile profile,PngSpec pngSpec) {
        super(profile);
        this.pngSpec = pngSpec;
    }

    @Override
    public Path getUrl(Path repoBase) {
        return pngSpec.getUrl(repoBase);
    }

    @Override
    public boolean isLayerPng() {
        return true;
    }

    public PngSpec getSrcPng() {
        return pngSpec;
    }
}
