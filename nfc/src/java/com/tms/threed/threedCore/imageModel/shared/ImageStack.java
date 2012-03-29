package com.tms.threed.threedCore.imageModel.shared;

import com.google.common.collect.ImmutableList;
import com.tms.threed.repo.shared.JpgKey;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import smartsoft.util.lang.shared.Path;

/**
 * This class represents a stack of images such that:
 * <p/>
 * 1.  Each image in the stack has the same x,y,width,height but different zIndex
 * 2.  The bottom image is always a JPG all others are PNGs
 */
public class ImageStack {

    private final ImView imView;
    private final ImmutableList<ImPng> pngs;

    public ImageStack(ImView imView, ImmutableList<ImPng> pngs) {
        this.imView = imView;
        this.pngs = pngs;
    }

    public ImmutableList<Path> getUrlsJpgMode(JpgWidth jpgWidth) {
        return getUrlsJpgMode(jpgWidth, true);
    }

    public ImJpg getJpg(JpgWidth jpgWidth) {
        ImmutableList.Builder<ImPng> jpgPngs = ImmutableList.builder();

        for (ImPng png : pngs) {
            if (png.isPartOfJpg()) {
                jpgPngs.add(png);
            }
        }

        return new ImJpg(imView, pngs, jpgWidth);
    }

    public String getJpgFingerprint() {
        return imView.getJpgFingerprint(pngs);
    }

    public ImmutableList<Path> getUrlsJpgMode(JpgWidth jpgWidth, boolean includeZPngs) {
        ImmutableList.Builder<ImPng> jpgPngs = ImmutableList.builder();
        ImmutableList.Builder<Path> urls = ImmutableList.builder();

        for (ImPng png : pngs) {
            if (png.isPartOfJpg()) {
                jpgPngs.add(png);
            }
        }

        ImmutableList<ImPng> jpgPngList = jpgPngs.build();
        Path jpgUrl = imView.getJpgUrl(jpgPngList, jpgWidth);

        urls.add(jpgUrl);

        if (includeZPngs) {
            for (ImPng png : pngs) {
                if (png.isZLayer()) {
                    Path pngUrl = png.getUrl();
                    urls.add(pngUrl);
                }
            }
        }

        return urls.build();
    }

    public ImmutableList<Path> getUrlsPngMode(JpgWidth jpgWidth) {
        ImmutableList.Builder<Path> urls = ImmutableList.builder();
        for (ImPng png : pngs) {
            urls.add(png.getUrl());
        }
        return urls.build();
    }

    public ImmutableList<ImPng> getBlinkPngs() {
        ImmutableList.Builder<ImPng> blinkPngs = ImmutableList.builder();
        for (ImPng png : pngs) {
            blinkPngs.add(png);
        }
        return blinkPngs.build();
    }

    public ImmutableList<ImPng> getPngs() {
        return pngs;
    }

    public void print() {
        System.out.println("ImageStack:");
        System.out.println("\t pngs: ");
        for (ImPng png : pngs) {
            System.out.println("\t\t" + png);
        }
        System.out.println();
    }

    public JpgKey getJpgKey(JpgWidth jpgWidth) {
        String jpgFingerprint = getJpgFingerprint();
        SeriesKey seriesKey = imView.getSeries().getSeriesKey();
        return new JpgKey(seriesKey, jpgWidth, jpgFingerprint);
    }
}
