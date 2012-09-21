package c3i.core.imageModel.shared;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.threedModel.shared.BaseImageKey;
import c3i.core.threedModel.shared.Slice2;
import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

/**
 * This class represents a stack of images such that:
 * <p/>
 * 1.  Each image in the stack has the same x,y,width,height but different zIndex
 * 2.  The bottom image is always a JPG all others are PNGs
 */
@Immutable
public class CoreImageStack {

    private final Key key;

    private final RawImageStack rawImageStack;

    private final BaseImage baseImage;
    private final ImmutableList<PngSpec> zPngs;

    private final ImmutableList<ImImage> images;

    public CoreImageStack(RawImageStack rawImageStack, Profile profile, ImageMode imageMode) {
        this.rawImageStack = rawImageStack;
        this.key = new Key(rawImageStack.getSpec(), profile, imageMode);
        baseImage = initBaseImage();
        zPngs = initZPngs();
        images = initImages(baseImage, zPngs);
    }

    private BaseImage initBaseImage() {
        if (key.imageMode.isPngMode()) {
            return null;
        } else {
            ImmutableList.Builder<PngKey> builder = ImmutableList.builder();
            for (final PngSpec pngSpec : getBasePngs()) {
                PngKey pk = new PngKey(pngSpec.getShortSha(), pngSpec.getDeltaY());
                builder.add(pk);
            }
            ImmutableList<PngKey> pngs = builder.build();
            return new BaseImage(getProfile(), getSlice2(), pngs);
        }
    }

    public Key getKey() {
        return key;
    }


    public ImmutableList<PngSpec> getBlinkPngs() {
        return rawImageStack.getBlinkPngs();
    }

    public ImmutableList<PngSpec> getSrcPngs() {
        return rawImageStack.getAllPngs();
    }

    public void print() {
        rawImageStack.print();
    }

    public BaseImageKey getBaseImageKey() {
        ImView view = rawImageStack.getView();
        String jpgFingerprint = getBaseImageFingerprint();
        SeriesKey seriesKey = view.getSeries().getSeriesKey();
        Profile profile = key.getProfile();
        return new BaseImageKey(seriesKey, profile, jpgFingerprint);
    }


    public BaseImage getBaseImage() {
        return baseImage;
    }

    public BaseImage getJpg() {
        return getBaseImage();
    }

    private Profile getProfile() {
        return key.getProfile();
    }

    public ImView getView() {
        return rawImageStack.getImView();
    }


    private ImmutableList<ImImage> initImages(BaseImage baseImage, ImmutableList<PngSpec> zPngs) {
        ImmutableList.Builder<ImImage> builder = ImmutableList.builder();
        if (key.getFixedPicks().isValidBuild()) {

            if (baseImage != null) {
                builder.add(baseImage);
            }

            for (final PngSpec zPng : zPngs) {
                builder.add(new LayerImage(getProfile(), zPng));
            }
        }

        return builder.build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoreImageStack that = (CoreImageStack) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public int getZCount() {
        return rawImageStack.getZCount();
    }


    public String getBaseImageFingerprint() {
        return getJpg().getFingerprint();
    }

    public ViewKey getViewKey() {
        return key.getViewKey();
    }

    public int getImageCount() {
        return images.size();
    }

    public int getSrcPngCount() {
        return rawImageStack.getSrcPngCount();
    }

    public int getAngle() {
        return key.getAngle();
    }

    public Slice2 getSlice2() {
        return new Slice2(getView(), getAngle());
    }

    public ImmutableList<ImImage> getImages() {
        return images;
    }

    public static class Key {

        private final RawImageStack.Key rawKey;
        private final Profile profile;
        private final ImageMode imageMode;

        public Key(RawImageStack.Key rawKey, Profile profile, ImageMode imageMode) {
            this.rawKey = rawKey;
            this.profile = profile;
            this.imageMode = imageMode;
        }

        public RawImageStack.Key getRawKey() {
            return rawKey;
        }

        public AngleKey getAngleKey() {
            return rawKey.getAngleKey();
        }

        public Profile getProfile() {
            return profile;
        }

        public ImageMode getImageMode() {
            return imageMode;
        }

        public FixedPicks getFixedPicks() {
            return rawKey.getFixedPicks();
        }

        public ViewKey getViewKey() {
            return rawKey.getViewKey();
        }

        public int getViewIndex() {
            return getViewKey().getViewIndex();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (imageMode != key.imageMode) return false;
            if (!profile.equals(key.profile)) return false;
            if (!rawKey.equals(key.rawKey)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = rawKey.hashCode();
            result = 31 * result + profile.hashCode();
            result = 31 * result + imageMode.hashCode();
            return result;
        }

        public int getAngle() {
            return rawKey.getAngle();
        }


    }

    public FixedPicks getFixedPicks() {
        return getKey().getFixedPicks();
    }

    public boolean hasBackground() {
        return key.profile.includeBackgroundLayer();
    }

    private ImmutableList<PngSpec> getBasePngs() {
        if (key.imageMode.isPngMode()) {
            throw new IllegalStateException();
        }

        final ImmutableList<PngSpec> allPngs = rawImageStack.getAllPngs();
        final ImmutableList.Builder<PngSpec> jpgPngs = ImmutableList.builder();

        for (PngSpec srcPng : allPngs) {
            if (srcPng.isBackground() && !hasBackground()) {
                continue;
            }

            if (srcPng.isZLayer()) {
                break;
            }

            jpgPngs.add(srcPng);


        }
        return jpgPngs.build();
    }

    private ImmutableList<PngSpec> initZPngs() {
        ImageMode imageMode = key.imageMode;
        if (imageMode.isSkipZLayers()) {
            return ImmutableList.of();
        }

        final ImmutableList<PngSpec> allPngs = rawImageStack.getAllPngs();
        final ImmutableList.Builder<PngSpec> zPngs = ImmutableList.builder();

        for (PngSpec pngSpec : allPngs) {
            if (pngSpec.isBackground() && !hasBackground()) {
                continue;
            }

            if (imageMode.isPngMode()) {
                zPngs.add(pngSpec);
            } else if (imageMode.isJpgMode()) {
                if (pngSpec.isZLayer()) {
                    zPngs.add(pngSpec);
                }
            } else {
                throw new IllegalStateException();
            }


        }
        return zPngs.build();
    }

}
