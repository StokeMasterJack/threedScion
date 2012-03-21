package com.tms.threed.threedCore.imageModel.shared;

import com.google.common.collect.ImmutableList;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import smartsoft.util.lang.shared.Path;

public class ImJpg {

    public static final String FINGERPRINT_SEPARATOR = "-";

    private final Path url;
    private final String fingerprint;
    private final int hash;

    public ImJpg(ImView view, ImmutableList<ImPng> pngs, JpgWidth jpgWidth) {
        assert view != null;
        assert pngs != null;
        assert pngs.size() != 0;

        this.fingerprint = getFingerprint(pngs);
        this.hash = fingerprint.hashCode();
        this.url = view.getSeries().getThreedBaseJpgUrl(jpgWidth).append(fingerprint).appendName(".jpg");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImJpg that = (ImJpg) o;
        return fingerprint.equals(that.fingerprint);

    }

    @Override
    public int hashCode() {
        return hash;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public Path getUrl() {
        return url;
    }

    public static String getFingerprint(ImmutableList<ImPng> pngs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pngs.size(); i++) {
            ImPng png = pngs.get(i);
            String shortSha = png.getShortSha();
            sb.append(shortSha);
            boolean last = (i == pngs.size() - 1);
            if (!last) sb.append(FINGERPRINT_SEPARATOR);
        }
        return sb.toString();
    }



}