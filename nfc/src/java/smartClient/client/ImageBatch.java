package smartClient.client;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageBatch implements Exportable {

    private final ImmutableList<Path> urls;

    private final Loader<ImageBatch> loader = new Loader<ImageBatch>("ImageBatchLoader");
    private int completeCount;

    private final ImmutableList<Image> images;

    public ImageBatch(final ImmutableList<Path> urls) {
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
                    Console.log("complete: " + image);
                    if (isComplete()) {
                        throw new IllegalStateException();
                    }
                    completeCount++;
                    if (isComplete()) {
                        loader.setResult(ImageBatch.this);
                    }
                }
            });


            builder.add(image);
        }
        this.images = builder.build();
    }

    public Future<ImageBatch> getLoadFuture() {
        return loader.ensureLoaded();
    }


    private int getImageCount() {
        return urls.size();
    }

    public boolean containsUrl(Path imageUrl) {
        Preconditions.checkNotNull(imageUrl);
        return urls.contains(imageUrl);
    }

    public ImmutableList<Image> getImages() {
        return images;
    }

    private Image[] getImageArray1() {
        Image[] a = new Image[images.size()];

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            a[i] = image;
            Console.log("internal image[" + i + "] = " + image);
        }
        assert a.length > 0;

        Console.log("internal a = " + a);
        return a;
    }

    @Export
    public Image[] getImageArray() {
        Image[] a = new Image[images.size()];
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            a[i] = image;
            Console.log("internal image[" + i + "] = " + image);
        }
        assert a.length > 0;
        Console.log("internal a = " + Arrays.toString(a));
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


}
