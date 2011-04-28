package com.tms.threed.threedFramework.imageModel.shared.slice;

import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.imageModel.shared.IPng;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

public class ImgStack implements IImageStack {

    private final Jpg jpg;
    private final List<Png> zPngs;
    private final Path imageBase;

    public ImgStack(Jpg jpg, List<Png> zPngs, Path imageBase) {
        this.jpg = jpg;
        this.zPngs = zPngs;
        this.imageBase = imageBase;
    }

    public Jpg getJpg() {
        return jpg;
    }

    public List<Png> getZPngs() {
        return zPngs;
    }

    @Override
    public Path getImageBase() {
        return imageBase;
    }

    @Override public Path getJpgUrl() {
        return jpg.getUrl(imageBase);
    }

    public List<Path> getUrlsJpgMode(boolean includeZPngs) {
        System.out.println("ImgStack.getUrlsJpgMode");
        ArrayList<Path> list = new ArrayList<Path>();
        list.add(jpg.getUrl(imageBase));

        if (includeZPngs) {
            for (int i = 0; i < zPngs.size(); i++) {
                Png png = zPngs.get(i);
                list.add(png.getUrl(imageBase));
            }
        }

        return list;
    }

    @Override public List<Path> getUrlsJpgMode() {
        return getUrlsJpgMode(true);
    }

    public List<Path> getUrlsPngMode() {
        ArrayList<Path> list = new ArrayList<Path>();

        List<Png> jPngs = jpg.getPngs();
        for (Png png : jPngs) {
            list.add(png.getUrl(imageBase));
        }

        for (Png zPng : zPngs) {
            list.add(zPng.getUrl(imageBase));
        }

        return list;
    }
//
//    public List<Path> getUrls(boolean pngMode) {
//        if (pngMode) return getUrlsPngMode();
//        else return getUrlsJpgMode();
//    }

    @Override
    public List<IPng> getAllPngs() {
        ArrayList<IPng> a = new ArrayList<IPng>();
        a.addAll(jpg.getPngs());
        a.addAll(zPngs);
        return a;
    }
}
