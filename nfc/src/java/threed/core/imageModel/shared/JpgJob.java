package threed.core.imageModel.shared;

import threed.core.threedModel.shared.JpgWidth;

public class JpgJob {

    private final JpgWidth width; //jpg width in pixels

    public JpgJob(JpgWidth width) {
        assert width != null;
        this.width = width;
    }

    public JpgWidth getWidth() {
        return width;
    }

    @Override public String toString() {
        return "JpgJob{" +
                "width=" + width +
                '}';
    }
}
