package com.tms.threed.threedFramework.util.lang.server.date;

import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

public class Date implements Comparable, Cloneable, Serializable {

    private static final long serialVersionUID = -3270357775297205372L;

    private int y;
    private int m;
    private int d;

    public static final String SQL_FORMAT = "yyyy-MM-dd";
    public static final String JDBC_FORMAT = "{d 'yyyy-MM-dd'}";

    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    public Date(Month m, int dayOfMonth) {
        this(m.getYear(), m.getMonth(), dayOfMonth);
    }

    public Date(java.util.Date d) {
        set(d);
    }

    public Date(java.sql.Date d) {
        set(d);
    }

    public Date(Calendar c) {
        set(c);
    }

    public Date() {
        set(new java.util.Date());
    }

    public Date(int y, int m, int d) {
        this.y = y;
        this.m = m;
        this.d = d;
    }

    public Date(String dateShortFormat, Locale locale) {
        try {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            java.util.Date utilDate = dateFormat.parse(dateShortFormat);
            set(utilDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new DateParseException(dateShortFormat);
        }
    }

    public Date(String dateShortFormat) {
        try {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
            java.util.Date utilDate = dateFormat.parse(dateShortFormat);
            set(utilDate);
        }
        catch (ParseException e) {
            throw new DateParseException(dateShortFormat);
        }
    }

    public Date(String date, String format) throws DateParseException {
        try {
            DateFormat df = new SimpleDateFormat(format);
            java.util.Date utilDate = df.parse(date);
            set(utilDate);
        }
        catch (ParseException e) {
            throw new DateParseException(date);
        }
    }

    public Date(long time) {
        this(new java.util.Date(time));

    }

    public static Date fromUtilDate(java.util.Date d) {
        return new Date(d);
    }

    public boolean isBefore(Date that) {
        return (this.compareTo(that) < 0);
    }

    public boolean isAfter(Date that) {
        return (this.compareTo(that) > 0);
    }

    private void set(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        set(c);
    }

    private void set(Calendar c) {
        y = c.get(Calendar.YEAR);
        m = c.get(Calendar.MONTH) + 1;
        d = c.get(Calendar.DAY_OF_MONTH);
    }

    public Date addDays(int days) {
        if (days == 0) return copy();
        Calendar c = this.toUtilCalendar();
        c.add(Calendar.DATE, days);
        return new Date(c);
    }

    public int getYear() {
        return y;
    }

    public int getMonth() {
        return m;
    }

    /**
     * @return an int between 1 and 31
     */
    public int getDayOfMonth() {
        return d;
    }

    private void setYear(int y) {
        this.y = y;
    }

    private void setMonth(int m) {
        this.m = m;
    }

    private void setDayOfMonth(int d) {
        this.d = d;
    }

    public java.util.Date toUtilDate() {
        return toUtilCalendar().getTime();
    }

    public java.util.Calendar toUtilCalendar() {
        java.util.Calendar c = new GregorianCalendar(y, m - 1, d, 0, 0, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c;
    }

    public java.sql.Date toSqlDate() {
        return new java.sql.Date(toUtilDate().getTime());
    }

    public String toString(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(this.toUtilDate());
    }

    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(toUtilDate());
    }

    public String toString(Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return dateFormat.format(toUtilDate());
    }

    public long toLong() {
        return toUtilDate().getTime();
    }

    /**
     * returns 'yyyy-mm-dd'
     */
    public String toStringSql() {
        return this.toString("yyyy-MM-dd");
    }

    /**
     * returns {d 'yyyy-mm-dd'}
     */
    public String toStringJdbc() {
        return "{d " + "'" + this.toStringSql() + "'" + "}";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Date)) throw new IllegalArgumentException();

        final Date date = (Date) o;

