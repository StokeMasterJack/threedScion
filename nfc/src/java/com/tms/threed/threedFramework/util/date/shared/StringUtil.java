package com.tms.threed.threedFramework.util.date.shared;

public class StringUtil {

    public static String unCamelizeName(String name) {
        if (isEmpty(name)) throw new IllegalArgumentException("name must not be null");
        StringBuffer newString = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                newString.append(" ");
            }
            newString.append(c);
        }
        String s = newString.toString();
        String s1 = capFirstLetter(s);
        return s1.trim();
    }

    public static String capFirstLetter(String s) {
        if (isEmpty(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toUpperCase() + rest;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static String nullNormalize(String s) {
        return isEmpty(s) ? null : s;
    }

    public static String getStackTraceAsString(Throwable t) {
        String nl = "\n";
        StringBuilder s = new StringBuilder();
        s.append(t);
        s.append(nl);
        StackTraceElement[] stackTraceElements = t.getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement ste = stackTraceElements[i];
            s.append("\tat ");
            s.append(ste);
            s.append(nl);
        }
        Throwable ourCause = t.getCause();
        if (ourCause != null) {
            s.append("Caused By: ");
            s.append(t.toString());
            s.append("\n");
            s.append(getStackTraceAsString(ourCause));
        }
        return s.toString();

    }


}
