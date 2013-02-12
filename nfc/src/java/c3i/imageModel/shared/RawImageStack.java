package c3i.imageModel.shared;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

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

    private ImmutableList<PngSpec> blinkPngs;

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

    public ImmutableList<PngSpec> getBlinkPngs() {
        if (blinkPngs == null) {
            ImmutableList.Builder<PngSpec> builder = ImmutableList.builder();
            for (PngSpec png : allPngs) {
                builder.add(png);
            }
            this.blinkPngs = builder.build();
        }
        return this.blinkPngs;
    }

    public ImmutableList<PngSpec> getAllPngs() {
        return allPngs;
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
        ImmutableList<PngSpec> allPngs1 = getAllPngs();
        return allPngs1.toString();
    }
}
