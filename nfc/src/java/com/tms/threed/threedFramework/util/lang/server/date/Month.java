package com.tms.threed.threedFramework.util.lang.server.date;

import com.tms.threed.threedFramework.util.lang.server.StringUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * AKA Calendar Views
 * Represents a Month for calendaring purposes.
 * Notes:
 * cellIndex is an int from 0 to 41 representing the position
 */
public class Month implements java.io.Serializable, Comparable {

    private int year; //ex: 2000
    private int month; //1 to 12

    public Month() {
        setToToday();
    }

    /**
     * @param uiString ex: 12/2003
     */
    public Month(String uiString) {
        String exampleFormat = "12/2003";
        String msg = "Invalid month format: " + uiString + ". Should look like this: " + exampleFormat;
        if (StringUtil.isBlank(uiString)) throw new IllegalArgumentException("uiString can not be blank");
        if (uiString.length() < 6 || uiString.length() > 7) throw new IllegalArgumentException(msg);
        if (uiString.indexOf("/") == -1) throw new IllegalArgumentException(msg);
        String[] a = uiString.split("/");
        String MM = a[0];
        String yyyy = a[1];
        this.setYear(yyyy);
        this.setMonth(MM);
    }

    /**
     * @param year  ex: 2009
     * @param month 1 to 12
     */
    public Month(int year, int month) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Bad Month:[" + month + "] must be between 1 and 12 inclusive");
        this.year = year;
        this.month = month;
    }

    public Month(Calendar c) {
        this.month = c.get(Calendar.MONTH) + 1;
        this.year = c.get(Calendar.YEAR);
    }

    public Month(String year, String month) {
        this.setYear(year);
        this.setMonth(month);
    }

    public void setToToday() {
        Calendar cal = Calendar.getInstance();
        this.year = cal.get(Calendar.YEAR);
        this.month = cal.get(Calendar.MONTH) + 1;
    }

    public void setYear(int year) {
        int oldValue = this.year;
        if (year < 1000 || year > 3000) throw new IllegalArgumentException("Year must be between 1000 and 3000");
        this.year = year;
        propertyChangeSupport.firePropertyChange("year", oldValue, month);
    }

    public void setYear(String yyyy) {
        if (yyyy == null || yyyy.trim().equals("")) throw new IllegalArgumentException("yyyy is required");
        int y;
        try {
            y = Integer.parseInt(yyyy);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("yyyy must be a whole number");
        }

        if (yyyy.length() == 2) {
            if (y < 20)
                y = 2000 + y;
            else
                y = 1900 + y;
            this.setYear(y);
        } else if (yyyy.length() == 4) {
            this.setYear(y);
        } else {
            throw new IllegalArgumentException("yyyy must be exactly 4  or 2 characters");
        }

    }

    /**
     * @param m number from 1 to 12
     */
    public void setMonth(int m) {
        int oldValue = this.month;
        if (m < 1 || m > 12)
            throw new IllegalArgumentException("Bad month: [" + m + "] month must be between 1 and 12 inclusive");
        this.month = m;

        propertyChangeSupport.firePropertyChange("month", oldValue, month);
    }

    public void setMonth(String mm) {
        if (mm == null || mm.trim().equals("")) throw new IllegalArgumentException("mm is required");
        if (mm.length() < 1 || mm.length() > 2)
            throw new IllegalArgumentException("mm must be exactly 1 or 2 characters");
        try {
            int m = Integer.parseInt(mm);
            this.setMonth(m);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("mm must be a whole number");
        }
    }

    public int getYear() {
        return this.year;
    }

    /**
     * @return 1 to 12
     */
    public int getMonth() {
        return month;
    }

    public String getMonthName() {
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        java.util.Date d = cal.getTime();
        DateFormat df = new SimpleDateFormat("MMMM");
        return df.format(d);
    }

    public String toString() {
        return this.getMonth() + "/" + this.year;
    }

    public String toString(String format) {
        Date d = null;
        DateFormat formatter = null;
        try {
            d = this.getFirstDay();
            formatter = new SimpleDateFormat(format);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return formatter.format(d.toUtilDate());
    }

    public void next() {
        check();
        if (this.month != 12) {
            month++;
        } else {
            this.month = 1;
            this.year++;
        }
        check();
    }

    private void check() {
        if (month < 1 || month > 12) throw new IllegalStateException("Bad Month:[" + month + "]");
    }

    public boolean isFringe(int cellIndex) {
        return !isCurrent(cellIndex);
    }

    public boolean isFringe(int row, int col) {
        return !isCurrent(row, col);
    }

    public boolean isCurrent(int cellIndex) {
        if (cellIndex < this.getStartCellIndex()) return false;
        if (cellIndex > this.getEndCellIndex()) return false;
        return true;
    }

    public boolean isCurrent(int row, int col) {
        return isCurrent(getCellIndex(row, col));
    }

    public void previous() {
        check();
        if (this.month != 1) {
            this.month--;
        } else {
            this.setMonth(12);
            this.setYear(year - 1);
        }
        check();
    }

    //returns the cell index of the first day of the month

    public int getStartCellIndex() {
        Calendar cal = new GregorianCalendar(year, month - 1, 1);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public Date getFirstDay() {
        return new Date(year, month, 1);
    }

    public Date getLastDay() {
        int lastDay = getDayCount();
        return new Date(year, month, lastDay);
    }

    /**
     * @return the cell index of the last day of the month
     */
    public int getEndCellIndex() {
        return getStartCellIndex() + getDayCount() - 1;
    }

    public int getDayCount() {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return (isLeapYear() ? 29 : 28);
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                throw new IllegalStateException("invalid month: " + this.month);
        }
    }

    //cellIndex is 0 thru 41

    public Date getDateAt(int cellIndex) {
        if (month < 1 || month > 12) throw new IllegalStateException("Bad Month:[" + month + "]");
        int dayOfMonth = cellIndex - this.getStartCellIndex() + 1;
        java.util.Calendar c = new java.util.GregorianCalendar(year, month - 1, dayOfMonth);
        Date d = new Date(c);
        return d;
    }

    public Date getDateAt(int row, int col) {
        int cellIndex = getCellIndex(row, col);
        return getDateAt(cellIndex);
    }

    /**
     * @return -1 if date is not on this month's celendar grid
     */
    public int getCellIndex(Date date) {
        for (int cellIndex = 0; cellIndex < 42; cellIndex++) {
            if (getDateAt(cellIndex).equals(date)) return cellIndex;
        }
        return -1;
    }

    public int getRow(Date date) {
        return getRow(getCellIndex(date));
    }

    public int getCol(Date date) {
        return getCol(getCellIndex(date));
    }

    public static int getRow(int cellIndex) {
        return cellIndex / 7;
    }

    public static int getCol(int cellIndex) {
        return cellIndex % 7;
    }

    public static int getCellIndex(int row, int col) {
        return row * 7 + col;
    }

    public boolean isLeapYear() {
        return (this.year % 4) == 0;
    }

    public int compareTo(Object o) {
        Month m = (Month) o;
        String thisYYYYMM = toString("yyyyMM");
        String mYYYYMM = m.toString("yyyyMM");
        return thisYYYYMM.compareTo(mYYYYMM);
    }

    public Object clone() throws CloneNotSupportedException {
        return new Month(getYear(), getMonth());
    }

    public Month copy() {
        return new Month(getYear(), getMonth());
    }

    public Date[] getDates() {
        Date[] dates = new Date[42];
        for (int i = 0; i < 42; i++) {
            dates[i] = getDateAt(i);
        }
        return dates;
    }

    /**
     * @param pos a number from 0 to 41
     * @return date at that cell
     */
    public Date getDate(int pos) {
        return getDateAt(pos);
    }

    public Month getNext() {
        Month m = this.copy();
        m.next();
        return m;
    }

    public Month getPrevious() {
        Month m = this.copy();
        m.previous();
        return m;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Month) || obj == null) return false;
        Month that = (Month) obj;
        return this.year == that.year && this.month == that.month;
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


}











