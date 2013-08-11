package c3i.core.imageModel.shared;

import c3i.core.threedModel.shared.JpgWidth;

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
