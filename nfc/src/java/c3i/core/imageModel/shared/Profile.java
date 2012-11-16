package c3i.core.imageModel.shared;

import c3i.core.threedModel.shared.JpgWidth;
import com.google.common.base.Preconditions;
import smartsoft.util.shared.RectSize;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

@Immutable
public class Profile implements Serializable {

    private static final long serialVersionUID = 253786519132787128L;

    private String key;
    private RectSize image;

    private JpgWidth jpgWidth;
    private BaseImageType baseImageType;

    public static Profile STD = new Profile("wStd", RectSize.STD_PNG, BaseImageType.JPG);

    public Profile(String key, RectSize image) {
        this(key, image, BaseImageType.JPG);
    }

    public Profile(String key, RectSize image, BaseImageType baseImageType) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(image);
        Preconditions.checkNotNull(baseImageType);
        this.key = key;
        this.image = image;
        if (baseImageType == null) {
            this.baseImageType = BaseImageType.JPG;
        } else {
            this.baseImageType = baseImageType;
        }
        jpgWidth = new JpgWidth(key, image.getWidth());
    }

    public Profile(JpgWidth jpgWidth) {
        this.key = jpgWidth.getKey();
        this.image = jpgWidth.getJpgSize();
        jpgWidth = new JpgWidth(key, image.getWidth());
        this.baseImageType = BaseImageType.JPG;
    }

    private Profile() {
    }

    public RectSize getImageSize() {
        return image;
    }


    public String getKey() {
        return key;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile that = (Profile) o;

        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Profile{" +
                "key='" + key + '\'' +
                ", jpgWidth=" + image.getWidth() +
                ", baseImageType=" + baseImageType +
                '}';
    }

    public BaseImageType getBaseImageType() {
        Preconditions.checkNotNull(baseImageType);
        return baseImageType;
    }

    public boolean includeBackgroundLayer() {
        return baseImageType == BaseImageType.JPG;
    }


    public boolean isJpg() {
        return baseImageType == BaseImageType.JPG;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public boolean isStandard() {
        return key.contains("wStd");
    }

    public boolean isScaled() {
        return !isStandard();
    }
}
