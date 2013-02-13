package c3i.imageModel.shared;

public class PngMatch {

    private SrcPng png;

    public void add(SrcPng newPng) {
        assert newPng != null;
        if (png == null) {
            this.png = newPng;
        } else {
            if (newPng.hasFeature("2Q")) {
                System.out.println();
            }
            if (newPng.getFeatureCount() > png.getFeatureCount()) {
                this.png = newPng;
            }
        }
    }

    public SrcPng getPng() {
        return png;
    }
}
