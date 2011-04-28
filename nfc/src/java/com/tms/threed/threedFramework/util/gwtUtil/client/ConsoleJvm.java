package com.tms.threed.threedFramework.util.gwtUtil.client;

import java.util.HashMap;

public class ConsoleJvm {

    private static final HashMap<String, Long> times = new HashMap<String, Long>();

    public static void log(String msg) {
        System.out.println(msg);
    }

    public static void time(String msg) {
        times.put(msg, System.currentTimeMillis());
    }

    public static void timeEnd(String msg) {
        Long t1 = times.get(msg);
        if (t1 == null) System.out.println(msg + ": No start time");
        else System.out.println(msg + ": " + (System.currentTimeMillis() - t1));
    }


}
