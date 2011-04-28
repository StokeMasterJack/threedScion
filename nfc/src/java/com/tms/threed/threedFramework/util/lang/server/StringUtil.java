package com.tms.threed.threedFramework.util.lang.server;


import com.tms.threed.threedFramework.util.lang.shared.IORuntimeException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Converts Servlet "Dave Ford" to "daveFord"
     */
    public static String camelizeName(String name) {
        name = name.toLowerCase();
        name = name.trim();

        StringBuffer sb1 = new StringBuffer();
        sb1.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (name.charAt(i - 1) == ' ')
                sb1.append(Character.toUpperCase(c));
            else
                sb1.append(c);
        }

        StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < sb1.length(); i++) {
            char c = sb1.charAt(i);
            if (c != ' ') sb2.append(c);
        }
        if (sb2.length() > 1) {
            return Character.toLowerCase(sb2.charAt(0)) + sb2.substring(1);
        } else {
            return sb2.toString().toLowerCase();
        }
    }

    public static String unCamelizeName(String name) {
        if (isBlank(name)) throw new IllegalArgumentException("name must not be null");
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
        if (isBlank(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toUpperCase() + rest;
    }

    public static String lowerFirstLetter(String s) {
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toLowerCase() + rest;
    }

    public static String chompTrailingComma(String input) {
        return chompTrailing(input, ",");
    }

     public static String chompLeadingComma(String input) {
        return chompLeading(input, ",");
    }

    public static String chompTrailingSemi(String input) {
        return chompTrailing(input, ";");
    }

    public static String chompTrailingSlash(String input) {
        String s = chompTrailing(input, "/");
        return chompTrailing(s, "\\");
    }

    public static String chompTrailingDot(String input) {
        return chompTrailing(input, "");
    }

    public static String chompTrailing(String input, String thingToChomp) {
        if (input == null || input.trim().equals("")) return input;
        if (input.endsWith(thingToChomp)) {
            return input.substring(0, input.length() - thingToChomp.length());
        } else {
            return input;
        }
    }

    public static String chompTrailing(String input, int charCount) {
        if (input == null || input.trim().equals("")) return input;
        return input.substring(0, input.length() - charCount);
    }

    public static String chompLeadingSlash(String input) {
        input = chompLeading(input, "/");
        input = chompLeading(input, "\\");
        return input;
    }

    public static String chompLeadingDot(String input) {
        return chompLeading(input, "");
    }

    public static String chompLeading(String input, String thingToChomp) {
        if (input == null || input.trim().equals("")) return input;
        if (input.startsWith(thingToChomp)) {
            return input.substring(thingToChomp.length());
        } else {
            return input;
        }
    }

    public static boolean startsWithIgnoreCase(String target, String query) {
        if (target == null || query == null) return false;
        if (query.length() > target.length()) return false;
        target = target.toLowerCase();
        query = query.toLowerCase();
        return target.startsWith(query);
    }

    public static String join(String[] a, String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
            sb.append(a[i]);
            if (i != a.length - 1) sb.append(delimiter);
        }
        return sb.toString();
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

    public static String rpad(String unpaddedString, char padChar, int desiredFinalLength) {
        int padCount = desiredFinalLength - unpaddedString.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        sb.insert(0, unpaddedString);
        return sb.toString();
    }

    public static String tab(int indentDepth) {
        return buildString('\t', indentDepth);
    }

    public static String buildString(char c, int qty) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < qty; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String lpad(long L, int desiredLength) {
        return lpad(L + "", '0', desiredLength);
    }

    public static String lpad(int i, int desiredLength) {
        return lpad(i + "", '0', desiredLength);
    }

    public static boolean isBlank(String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.trim().equals("") || s.trim().equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean notEmpty(String s) {
        return !isBlank(s);
    }


    public static boolean notBlank(String s) {
        return !isBlank(s);
    }

    /**
     * trim and convert empty strings to null
     * Also, convert the string "null" to null
     */
    public static String normalize(String s) {
        String n;
        if (isBlank(s)) {
            n = null;
        } else {
            n = s.trim();
            if (n.equalsIgnoreCase("null")) {
                n = null;
            }
        }
        return n;
    }

    public static boolean endsWith(StringBuffer sb, String s) {
        int sbL = sb.length();
        int sL = s.length();
        if (sbL < sL) return false;
        for (int i = 0; i < sL; i++) {
            int sIndex = i;
            int sbIndex = (sbL - sL) + sIndex;
            char sbChar = sb.charAt(sbIndex);
            char sChar = s.charAt(sIndex);
            if (sbChar != sChar) return false;
        }
        return true;
    }

    public static String readUntil(Reader r, String s, int max) throws java.io.IOException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < max; i++) {
            int b = r.read();
            if (b == -1) break;
            char c = (char) b;
            sb.append(c);
            if (endsWith(sb, s)) {
                return sb.substring(0, i - s.length() + 1);
            }
        }
        return sb.toString();
    }

    public static String readUntil(InputStream is, String s, int max) {
        if (is == null) throw new NullPointerException("is can not be null");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < max; i++) {
            int b = 0;
            try {
                b = is.read();
            }
            catch (java.io.IOException e) {
                throw new IORuntimeException(e);
            }
            if (b == -1) break;
            char c = (char) b;
            sb.append(c);
            if (endsWith(sb, s)) {
                return sb.substring(0, i - s.length() + 1);
            }
        }
        return sb.toString();
    }

    public static String readAll(InputStream is) {
        if (is == null) throw new NullPointerException("is can not be null");
        return readUntil(is, "!@##$$%#@Q", 1000000000);
    }

    public static String readAll(Reader r) throws java.io.IOException {
        return readUntil(r, "!@##$$%#@Q", 1000000000);
    }

    /**
     * Extracts a line from a multi-line document
     *
     * @param document   a multi-line document
     * @param lineNumber
     * @return one line
     */
    public static String getLine(String document, int lineNumber) {
        StringReader stringReader = new StringReader(document);
        BufferedReader bufferedReader = new BufferedReader(stringReader);
        String line = null;
        for (int i = 0; i < lineNumber; i++) {
            try {
                line = bufferedReader.readLine();
            }
            catch (java.io.IOException e) {
                e.printStackTrace();
                RuntimeException rte = new RuntimeException(e);
                rte.setStackTrace(e.getStackTrace());
                throw rte;
            }
        }
        return line;
    }

    public static String getLastToken(String s, String delimiter) {
        int i = s.lastIndexOf(delimiter);
        return s.substring(i + 1);
    }

    public static boolean isBlankObject(Object o) {
        if (o == null) return true;
        return isBlank(o.toString());
    }

    public static void println1(Object s) {
        System.out.println("|" + s + "|");
    }

    private static final Pattern nonWhitespaceChar = Pattern.compile("\\S");

    public static int indexOfFirstNonWhitespace(String singleLineOfText) {
        final Matcher matcher = nonWhitespaceChar.matcher(singleLineOfText);
        boolean f = matcher.find();
        if (f) return matcher.start();
        else return -1;
    }

    public static String tabsToSpaces(String s) {
        if (s == null) return null;
        return s.replaceAll("\t", "    ");
    }

    private static char[] hexChars = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String byteArray2Hex(byte[] ba) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ba.length; i++) {
            int hbits = (ba[i] & 0x000000f0) >> 4;
            int lbits = ba[i] & 0x0000000f;
            sb.append("" + hexChars[hbits] + hexChars[lbits] + " ");
        }
        return sb.toString();
    }

     public static String nullNormalize(String s) {
        return isEmpty(s) ? null : s;
    }
}