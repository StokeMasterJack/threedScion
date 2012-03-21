package com.tms.threed.repo.server;

import java.io.File;

public class TwoThirty8 {

    private final String two;
    private final String thirty8;

    public TwoThirty8(String two, String thirty8) {
        this.two = two;
        this.thirty8 = thirty8;
    }

    public String getTwo() {
        return two;
    }

    public String getThirty8() {
        return thirty8;
    }

    public File getFileName(File prefix) {
        File twoFile = new File(prefix, two);
        return new File(twoFile, thirty8 + ".jpg");
    }

    @Override public String toString() {
        return "TwoThirty8{" +
                "two='" + two + '\'' +
                ", thirty8='" + thirty8 + '\'' +
                '}';
    }
}
