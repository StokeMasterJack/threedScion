package c3i.imageModel.server;


import smartsoft.util.shared.Strings;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Extracts the outVars for a given Slice.
 */
public class PngVars {

    public static Set<String> getPngVars(final File viewDir, final int angle) {

        class Helper {

            //computed
            private final String expectedLocalName;
            private final Set<File> pngFiles;
            private final Set<String> pngVars;

            private Helper() {
                expectedLocalName = initPngFileName(angle);
                pngFiles = new HashSet<File>();
                pngVars = new HashSet<String>();
                initPngFiles(viewDir);
                initFeatures();
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
                        pngVars.add(f.getName());
                        f = f.getParentFile();
                    }
                }
            }

            public Set<String> getPngVars() {
                return pngVars;
            }
        }


        return new Helper().getPngVars();

    }


    private static String initPngFileName(int angle) {
        return "vr_1" + "_" + Strings.lpad(angle + "", '0', 2) + ".png";
    }

    private static boolean isLayerDir(File node) {
        if (!node.isDirectory()) return false;
        String name = node.getName();
        return name.length() >= 3 && name.charAt(2) == '_';
    }

    private static boolean isZLayerDir(File node) {
        return isLayerDir(node) && node.getName().charAt(3) == 'z';
    }


}
