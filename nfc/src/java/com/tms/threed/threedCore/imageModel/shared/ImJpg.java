package com.tms.threed.threedCore.imageModel.shared;

import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.Angle;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class ImJpg {

    private final ImView view;
    private final List<ImPng> pngs;
    private final int angle;

    private Path localPath;

    private final JpgFingerprint fingerprint;
    private final JpgWidth jpgWidth;

    public ImJpg(@Nonnull ImView view, @Nonnull List<ImPng> pngs, int angle,JpgWidth jpgWidth) {
        assert view != null;
        assert view != pngs;
        assert pngs.size() != 0;

        this.view = view;
        this.pngs = pngs;
        this.angle = angle;
        this.jpgWidth = jpgWidth;

        //cache
        this.fingerprint = new JpgFingerprint(pngs);
//        this.featureList = initFeatureList();
        this.localPath = initLocalPath();


    }

    public String  getPath() {
        return null; //place holder
    }


    /**
     * @return Ex: vr_1_02.jpg
     */
    public Path getLocalPath() {
        return localPath;
    }

    private Path initLocalPath() {
        return new Path("vr_1_" + Angle.getAnglePadded(angle) + ".jpg");
    }


    public ImView getView() {
        return view;
    }

    public int getAngle() {
        return angle;
    }

    public List<ImPng> getPngs() {
        return pngs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImJpg that = (ImJpg) o;
        return this.toString().equals(that.toString());

    }

    private transient int hash = -1;

    @Override
    public int hashCode() {
        if (hash == -1) {
            this.hash = getFingerprint().hashCode();
        }
        return hash;
    }


    public String getFingerprint() {
        return fingerprint.stringValue();
    }

    private Path getJpgUrlBase() {
        ImSeries series = getView().getSeries();
        return series.getThreedBaseJpgUrl(jpgWidth);
    }

    public Path getUrl() {
        String fingerprint = getFingerprint();
        Path jpgUrlBase = getJpgUrlBase();
        return jpgUrlBase.append(fingerprint).appendName(".jpg");
    }



    public static class JpgFingerprint {

        public static final String GUID_SEPARATOR = "-";

        private final String value;

        public JpgFingerprint(List<ImPng> pngs) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < pngs.size(); i++) {
                ImPng png = pngs.get(i);
                String shortSha = png.getShortSha();
                sb.append(shortSha);
                boolean last = (i == pngs.size() - 1);
                if (!last) sb.append(GUID_SEPARATOR);
            }
            this.value = sb.toString();
        }

        public String stringValue() {
            return value;
        }

        @Override
        public String toString() {
            return stringValue();
        }
    }

}