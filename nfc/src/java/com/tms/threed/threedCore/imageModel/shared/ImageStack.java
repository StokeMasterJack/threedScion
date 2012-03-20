package com.tms.threed.threedCore.imageModel.shared;

import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a stack of images such that:
 * <p/>
 * 1.  Each image in the stack has the same x,y,width,height but different zIndex
 * 2.  The bottom image is always a JPG all others are PNGs
 */
public class ImageStack implements IImageStack {

    private final ImView view;
    private final List<ImPng> basePngs;
    private final List<ImPng> zPngs;

    private transient final List<IPng> allPngs;
    private transient final List<ImPng> allPngs2;
    private transient final List<ImPng> blinkPngs;
    private transient final ImJpg jpg;
    private transient final ImJpg fullJpg;


    public ImageStack(List<ImPng> basePngs, List<ImPng> zPngs, ImView view, int angle, JpgWidth jpgWidth) {
        this.basePngs = basePngs;
        this.zPngs = zPngs;
        this.view = view;
        this.allPngs = initAllPngs();
        this.allPngs2 = initAllPngs2();
        this.blinkPngs = initBlinkPngs();
        this.jpg = new ImJpg(view, basePngs, angle, jpgWidth);

        this.fullJpg = new ImJpg(view, allPngs2, angle, jpgWidth);
    }

    private List<IPng> initAllPngs() {
        ArrayList<IPng> a = new ArrayList<IPng>(basePngs);
        a.addAll(zPngs);
        return a;
    }

    private List<ImPng> initAllPngs2() {
        ArrayList<ImPng> a = new ArrayList<ImPng>(basePngs);
        a.addAll(zPngs);
        return a;
    }

    private List<ImPng> initBlinkPngs() {
//        System.out.println("ImageStack.initBlinkPngList");
        ArrayList a = new ArrayList();
        for (IPng png : allPngs) {
            if (png.isBlink()) {
//                System.out.println("IsBlink: " + png.toString());
                a.add(png);
            } else {
//                System.out.println("NOT Blink: " + png.toString());
            }
        }
        return a;
    }

    public ImView getView() {
        return view;
    }

    public List<ImPng> getBasePngs() {
        return basePngs;
    }

    public List<ImPng> getZPngs() {
        return zPngs;
    }

    public List<IPng> getAllPngs() {
        return allPngs;
    }

    public List<ImPng> getBlinkPngs() {
        return blinkPngs;
    }


    public ImJpg getJpg() {
        return jpg;
    }

    public ImJpg getFullJpg() {
        return fullJpg;
    }

    @Override
    public Path getImageBase() {
        return view.getSeries().getThreedBaseUrl();
    }

    @Override
    public Path getJpgUrl() {
        return jpg.getUrl();
    }

    @Override
    public List<Path> getUrlsJpgMode(boolean includeZPngs) {

        ArrayList<Path> list = new ArrayList<Path>();
        list.add(jpg.getUrl());

        if (includeZPngs) {
            for (int i = 0; i < zPngs.size(); i++) {
                ImPng png = zPngs.get(i);
                list.add(png.getUrl());
            }
        }

        return list;
    }

    @Override
    public List<Path> getUrlsJpgMode() {
        return getUrlsJpgMode(true);
    }

    @Override
    public List<Path> getUrlsPngMode() {
        ArrayList<Path> list = new ArrayList<Path>();

        List<ImPng> jPngs = jpg.getPngs();
        for (ImPng png : jPngs) {
            list.add(png.getUrl());
        }

        for (ImPng zPng : zPngs) {
            list.add(zPng.getUrl());
        }

        return list;
    }

//    public List<Path> getUrls(boolean pngMode) {
//        if (pngMode) return getUrlsPngMode();
//        else return getUrlsJpgMode();
//    }

    public void print() {
        System.out.println("ImageStack:");
        System.out.println("\t jpg:");
        System.out.println("\t\t" + jpg);
        System.out.println("\t pngs: ");
        for (ImPng png : zPngs) {
            System.out.println("\t\t" + png);
        }
        System.out.println();
    }

    public void purgeZLayers() {
        zPngs.clear();
    }


}
