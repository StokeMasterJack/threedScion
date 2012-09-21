package c3i.repo.server;

import c3i.core.imageModel.server.ImageUtil;
import c3i.core.imageModel.shared.BaseImageType;
import com.google.common.base.Preconditions;

import java.io.File;

public class TwoThirty8 {

    private final String two;
    private final String thirty8;

    public TwoThirty8(String two, String thirty8) {
        this.two = two;
        this.thirty8 = thirty8;
    }

    public String getTwo() {
        return two;
    }

    public String getThirty8() {
        return thirty8;
    }

    public File getFileName(File prefix,BaseImageType baseImageType) {
        Preconditions.checkNotNull(baseImageType);
        File twoFile = new File(prefix, two);
        String ext = baseImageType.getFileExtension();
        return new File(twoFile, thirty8 + "." + ext);
    }

    @Override
    public String toString() {
        return "TwoThirty8{" +
                "two='" + two + '\'' +
                ", thirty8='" + thirty8 + '\'' +
                '}';
    }

    public static TwoThirty8 getTwoThirty8(String jpgFingerprint) {
        String shortFingerprint = ImageUtil.getFingerprint(jpgFingerprint);
        String two = shortFingerprint.substring(0, 2);
        String thirty8 = shortFingerprint.substring(2);
        TwoThirty8 twoThirty8 = new TwoThirty8(two, thirty8);
        return twoThirty8;
    }
}
