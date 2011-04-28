package com.tms.threed.threedFramework.util.date.shared;

import java.util.Date;

@SuppressWarnings({"deprecation"})
public class Month {

    public static final String[] monthNames = {
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
    };

    private Year y;
    private int m;

    private Date selectedDate;
    private Date startDate;

    public Month() {
        setYearMonth();
    }

    public Month(Year y, int m) {
        if (m < 1 || m > 12) throw new IllegalArgumentException("Month must be between 1 and 12");
        this.y = y;
        this.m = m;
    }

    public Month(int y, int m) {
        this(new Year(y), m);
    }


    @SuppressWarnings({"deprecation"})
    public void setYearMonth(java.util.Date date) {
        this.m = date.getMonth() + 1;
        this.y = new Year(1900 + date.getYear());
        clearCachedValues();
    }

    public void setYearMonth() {
        setYearMonth(new java.util.Date());
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setSelectedDate() {
        setSelectedDate(new Date());
    }

    public void setYearMonthAndSelectedDate(Date date) {
        Date d;
        if (date == null) {
            d = new Date();
        } else {
            d = date;
        }
        this.m = d.getMonth() + 1;
        final int utilYear = d.getYear();
        this.y = new Year(1900 + utilYear);
        this.selectedDate = date;
        clearCachedValues();
    }

    public void reset() {

    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public String getMonthName() {
        return monthNames[m - 1];
    }

    public Date getStartDate() {
        if (this.startDate == null) {
            this.startDate = new Date(y.intValue() - 1900, m - 1, 1);
        }
        return startDate;
    }

    public int getStartDay() {
        Date startDate = getStartDate();
        return startDate.getDay();
    }


    public void next() {
        if (m == 12) {
            m = 1;
            y.next();
        } else {
            m++;
        }
        clearCachedValues();
    }

    private void clearCachedValues() {
        startDate = null;
    }

    public void previous() {
        if (m == 1) {
            y.previous();
            m = 12;
        } else {
            m--;
        }
        clearCachedValues();
    }

    public int getDayCount() {
        switch (this.m) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return (y.isLeap() ? 29 : 28);
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                throw new IllegalStateException("invalid month: " + this.m);
        }
    }

    /**
     *
     * @param cellIndex a number from 0 to 41 inclusive
     * @return a number between 1 and 31
     */
    private int getDayOfMonthAt(int cellIndex) {
        return cellIndex - this.getStartDay() + 1;
    }

    public java.util.Date getDateAt(int row, int col) {
        int cellIndex = getCellIndex(row, col);
        return getDateAt(cellIndex);
    }

    public java.util.Date getDateAt(int cellIndex) {
        int d = getDayOfMonthAt(cellIndex);
        final int yint = y.intValue();
        final Date date = new Date(yint - 1900, m - 1, d);
        return date;
    }

    public Month copy() {
        return new Month(this.y.copy(), this.m);
    }

    public String toString() {
        return getMonthName() + " " + y;
    }

    public Month getNext() {
        Month copy = this.copy();
        copy.next();
        return copy;
    }

    public Month getPrevious() {
        Month copy = this.copy();
        copy.previous();
        return copy;
    }

    public boolean isFringe(int row, int col) {
        int pos = getCellIndex(row, col);
        return isFringe(pos);
    }

    public boolean isFringe(int cellIndex) {
        int i = getDayOfMonthAt(cellIndex);
        if (i <= 0) {
            return true;
        } else if (i >= 1 && i < (getDayCount() + 1)) {
            return false;
        } else {
            return true;
        }
    }

    public int getMonthNumber() {
        return m;
    }

    public Year getYear() {
        return y;
    }

    public boolean isToday(int row, int col) {
        final java.util.Date d = getDateAt(row, col);
        java.util.Date today = new java.util.Date();
        return DateUtil.dateCompare(d, today);
    }

    private int getDayOfMonthAt(int row, int col) {
        int cellIndex = getCellIndex(row, col);
        return getDayOfMonthAt(cellIndex);
    }

    public static int getCellIndex(int row, int col) {
        return row * 7 + col;
    }


}
