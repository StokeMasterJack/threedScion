package com.tms.threed.threedFramework.util.lang.server.date;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

public class Period {

    private Date startDate = new Date();
    private Date endDate = new Date();

    public Period(Date startDate, Date endDate) {
        if (startDate.isAfter(endDate)) throw new IllegalArgumentException("startDate can not be after endDate");
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getDayCount() {
        try {
            Date d1 = (Date) startDate.clone();
            Date d2 = (Date) endDate.clone();
            if (d1.equals(d2)) {
                return 1;
            }
            else if (d2.compareTo(d1) > 0) {
                int i = 0;
                for (i = 0; !d2.equals(d1); i++) {
                    d2.previous();
                }
                return i + 1;
            }
            else {
                throw new IllegalStateException("startDate can't be after endDate");
            }
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public TimeBlock toTimeBlock() {
        return new TimeBlock(startDate, getDayCount());
    }

    public TimeBlock getWeekEnds() {
        Set<Date> saturdaysAndSundays = new HashSet<Date>();
        for (int i = 0; true; i++) {
            Date d = startDate.addDays(i);
            if (d.isAfter(endDate)) break;
            if (d.isWeekEnd()) {
                saturdaysAndSundays.add(d);
            }
        }
        return new TimeBlock(saturdaysAndSundays);
    }


    public String toString() {
        return startDate + " - " + endDate;
    }

    public boolean contains(Date date) {
        if (date == null) return false;
        if (date.isBefore(startDate)) return false;
        else if (date.isAfter(endDate)) return false;
        else return true;
    }

    public SortedSet<Date> toSortedSet() {
        return toTimeBlock().getDates();
    }

    public TimeBlock getMondays() {
        Set<Date> mondays = new HashSet<Date>();
        for (int i = 0; true; i++) {
            Date d = startDate.addDays(i);
            if (d.isAfter(endDate)) break;
            if (d.isMonday()) {
                mondays.add(d);
            }
        }
        return new TimeBlock(mondays);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Period period = (Period) o;

        if (!endDate.equals(period.endDate)) return false;
        if (!startDate.equals(period.startDate)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = startDate.hashCode();
        result = 29 * result + endDate.hashCode();
        return result;
    }
}
