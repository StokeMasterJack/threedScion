package c3i.core.imageModel.shared;

import com.google.common.base.Preconditions;

public class PngKey {

    private final String shortSha;      //7 digits
    private final int deltaY;    //2 or 0 digits

    public PngKey(String shortSha, int deltaY) {
        Preconditions.checkNotNull(shortSha);
        Preconditions.checkArgument(shortSha.length() == 7);
        Preconditions.checkArgument(deltaY >= 0);
        Preconditions.checkArgument(deltaY <= 99);
        this.shortSha = shortSha;
        this.deltaY = deltaY;
    }

    public PngKey(String pngSegment) {
        if (pngSegment.length() == 9) {
            this.shortSha = pngSegment.substring(0, 7);
            String sTransform = pngSegment.substring(7);
            deltaY = new Integer(sTransform);
        } else if (pngSegment.length() == 7) {
            this.shortSha = pngSegment;
            this.deltaY = 0;
        } else {
            throw new IllegalArgumentException("Invalid pngSegment[" + pngSegment + "]");
        }
    }

    public String getShortSha() {
        return shortSha;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public static String serializeToUrlSegment(String shortSha, int deltaY) {
        if (deltaY == 0) {
            return shortSha;
        } else if (deltaY > 0 && deltaY < 10) {
            return shortSha + "0" + deltaY;
        } else if (deltaY >= 10 && deltaY <= 99) {
            return shortSha + deltaY + "";
        } else {
            throw new IllegalStateException();
        }
    }

    public String serializeToUrlSegment() {
        return serializeToUrlSegment(shortSha, deltaY);
    }

    @Override
    public String toString() {
        return "PngSegment{" +
                "shortSha='" + shortSha + '\'' +
                ", transform=" + deltaY +
                '}';
    }


}
