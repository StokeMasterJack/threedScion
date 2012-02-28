package com.tms.threed.threedFramework.util.servlet;

import com.tms.threed.threedFramework.util.lang.shared.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionRenderer {

    public static boolean isDebug(HttpServletRequest request) {
        String sn = request.getServerName();
        return sn.contains("dev") || sn.contains("staging") || sn.contains("origin") || sn.contains("localhost");
    }

    public static String render(Throwable e) {
        if (e == null) return "";
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        e.printStackTrace(pw);
        pw.flush();
        return stringWriter.toString();
    }

    /**
     * Automatically escapes html special characters
     */
    public static String renderAsHtmlComment(Throwable exceptionObject) {
        try {

            StringWriter stringWriter = new StringWriter();
            PrintWriter out = new PrintWriter(stringWriter);

            out.println();
            out.println("<!--");
            out.println("********************************************");
            out.println("*****        Exception Report        ****** ");
            out.println("********************************************");
            if (exceptionObject == null) return "Nothing to render. Exception object is null.";
            out.println(Strings.escapeXml(render(exceptionObject)));
            out.println("********************************************");
            out.println("-->");

            out.flush();

            return stringWriter.toString();
        }
        catch (Throwable ee){
            return "<!-- error in the error handler[" + ee + "]";
        }
    }

    public static void renderException(HttpServletResponse response, Throwable e) {
        renderException(response, e, "No title");
    }

    public static void renderException(HttpServletResponse response, Throwable e, String title) {
        response.reset();
        response.setStatus(500);
        response.setContentType("text/plain");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        }
        catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        out.println("ERROR PROCESSING REQUEST");
        out.println("=============================");
        out.println("***  " + title + "  ***");
        out.println("=============================");
        if (e != null)
            e.printStackTrace(out);
    }

    public static void renderException(PrintWriter out, Throwable e, String title) {
        out.println("ERROR PROCESSING REQUEST");
        out.println("=============================");
        out.println("***  " + title + "  ***");
        out.println("=============================");
        if (e != null) e.printStackTrace(out);
    }

    public static void renderException(PrintWriter out, Throwable e) {
        renderException(out, e, "Smart Soft");
    }


}

