package com.tms.threed.threedFramework.util.lang.server.date;

import java.io.*;
import java.text.*;
import java.util.*;

public class Time implements Serializable,Comparable{

    private static final long serialVersionUID = -4095692220146797498L;

    private int h;
    private int m;
    private int s;

    public static final String SQL_FORMAT = "hh-mm-ss";

    public Time(java.util.Date d) {
        set(d);
    }

    public Time(java.sql.Time t) {
        set(t);
    }

    public Time(Calendar c) {
        set(c);
    }

    public Time() {
        set(new java.util.Date());
    }

    public Time(int h, int m, int s) {
        this.h = h;
        this.m = m;
        this.s = s;
    }

    public Time(String hhmmss){
        this(hhmmss,"h:mm a");
    }

    public Time(String time,String format){
        try {
            DateFormat df = new SimpleDateFormat(format);
            java.util.Date utilDate = df.parse(time);
            set(utilDate);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Invalid Time: " + time);
        }
    }

    private void set(java.util.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        set(c);
    }

    private void set(Calendar c) {
        this.h = c.get(Calendar.HOUR_OF_DAY);
        this.m = c.get(Calendar.MINUTE);
        this.s = c.get(Calendar.SECOND);
    }

    public Time addDays(int days){
        Calendar c = this.toUtilCalendar();
        c.add(Calendar.DATE,days);
        return new Time(c);
    }

    public int getHour() {
        return h;
    }

    public int getMinute() {
        return m;
    }

    public void setHour(int h) {
        this.h = h;
    }

    public void setMinute(int m) {
        this.m=m;
    }


    public java.util.Date toUtilDate() {
        return toUtilCalendar().getTime();
    }

    public Calendar toUtilCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND,s);
        return c;
    }

    public java.sql.Time toSqlTime() {
        return new java.sql.Time(toUtilDate().getTime());
    }

    public String toString(String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(this.toUtilDate());
    }

    public String toString() {
        return this.toString("h:mm a");
    }

    public String toStringSql(){
        return this.toString("HH:mm:ss");
    }

    public static void main(String[] args) throws Exception {
        String s = "9:00 AM";
        Time t = new Time(s);
        System.out.println(t);
    }


    public Time copy() {
        return new Time(h,m,s);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Time time = (Time) o;

        if (h != time.h) return false;
        if (m != time.m) return false;
        if (s != time.s) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = h;
        result = 29 * result + m;
        result = 29 * result + s;
        return result;
    }

    public long diffInSeconds(Time t2) {
        long x2 = t2.toUtilDate().getTime();
        long x1 = this.toUtilDate().getTime();

        long lDiffInMillis = x2 - x1;
        double dDiffInMillis = (double) lDiffInMillis;
        double diffInSeconds = dDiffInMillis/1000.0;
        return Math.round(diffInSeconds);
    }

    public int compareTo(Object o) {
        Time that = (Time) o;
        return this.toUtilDate().compareTo(that.toUtilDate());
    }
}