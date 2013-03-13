package c3i.imageModel.shared;

import c3i.featureModel.shared.common.SimplePicks;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import smartsoft.util.shared.Path;

import javax.annotation.concurrent.Immutable;
import java.util.logging.Logger;

/**
 *  This class represents a stack of images such that:
 *
 *      Each image in the stack has the same x,y,width,height but different zIndex
 *      The bottom image is always a JPG all others are PNGs
 *
 *      It does not include profile.
 */
@Immutable
public class RawImageStack {

    protected final Key spec;
    protected final ImView imView;
    protected final ImmutableList<PngSpec> allPngs;
    protected final int zCount;

    private String jpgFingerprint;

    public RawImageStack(SimplePicks fixedPicks, int angle, ImView view, ImmutableList<PngSpec> allPngs) {
        this(new Key(new AngleKey(view.getViewKey(), angle), fixedPicks), view, allPngs);
    }

    public RawImageStack(Key spec, ImView imView, ImmutableList<PngSpec> allPngs) {
        Preconditions.checkNotNull(allPngs);
        Preconditions.checkArgument(!allPngs.isEmpty(), "ImageStack has no pngs for [" + imView + "] angle[" + spec.getAngle() + "]");

        this.spec = spec;
        this.imView = imView;
        this.allPngs = allPngs;

        int c = 0;
        for (PngSpec png : allPngs) {
            if (png.isZLayer()) c++;
        }
        this.zCount = c;
    }

    public Key getSpec() {
        return spec;
    }

    public int getAngle() {
        return spec.getAngle();
    }

    public AngleKey getAngleKey() {
        return spec.getAngleKey();
    }

    public Path getContextPath() {
        return getAngleKey().getPath();
    }

    public ImmutableList<PngSpec> getBasePngs1() {
        return getBasePngs1(true);
    }

    public ImmutableList<PngSpec> getBasePngs1(boolean includeBackgroundLayer) {
        ImmutableList.Builder<PngSpec> b = ImmutableList.builder();
        for (PngSpec png : allPngs) {
            if (png.isBackground() && !includeBackgroundLayer) {
                continue;
            }
            if (png.isZLayer()) {
                break;
            }
            b.add(png);
        }
        return b.build();
    }

    public ImmutableList<PngSegment> getBasePngs2() {
        return toPngSegmentList(getBasePngs1());
    }

    public ImmutableList<PngSegment> getBasePngs2(boolean skipBackgroundLayer) {
        return toPngSegmentList(getBasePngs1(skipBackgroundLayer));
    }

    public RawBaseImage getBasePngs3() {
        return new RawBaseImage(getBasePngs2(true));
    }

    public RawBaseImage getBasePngs3(boolean skipBackgroundLayer) {
        return new RawBaseImage(getBasePngs2(skipBackgroundLayer));
    }

    public ImmutableList<PngSpec> getZPngs1() {
        ImmutableList.Builder<PngSpec> b = ImmutableList.builder();
        for (PngSpec png : allPngs) {
            if (png.isZLayer()) {
                b.add(png);
            }

        }
        return b.build();
    }

    public ImmutableList<PngSegment> getZPngs2() {
        return toPngSegmentList(getZPngs1());
    }


    public ImmutableList<PngSpec> getAllPngs1() {
        return allPngs;
    }

    public ImmutableList<PngSegment> getAllPngs2() {
        return toPngSegmentList(getAllPngs1());
    }

    public ImmutableList<PngSegment> toPngSegmentList(ImmutableList<PngSpec> pngSpecList) {
        ImmutableList.Builder<PngSegment> builder = ImmutableList.builder();
        for (final PngSpec pngSpec : pngSpecList) {
            PngSegment pk = new PngSegment(pngSpec.getShortSha(), pngSpec.getDeltaY());
            builder.add(pk);
        }
        return builder.build();
    }

//    public ImmutableList<SrcPng> getSrcPngsForOneLayer() {
//        for (SrcPng srcPng : srcPngs) {
//            ImLayer layer = srcPng.getLayer();
//        }
//        return srcPngs;
//    }

    public void print() {
        System.out.println("RawImageStack:");
        System.out.println("\t srcPngs: ");
        for (PngSpec png : allPngs) {
            System.out.println("\t\t" + png);
        }
        System.out.println();
    }

    public ImView getImView() {
        return imView;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawImageStack that = (RawImageStack) o;
        return allPngs.equals(that.allPngs);
    }

    @Override
    public int hashCode() {
        return allPngs.hashCode();
    }

    public int getZCount() {
        return zCount;
    }


    public ViewKey getViewKey() {
        return spec.getViewKey();
    }

    public int getSrcPngCount() {
        return allPngs.size();
    }

    public ImView getView() {
        return imView;
    }

    public String getBaseUrl() {
        return getJpgFingerprint();
    }


    public static class Key {

        private final AngleKey angleKey;
        private final SimplePicks fixedPicks;

        public Key(AngleKey angleKey, SimplePicks fixedPicks) {
            this.angleKey = angleKey;
            this.fixedPicks = fixedPicks;
        }

        public AngleKey getAngleKey() {
            return angleKey;
        }

        public int getAngle() {
            return getAngleKey().getAngle();
        }

        public SimplePicks getPicks() {
            return fixedPicks;
        }

        public ViewKey getViewKey() {
            return angleKey.getViewKey();
        }

        public int getViewIndex() {
            return getViewKey().getViewIndex();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key that = (Key) o;
            return angleKey.equals(that.angleKey) && fixedPicks.equals(that.fixedPicks);
        }

        @Override
        public int hashCode() {
            int result = angleKey.hashCode();
            result = 31 * result + fixedPicks.hashCode();
            return result;
        }
    }

    public CoreImageStack getCoreImageStack(Profile profile, ImageMode imageMode) {
        return new CoreImageStack(this, profile, imageMode);
    }


    public String getJpgFingerprint() {
        if (jpgFingerprint == null) {
            CoreImageStack coreImageStack = getCoreImageStack(null, ImageMode.JPG);
            jpgFingerprint = coreImageStack.getBaseImageFingerprint();
        }
        return jpgFingerprint;
    }

    private static Logger log = Logger.getLogger(RawImageStack.class.getName());

    @Override
    public String toString() {
        ImmutableList<PngSpec> allPngs1 = getAllPngs1();
        return allPngs1.toString();
    }
}