        if (d != date.d) return false;
        if (m != date.m) return false;
        if (y != date.y) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = y;
        result = 29 * result + m;
        result = 29 * result + d;
        return result;
    }


    /**
     * @return neg int -> this < that
     *         0       -> this = that
     *         pos int -> this > that
     */
    public int compareTo(Object o) {
        if (o == null) return 1;
        Date that = (Date) o;

        if (this.y > that.y) return 1;
        if (this.y < that.y) return -1;

        if (this.m > that.m) return 1;
        if (this.m < that.m) return -1;

        if (this.d > that.d) return 1;
        if (this.d < that.d) return -1;

        return 0;

    }

    /**
     * Advance <i>howMany</i> number of days
     *
     * @param howMany
     */
    public void next(int howMany) {
        for (int i = 0; i < howMany; i++) {
            this.next();
        }
    }

    /**
     * Rollback <i>howMany</i> number of days
     *
     * @param howMany
     */
    public void previous(int howMany) {
        for (int i = 0; i < howMany; i++) {
            this.previous();
        }
    }

    /**
     * Advance one day
     */
    public void next() {
        Month ym = new Month(y, m);
        if (this.d == ym.getDayCount()) {
            this.d = 1;
            ym.next();
            y = ym.getYear();
            m = ym.getMonth();
        } else {
            this.d++;
        }
    }

    public Date getNext(int howManyDays) {
        Date newDate = this.copy();
        newDate.next(howManyDays);
        return newDate;
    }

    public Date getPrevious(int howManyDays) {
        try {
            Date newDate = (Date) this.clone();
            newDate.previous(howManyDays);
            return newDate;
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Rollback one day
     */
    public void previous() {
        Month ym = new Month(y, m);
        if (this.d == 1) {
            ym.previous();
            this.d = ym.getDayCount();
            this.m = ym.getMonth();
            this.y = ym.getYear();
        } else {
            this.d--;
        }
    }

    public boolean isToday() {
        Date today = new Date();
        if (today.equals(this)) return true;
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public Date copy() {
        return new Date(y, m, d);
    }

    /**
     * @param d2
     * @param d1
     * @return number of days between the two dates
     */
    public static int diff(Date d2, Date d1) {
        if (d2 == null) throw new IllegalArgumentException("d2 is required");
        if (d1 == null) throw new IllegalArgumentException("d1 is required");
        if (d2.compareTo(d1) < 0) return 0;
        try {
            Date D2 = (Date) d2.clone();
            Date D1 = (Date) d1.clone();
            for (int i = 0; i < 100000; i++) {
                if (D2.equals(D1)) return i;
                D2.previous();
            }
            throw new IllegalArgumentException("Dates too far apart");
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

    }


    public static Date getFirstDayOfThisMonth() {
        Month m = new Month();
        return m.getFirstDay();
    }

    public static Date getFirstDayOfLastMonth() {
        Month m = new Month();
        m.previous();
        return m.getFirstDay();
    }

    public static Date getFirstDayOfThisYear() {
        Date d = new Date();
        d.setMonth(0);
        d.setDayOfMonth(1);
        return d;
    }

    public static Date getFirstDayOfLastYear() {
        Date d = getFirstDayOfThisYear();
        d.setYear(d.getYear() - 1);
        return d;
    }

    public static Date getLastDayOfthisMonth() {
        Month m = new Month();
        return m.getLastDay();
    }

    public static Date getLastDayOfLastMonth() {
        Month m = new Month();
        m.previous();
        return m.getLastDay();
    }

    public int getDayOfWeek() {
        Calendar gc = toUtilCalendar();
        return gc.get(Calendar.DAY_OF_WEEK);
    }

    public Timestamp toSqlTimestamp() {
        return new Timestamp(toLong());
    }

    public Date addMonths(int howMany) {
        Calendar gc = toUtilCalendar();
        gc.add(Calendar.MONTH, howMany);
        return new Date(gc);
    }

    public boolean isWeekEnd() {
        int dayOfWeek = getDayOfWeek();
        return dayOfWeek == Date.SATURDAY || dayOfWeek == Date.SUNDAY;
    }

    public boolean isMonday() {
        return getDayOfWeek() == MONDAY;
    }

    public Month getYearMonth() {
        return new Month(getYear(), getMonth());
    }
}
