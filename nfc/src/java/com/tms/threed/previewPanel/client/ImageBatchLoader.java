package com.tms.threed.previewPanel.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.Image;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class ImageBatchLoader {

    private final ImmutableList<Path> urls;
    private final ImmutableList<ImageLoader> imageLoaders;

    private ArrayList<Path> completeSuccess = new ArrayList<Path>();
    private ArrayList<Path> completeError = new ArrayList<Path>();

    public ImageBatchLoader(ImmutableList<Path> urls, ImageSize imageSize, final BatchLoadListener batchLoadListener) {
        assert urls != null;
        assert assertAllNonNull(urls);

        this.urls = urls;

        ImmutableList.Builder<ImageLoader> builder = ImmutableList.builder();

        Console.log("Fetching jpgs: ");
        for (Path url : urls) {
            Console.log("\t" + url);
        }

        for (int i = 0; i < urls.size(); i++) {
            Path url = urls.get(i);
            builder.add(new ImageLoader(i, url, imageSize, new ImageLoader.CompleteCallback() {
                @Override
                public void call(ImageLoader.FinalOutcome finalOutcome) {
                    if (finalOutcome.isCompleteError()) {
                        completeError.add(finalOutcome.getUrl());
                    } else if (finalOutcome.isCompleteSuccess()) {
                        completeSuccess.add(finalOutcome.getUrl());
                    } else {
                        throw new IllegalStateException();
                    }

                    if (finalOutcome.isBase()) {
                        batchLoadListener.onFirstImageComplete(finalOutcome);
                    }

                    if (isAllComplete()) {
                        batchLoadListener.onAllImagesComplete();
                    }
                }
            }));
        }

        imageLoaders = builder.build();


    }


    public void showAll() {
        for (ImageLoader imageLoader : imageLoaders) {
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

    private boolean isAllComplete() {
        return getCompleteCount() == getImageCount();
    }

    private int getImageCount() {
        return urls.size();
    }

    private boolean allImagesHadError() {
        return completeError.size() == getImageCount();
    }

    private ImageLoader getBaseImage() {
        if (imageLoaders != null && imageLoaders.size() > 0) {
            return imageLoaders.get(0);
        } else {
            return null;
        }
    }

    private boolean baseImageHadError() {
        ImageLoader baseImage = getBaseImage();
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

    public boolean isActive(Image image) {
        String url = image.getUrl();
        for (ImageLoader loader : imageLoaders) {
            if (loader.matches(image)) {
                return true;
            }
        }
        for (Path path : urls) {
            if (path.toString().equals(url)) {
                return true;
            }
        }
        return false;
    }

    public ImmutableList<ImageLoader> getLoaders() {
        return imageLoaders;
    }


    public static interface BatchLoadListener {
        void onAllImagesComplete();

        void onFirstImageComplete(ImageLoader.FinalOutcome finalOutcome);
    }


}
