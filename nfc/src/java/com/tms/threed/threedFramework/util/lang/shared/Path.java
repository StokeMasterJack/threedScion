package com.tms.threed.threedFramework.util.lang.shared;

import java.io.Serializable;

/**
 * Represents a path that may be used on multiple platforms. i.e. file systems (Mac, Windows, Unix) and web URLs
 */
public class Path implements Comparable<Path>, Serializable {

    //will never have leading or trailing slashes
    private String pathString;

    public Path() {
        assert isValid();
    }

    public Path(String u) {
        pathString = fixUp(u);
        assert isValid();
    }

    public Path(Path context, Path localPath) {
        this(context == null ? null : context.pathString, localPath == null ? null : localPath.pathString);
        assert isValid();
    }

    public Path(Path context, String pathString) {
        this(context == null ? null : context.pathString, pathString);
        assert isValid();
    }

    public Path(String context, String localPath) {
        String u1 = fixUp(context);
        String u2 = fixUp(localPath);

        if (u1 == null && u2 == null) {
            pathString = null;
        } else if (u1 != null && u2 == null) {
            pathString = u1;
        } else if (u1 == null && u2 != null) {
            pathString = u2;
        } else if (u1 != null && u2 != null) {
            pathString = u1 + "/" + u2;
        } else {
            throw new IllegalStateException();
        }

        assert isValid();
    }


    private void fixUp() {
        this.pathString = fixUp(this.pathString);
    }

    private void check() {

    }

    private static void check(String u) {
        if (u == null) return;
        if (Strings.isEmpty(u)) throw new IllegalStateException("Failed isEmpty test for path: [" + u + "]");
        if (u.contains(" ")) throw new IllegalStateException("Failed containsSpace test for path: [" + u + "]");
        if (u.contains("\\")) throw new IllegalStateException("Failed containsBckSlash test for path: [" + u + "]");
        if (u.startsWith("//"))
            throw new IllegalStateException("Failed startsWithDoubleFwdSlash test for path: [" + u + "]");
    }

    private boolean isValid() {
        try {
            check();
            return true;
        } catch (IllegalStateException e) {
//            return false;
            throw e;
        }
    }

    public static String fixUp(String u) {
        if (Strings.isEmpty(u)) {
            return null;
        } else {
            u = " " + u + " ";
            u = u.trim();
            u = convertBackslashesToForward(u);
            u = removeWindowsDriveLetterPrefix(u);
            u = trimSlashes(u);
            check(u);
            return u;
        }
    }

    public static String removeWindowsDriveLetterPrefix(String u) {
        return u.replaceAll("c:", "");
    }

    public static String convertBackslashesToForward(String u) {
        return u.replaceAll("\\\\", "/");
    }

    public static boolean isUrl(String s) {
        if (s.startsWith("http:/")) return true;
        else if (s.startsWith("file:/")) return true;
        else return false;
    }

    public boolean getPathString() {
        return isUrl(pathString);
    }


    @Override
    public String toString() {
        assert isValid();
        if (pathString == null) return "";
        if (pathString.startsWith("http:")) return pathString;
        if (pathString.startsWith("file:")) return pathString;
        return "/" + pathString;
    }

    public String toStringNoLeadingSlash() {
        assert isValid();
        if (pathString == null) return "";
        if (pathString.startsWith("http:")) return pathString;
        if (pathString.startsWith("file:")) return pathString;
        return pathString;
    }

    public static String trimSlashes(String s) {
        String a = trimLeadingSlash(s);
        String b = trimTrailingSlash(a);
        return b;
    }

    public static String trimTrailingSlash(String s) {
        if (s.endsWith("/")) return s.substring(0, s.length() - 1);
        return s + "";
    }

    public static String trimLeadingSlash(String s) {
        if (s.startsWith("/")) return s.substring(1);
        return s + "";
    }

    public Path copy() {
        return new Path(pathString);
    }

    public Path append(Path url) {
        return new Path(this, url);
    }

    public Path prepend(Path url) {
        return new Path(url, this);
    }

    public Path append(String url) {
        return new Path(this, new Path(url));
    }

    public Path prepend(String url) {
        return new Path(new Path(url), this);
    }

    public Path appendName(String suffix) {
        String s = this.pathString + suffix;
        return new Path(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path that = (Path) o;

        assert this.isValid();
        assert that.isValid();
        if (!pathString.equals(that.pathString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pathString.hashCode();
    }

    public int compareTo(Path that) {
        if (that == null) throw new IllegalArgumentException("\"o\" is required");
        return this.pathString.compareTo(that.pathString);
    }

    public Path leftTrim(Path path) {
        String leftString = path.toString();
        String thisString = toString();
        return new Path(thisString.substring(leftString.length()));
    }

    public boolean endsWith(String s) {
        if (pathString == null) return false;
        return pathString.endsWith(s);
    }


    public Path dotDot() {
        if (pathString == null) return this;

        int i = pathString.lastIndexOf('/');
        if (i == -1) return this;

        String s = pathString.substring(0, i);
        return new Path(s);
    }

    public boolean isHttpUrl() {
        if (pathString == null) {
            return false;
        } else {
            return pathString.toLowerCase().startsWith("http");
        }
    }
}
