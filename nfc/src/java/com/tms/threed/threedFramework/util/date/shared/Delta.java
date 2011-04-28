package com.tms.threed.threedFramework.util.date.shared;

public class Delta {

    private long t1;
    private long t2;

    public void start() {
//        assert t1 == 0;
//        assert t2 == 0;
        t1 = System.currentTimeMillis();
    }

    public void end() {
//        assert t1 != 0;
//        assert t2 == 0;
        t2 = System.currentTimeMillis();
    }

    public long getDelta() {

//        assert t1 != 0;
//        assert t2 != 0;

        return t2 - t1;
    }

}
