package c3i.core.imageModel.server;

import java.io.File;
import java.io.FileFilter;

public class FileFilters {

    public static boolean common(File f) {
        String n = f.getName();
        return !f.isHidden() && !n.startsWith("") && f.canRead();
    }

    public static boolean dir(File f) {
        return f.isDirectory();
    }

    public static boolean file(File f) {
        return !f.isDirectory();
    }

    public static boolean png(File f) {
        String n = f.getName();
        return common(f) && file(f) && n.split("_").length == 3 && n.endsWith(".png");
    }

//    public static boolean isEmptyPng(File f) {
//        if (!png(f)) return false;
//        if (f.length() > 5000) return false;
//        return ImageUtil.isEmpty(f);
//    }

    public static final FileFilter VIEW_DIR_FILTER = new FileFilter() {
        public boolean accept(File f) {
            return common(f) && dir(f);
        }
    };

    public static final FileFilter LAYER_DIR_FILTER = new FileFilter() {
        public boolean accept(File f) {
            String n = f.getName();
            return common(f) && dir(f) && n.split("_").length >= 2 && !n.endsWith("_Skip");
        }
    };

    public static final FileFilter PNG_FILE_FILTER = new FileFilter() {
        public boolean accept(File f) {
            return png(f);
        }
    };

//    public static final FileFilter EMPTY_PNG_FILTER = new FileFilter() {
//        @Override public boolean accept(File f) {
//            return isEmptyPng(f);
//        }
//    };

}
