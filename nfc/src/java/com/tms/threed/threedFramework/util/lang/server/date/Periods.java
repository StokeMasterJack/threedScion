package com.tms.threed.threedFramework.util.lang.server.date;

import java.lang.reflect.*;

public class Periods {

    private Date today = new Date();

    public Periods(Date today) {
        this.today = today;
    }

    public String[] getOptions() {
        //todo: change to use reflection
        String[] a = {"YearToDate", "LastYearToDate", "LastYear", "LastMonth", "Today", "Yesterday", "MonthToDate"};
        return a;
    }

    public String get(String periodName) {
        Class aClass = Periods.class;
        try {
            Class[] argTypes = null;
            Object[] args = null;
            Method method = aClass.getMethod("getString" + periodName, argTypes);
            return (String) method.invoke(Periods.class, args);
        }
        catch(Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Period getYearToDate() {
        Date d1 = Date.getFirstDayOfThisYear();
        return new Period(d1, today);
    }

    public Period getLastYear() {
        Date d1 = Date.getFirstDayOfThisYear();
        return new Period(d1, today);
    }

    public Period getLastMonth() {
        Month m = new Month(today.getYear(),today.getMonth());
        m.previous();
        Date d1 = m.getFirstDay();
        Date d2 = m.getLastDay();
        return new Period(d1, d2);
    }

    public Period getToday() {
        return new Period(today, today);
    }

    public Period getYesterday() {
        Date d;
        try {
            d = (Date) today.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        d.previous();
        return new Period(d, d);
    }

    public Period getMonthToDate() {
        Month m = new Month(today.getYear(),today.getMonth());
        Date d1 = m.getFirstDay();
        return new Period(d1, today);
    }


}