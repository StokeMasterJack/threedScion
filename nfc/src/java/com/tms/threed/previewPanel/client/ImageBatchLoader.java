package com.tms.threed.previewPanel.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class ImageBatchLoader {

    private final ImmutableList<Path> urls;
    private final ImmutableList<ImageLoaderOld> imageLoaders;

    private ArrayList<Path> completeSuccess = new ArrayList<Path>();
    private ArrayList<Path> completeError = new ArrayList<Path>();

    public ImageBatchLoader(ImmutableList<Path> urls, ImageSize imageSize, final BatchLoadListener batchLoadListener) {
        assert urls != null;
        assert assertAllNonNull(urls);

        this.urls = urls;

        ImmutableList.Builder<ImageLoaderOld> builder = ImmutableList.builder();

        Console.log("Fetching jpgs: ");
        for (Path url : urls) {
            Console.log("\t" + url);
        }

        for (int i = 0; i < urls.size(); i++) {
            Path url = urls.get(i);
            builder.add(new ImageLoaderOld(i, url, imageSize, new ImageLoaderOld.CompleteCallback() {
                @Override
                public void call(ImageLoaderOld.FinalOutcome finalOutcome) {
                    if (finalOutcome.isCompleteError()) {
                        completeError.add(finalOutcome.getUrl());
                    } else if (finalOutcome.isCompleteSuccess()) {
                        completeSuccess.add(finalOutcome.getUrl());
                    } else {
                        throw new IllegalStateException();
                    }

                    if (finalOutcome.isBase()) {
                        Console.log("firstComplete");
                        if (batchLoadListener != null) {
                            batchLoadListener.onFirstImageComplete(finalOutcome);
                        }
                    }

                    if (isComplete()) {
                        Console.log("AllComplete");
                        if (batchLoadListener != null) {
                            batchLoadListener.onAllImagesComplete();
                        }
                    }
                }
            }));
        }

        imageLoaders = builder.build();


    }


    public void showAll() {
        for (ImageLoaderOld imageLoader : imageLoaders) {
            imageLoader.setVisible();
        }
    }

    private static boolean assertAllNonNull(List<Path> urls) {
        for (Path url : urls) {
            if (url == null) return false;
        }
        return true;
    }


    private int getLoadedCount() {
        return completeSuccess.size();
    }

    private int getErrorCount() {
        return completeError.size();
    }


    private int getCompleteCount() {
        return getLoadedCount() + getErrorCount();
    }

    private boolean isComplete() {
        return getCompleteCount() == getImageCount();
    }

    private int getImageCount() {
        return urls.size();
    }

    private boolean allImagesHadError() {
        return completeError.size() == getImageCount();
    }

    private ImageLoaderOld getBaseImage() {
        if (imageLoaders != null && imageLoaders.size() > 0) {
            return imageLoaders.get(0);
        } else {
            return null;
        }
    }

    private boolean baseImageHadError() {
        ImageLoaderOld baseImage = getBaseImage();
        if (baseImage == null) {
            return false;
        } else {
            return baseImage.isCompleteError();
        }
    }


    public boolean isFatal() {
        return allImagesHadError() || baseImageHadError();
    }

    public List<Path> getErrors() {
        return this.completeError;
    }

    public boolean containsUrl(Path imageUrl) {
        Preconditions.checkNotNull(imageUrl);
        return urls.contains(imageUrl);
    }

    public ImmutableList<ImageLoaderOld> getLoaders() {
        return imageLoaders;
    }


    public static interface BatchLoadListener {
        void onAllImagesComplete();

        void onFirstImageComplete(ImageLoaderOld.FinalOutcome finalOutcome);
    }


}
