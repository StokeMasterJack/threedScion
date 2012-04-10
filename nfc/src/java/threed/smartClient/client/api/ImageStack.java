package threed.smartClient.client.api;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import threed.smartClient.client.util.futures.Future;
import threed.smartClient.client.util.futures.Loader;
import threed.smartClient.client.util.futures.OnComplete;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class ImageStack implements Exportable {

    private final ImmutableList<Path> urls;

    private final Loader<ImageStack> loader = new Loader<ImageStack>("ImageBatchLoader");
    private int completeCount;

    private final ImmutableList<Image> images;

    public ImageStack(final ImmutableList<Path> urls) {
        Preconditions.checkNotNull(urls);
        Preconditions.checkArgument(!urls.isEmpty());

        this.urls = urls;

        ImmutableList.Builder<Image> builder = ImmutableList.builder();
        for (int i = 0; i < urls.size(); i++) {
            final Image image = new Image(urls.get(i));
            ImageFuture imageFuture = image.ensureLoaded();
            imageFuture.complete(new OnComplete() {
                @Override
                public void call() {
                    if (isComplete()) {
                        throw new IllegalStateException();
                    }
                    completeCount++;
                    if (isComplete()) {
                        loader.setResult(ImageStack.this);
                    }
                }
            });


            builder.add(image);
        }
        this.images = builder.build();
    }

    public Future<ImageStack> ensureLoaded() {
        return loader.ensureLoaded();
    }


    public int getImageCount() {
        return urls.size();
    }

    public ImmutableList<Image> getImages() {
        return images;
    }

    @Export
    public Image[] getImageArray() {
        Image[] a = new Image[images.size()];
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            a[i] = image;
        }
        assert a.length > 0;
        return a;
    }

    public boolean isComplete() {
        return completeCount == urls.size();
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
        return !isBaseImageFailed();
    }

    public int getFailureCount() {
        int c = 0;
        for (Image image : images) {
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
        for (Image imageElement : images) {
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

        if (!urls.equals(that.urls)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return urls.hashCode();
    }

    public Image get(int i) throws IndexOutOfBoundsException {
        return images.get(i);
    }
}
