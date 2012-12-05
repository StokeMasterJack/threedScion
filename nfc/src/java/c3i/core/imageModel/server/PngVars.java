package c3i.core.imageModel.server;


import smartsoft.util.shared.Strings;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PngVars {

    private final File viewDir;
    private final int angle;

    //computed
    private final String expectedLocalName;
    private final Set<File> pngFiles;
    private final Set<String> features;

    public PngVars(File viewDir, int angle) {
        this.viewDir = viewDir;
        this.angle = angle;

        expectedLocalName = initPngFileName(angle);
        pngFiles = new HashSet<File>();
        features = new HashSet<String>();
        initPngFiles(viewDir);
        initFeatures();
    }

    public File getViewDir() {
        return viewDir;
    }

    public int getAngle() {
        return angle;
    }

    public Set<String> getPngVars() {
        return features;
    }

    public Set<File> getPngFiles() {
        return pngFiles;
    }

    private void initPngFiles(File node) {
        if (node.isDirectory() && !isZLayerDir(node)) {
            File[] childNodes = node.listFiles();
            for (File childNode : childNodes) {
                initPngFiles(childNode);
            }
        } else if (node.isFile()) {
            if (expectedLocalName.equals(node.getName())) {
                pngFiles.add(node);
            }
        }
    }

    private void initFeatures() {
        for (File pngFile : pngFiles) {
            File f = pngFile.getParentFile();
            while (!isLayerDir(f)) {
                features.add(f.getName());
                f = f.getParentFile();
            }
        }

    }


    private static String initPngFileName(int angle) {
        return new StringBuilder(11).append("vr_1").append("_").append(Strings.lpad(angle + "", '0', 2)).append(".png").toString();
    }

    private static boolean isLayerDir(File node) {
        if (!node.isDirectory()) return false;
        String name = node.getName();
        return name.length() >= 3 && name.charAt(2) == '_';
    }

    private static boolean isZLayerDir(File node) {
        return isLayerDir(node) && node.getName().charAt(3) == 'z';
    }


    public static Set<String> getPngVars(File viewDir, int angle) {
        return new PngVars(viewDir, angle).getPngVars();
    }
}
