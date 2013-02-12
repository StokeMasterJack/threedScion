package c3i.imageModel.shared;

import com.google.common.base.Preconditions;

/**
 * Represents one segment of a png layer stack like this:
 *
 * 7126703-b357925-63985d3
 *
 */
public class PngSegment {

    private final String shortSha;      //7 digits
    private final int deltaY;    //2 or 0 digits

    public PngSegment(String shortSha, int deltaY) {
        Preconditions.checkNotNull(shortSha);
        Preconditions.checkArgument(shortSha.length() == 7);
        Preconditions.checkArgument(deltaY >= 0);
        Preconditions.checkArgument(deltaY <= 99);
        this.shortSha = shortSha;
        this.deltaY = deltaY;
    }

    public PngSegment(String pngSegment) {
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

    public static String serializeUrlSegment(String shortSha, int deltaY) {
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
        return serializeUrlSegment(shortSha, deltaY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PngSegment that = (PngSegment) o;
        return shortSha.equals(that.shortSha) && deltaY != that.deltaY;
    }

    @Override
    public int hashCode() {
        int result = shortSha.hashCode();
        result = 31 * result + deltaY;
        return result;
    }

    @Override
    public String toString() {
        return serializeToUrlSegment();
    }


}
