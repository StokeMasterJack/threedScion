package com.tms.threed.threedFramework.util.lang.shared;

public class Strings {

    public static String getSubName(Class subclass, Class superclass) {
        final String n1 = Strings.getSimpleName(subclass);
        final String n2 = Strings.getSimpleName(superclass);
        final int i = n1.indexOf(n2);
        return n1.substring(0, i);
    }

    public static String getSimpleName(Object o) {
        if (o != null) return getSimpleName(o.getClass());
        return "null";
    }

    public static String getSimpleName(Class cls) {
        String[] a = cls.getName().split("\\.");
        String simpleName = a[a.length - 1];
        if (simpleName.contains("$")) {
            simpleName = simpleName.split("\\$")[1];
        }
        return simpleName;
    }

    public static String unCamalize(String name) {
        StringBuffer newString = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                newString.append(" ");
            }
            newString.append(c);
        }
        String s = newString.toString().trim();
        return capFirstLetter(s);
    }

    public static String capFirstLetter(String s) {
        if (isEmpty(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toUpperCase() + rest;
    }

    public static String uncapFirstLetter(String s) {
        if (isEmpty(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toLowerCase() + rest;
    }

    public static boolean notEmpty(String s1, String s2) {
        return !isEmpty(s1) && !isEmpty(s2);
    }

    public static boolean notEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String indent(int tabCount) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public static void indent(int tabCount, Object thingToPrint) {
        System.out.println(indent(tabCount) + thingToPrint);
    }

    public static String lpad(String unpaddedString, char padChar, int desiredFinalLength) {
        int padCount = desiredFinalLength - unpaddedString.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        sb.append(unpaddedString);
        return sb.toString();
    }

    public static String pad(int spaceCount) {
        if(spaceCount==0) return "";
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < spaceCount; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String tab(int tabCount) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }


    public static String nullNormalize(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.equals("")) return null;
        return s;
    }

    public static boolean containsWhitespace(String s) {
        return s.matches(".*\\s+.*");
    }

    public static boolean containsNonWordChar(String s) {
        return s.matches(".*\\W+.*");
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String crLf() {
        return "\r\n";
    }

    /**
     * Replace reserved XML characters with their XML entity equivalents.
     *
     * @param plainString input string
     * @return An escaped string
     */
    public static String escapeXml(String plainString) {
        if (plainString == null) return null;
        StringBuffer stringBuild = new StringBuffer();
        for (int i = 0; i < plainString.length(); i++) {
            char testChar = plainString.charAt(i);
            if (testChar == '<') stringBuild.append("&lt;");
            else if (testChar == '>') stringBuild.append("&gt;");
            else if (testChar == '&') stringBuild.append("&amp;");
            else if (testChar == '"') stringBuild.append("&quot;");
            else if (testChar == '\'') stringBuild.append("&apos;");
            else stringBuild.append(testChar);
        }
        return stringBuild.toString();
    }

    public static void prindent(int depth, String msg) {
        System.out.println(indent(depth) + msg);
    }

    public static void prindent(int depth, int msg) {
        System.out.println(indent(depth) + msg );
    }
}
