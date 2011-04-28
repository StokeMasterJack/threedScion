package com.tms.threed.threedFramework.imageModel.shared.slice;

import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.ArrayList;

public class Jpg {

    private final ArrayList<Png> pngs;
    private final StringBuffer fingerprintBuilder;
    private String fingerprint;

    public Jpg(int jpgLayerCount) {
        pngs = new ArrayList<Png>(jpgLayerCount);
        fingerprintBuilder = new StringBuffer();
    }

    /**
     * @param png non-z png
     */
    public void add(Png png) {

        assert fingerprint == null;
        assert !png.getLayer().isZLayer();

        pngs.add(png);
        fingerprintBuilder.append('-');

        String shortSha = png.getShortSha();


        fingerprintBuilder.append(shortSha);

    }

    public String getFingerprint() {
        if (fingerprint == null) {
            fingerprint = fingerprintBuilder.toString();
            if (fingerprint.startsWith("-")) {
                fingerprint = fingerprint.substring(1);
            }
        }
        return fingerprint;
    }

    public ArrayList<Png> getPngs() {
        return pngs;
    }

    public Path getUrl(Path imageBase) {
        return imageBase.append("jpgs").append(getFingerprint() + ".jpg");
    }

    @Override
    public String toString() {
        return getFingerprint();
    }
}
