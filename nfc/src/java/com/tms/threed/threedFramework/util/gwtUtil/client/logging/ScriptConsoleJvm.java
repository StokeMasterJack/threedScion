package com.tms.threed.threedFramework.util.gwtUtil.client.logging;

import java.util.HashMap;

public class ScriptConsoleJvm extends ScriptConsole {

    private final HashMap<String, Long> times = new HashMap<String, Long>();

    @Override public void log(String msg) {
        System.out.println(msg);
    }

    @Override public void error(String msg) {
        System.err.println(msg);
    }

    @Override public void time(String msg) {
        times.put(msg, System.currentTimeMillis());
    }

    @Override public void timeEnd(String msg) {
        Long t1 = times.get(msg);
        if (t1 == null) System.out.println(msg + ": No start time");
        else System.out.println(msg + ": " + (System.currentTimeMillis() - t1));
    }


    @Override public void profile(String msg) {
    }

    @Override public void profileEnd(String msg) {
    }


}