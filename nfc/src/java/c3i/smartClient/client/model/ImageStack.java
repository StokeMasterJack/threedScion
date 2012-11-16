package c3i.smartClient.client.model;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.CompleterImpl;
import c3i.util.shared.futures.Future;
import smartsoft.util.shared.Path;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.AngleKey;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImImage;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.imageModel.shared.SimplePicks;
import c3i.core.imageModel.shared.ViewKey;
import c3i.util.shared.futures.HasKey;
import c3i.util.shared.futures.OnComplete;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a configured 3D image for one value of (picks + view + angle). <br/>
 * <br/>
 * This may contain:<br/>
 *      <br/>
 *      a single flattened image (jpg or png) <br/>
 *      one png for each layer (png mode) <br/>
 *      or a flattened base image + layer pngs for accessories   <br/>
 *
 */
public class ImageStack implements Exportable, HasKey {

    private final Key key;
    private final CoreImageStack coreImageStack;
    private final LayerState m;

    private final Completer<ImageStack> loader = new CompleterImpl<ImageStack>();
    private int completeCount;

    private final ImmutableList<Img> images;

    private ImageStack(){
        throw new UnsupportedOperationException("needed (but not called) by gwt-exporter");
    }

    public ImageStack(final Key key, CoreImageStack coreImageStack) {
          this(key, coreImageStack,null);
    }

    public ImageStack(final Key key, CoreImageStack coreImageStack, LayerState m) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(coreImageStack);
        this.key = key;
        this.coreImageStack = coreImageStack;
        this.m = m;

        ImmutableList.Builder<Img> builder = ImmutableList.builder();

        ImmutableList<? extends ImImage> images = coreImageStack.getImages();
        for (int i = 0; i < images.size(); i++) {
            ImImage imImage = images.get(i);
            final Img image = new Img(key.getRepoBase(), imImage, m);
            image.ensureLoaded().complete(new OnComplete() {
                @Override
                public void call() {
                    if (allImagesComplete()) {
                        throw new IllegalStateException();
                    }
                    completeCount++;
                    if (allImagesComplete()) {
                        loader.setResult(ImageStack.this);
                    }
                }
            });


            builder.add(image);
        }
        this.images = builder.build();


    }

    @Export
    public ImageStackFuture ensureLoaded() {
        return new ImageStackFuture(loader.getFuture());
    }

    @Override
    @Nonnull
    public Key getKey() {
        return key;
    }

    public int getImageCount() {
        return coreImageStack.getImageCount();
    }

    public ImmutableList<Img> getImages() {
        return images;
    }

    @Export
    public Img[] getImageArray() {
        Img[] a = new Img[images.size()];
        for (int i = 0; i < images.size(); i++) {
            Img image = images.get(i);
            a[i] = image;
        }
        assert a.length > 0;
        return a;
    }

    public boolean allImagesComplete() {
        return completeCount == coreImageStack.getImageCount();
    }

    @Export
    @Override
    public String toString() {
        return "ImageBatch" + images.toString();
    }

    public boolean isBaseImageFailed() {
        return images.get(0).isFailed();
    }

    public boolean isFatal() {
        return isBaseImageFailed();
    }

    public int getFailureCount() {
        int c = 0;
        for (Img image : images) {
            boolean failed = image.isFailed();
            if (failed) {
                c++;
            }
        }
        return c;
    }

    public int getSuccessCount() {
        return images.size() - getFailureCount();
    }

    public List<Path> getFailedUrls() {
        ArrayList<Path> a = new ArrayList<Path>();
        for (Img imageElement : images) {
            boolean failed = imageElement.isFailed();
            if (failed) {
                a.add(imageElement.getUrl());
            }
        }
        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageStack that = (ImageStack) o;

        if (!coreImageStack.equals(that.coreImageStack)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return coreImageStack.hashCode();
    }

    public Img get(int i) throws IndexOutOfBoundsException {
        return images.get(i);
    }

    public boolean contains(Path imageUrl) {
        for (Img image : images) {
            if (image.getUrl().equals(imageUrl)) {
                return true;
            }
        }
        return false;
    }

    public ViewKey getViewKey() {
        return coreImageStack.getViewKey();
    }


    public ImageMode getImageMode() {
        return key.getImageMode();
    }

    public FixedPicks getFixedPicks() {
        return coreImageStack.getFixedPicks();
    }

    public static class Key {

        private final Path repoBase;
        private final CoreImageStack.Key coreKey;

        public Key(Path repoBase, CoreImageStack.Key coreKey) {
            this.repoBase = repoBase;
            this.coreKey = coreKey;
        }

        public Key(Path repoBaseUrl, AngleKey angleKey, FixedPicks fixedPicks, Profile profile, ImageMode imageMode) {
            this.repoBase = repoBaseUrl;
            RawImageStack.Key rawSpec = new RawImageStack.Key(angleKey, fixedPicks);
            this.coreKey = new CoreImageStack.Key(rawSpec, profile, imageMode);
        }

        public CoreImageStack.Key getCoreKey() {
            return coreKey;
        }

        public Path getRepoBase() {
            return repoBase;
        }

        public AngleKey getAngleKey() {
            return coreKey.getAngleKey();
        }


        public ViewKey getViewKey() {
            return coreKey.getViewKey();
        }

        public Profile getProfile() {
            return coreKey.getProfile();
        }

        public ImageMode getImageMode() {
            return coreKey.getImageMode();
        }

        public SimplePicks getFixedPicks() {
            return coreKey.getFixedPicks();
        }


        public int getViewIndex() {
            return coreKey.getViewIndex();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key that = (Key) o;
            return coreKey.equals(that.coreKey) && repoBase.equals(that.repoBase);

        }

        @Override
        public int hashCode() {
            int result = repoBase.hashCode();
            result = 31 * result + coreKey.hashCode();
            return result;
        }
    }




}
