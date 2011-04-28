package com.tms.threed.threedFramework.util.gwtUtil.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsole;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleGecko;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleIE;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleJvm;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleNull;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleOpera;
import com.tms.threed.threedFramework.util.gwtUtil.client.logging.ScriptConsoleWebkit;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

public class Console {

    public static final String DEBUG_REQUEST_PARAMETER = "debug";

    private final boolean debug;

    private final ScriptConsole scriptConsole;

    private static Console INSTANCE;

    private Console() {
        debug = initDebug();
        scriptConsole = initScriptConsole();
    }

    public static Console getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Console();
        }
        return INSTANCE;
    }

    private ScriptConsole initScriptConsole() {
        if (GWT.isScript()) {
            if (debug) {
                if (Browser.isIe()) {
                    return new ScriptConsoleIE();
                } else if (Browser.isFirefox()) {
                    return new ScriptConsoleGecko();
                } else if (Browser.isWebkit()) {
                    return new ScriptConsoleWebkit();
                } else if (Browser.isOpera()) {
                    return new ScriptConsoleOpera();
                } else {
                    return new ScriptConsoleNull();
                }
            } else {
                return new ScriptConsoleNull();
            }
        } else {
            return new ScriptConsoleJvm();
        }
    }

    public static boolean isDebug() {
        return getInstance().debug;
    }

    private boolean initDebug() {
        String sDebug = Window.Location.getParameter(DEBUG_REQUEST_PARAMETER);
        return Strings.notEmpty(sDebug);
    }


    public static void log(String msg) {
        getInstance().scriptConsole.log(msg);
    }

    public static void error(String msg) {
        getInstance().scriptConsole.error(msg);
    }

    public static void time(String msg) {
        getInstance().scriptConsole.time(msg);
    }

    public static void timeEnd(String msg) {
        getInstance().scriptConsole.timeEnd(msg);
    }

    public static void profile(String msg) {
        getInstance().scriptConsole.profile(msg);
    }

    public static void profileEnd(String msg) {
        getInstance().scriptConsole.profileEnd(msg);
    }


}
