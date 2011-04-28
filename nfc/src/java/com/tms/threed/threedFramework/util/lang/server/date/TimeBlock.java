package com.tms.threed.threedFramework.util.lang.server.date;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;

public class TimeBlock implements Comparable {

    private TreeSet<Date> dates = new TreeSet<Date>();

    public TimeBlock() {
    }

    /**
     * @param datesString A comma separated list of dates and or date ranges.
     *                    Ex 1: 6/5/2006-6/7/2006,6/10/2006-6/11/2006
     *                    Ex 2: 7/1/2006,8/1/2006,9/1/2006
     */
    public static TimeBlock createFromDateString(String datesString) {
        if(datesString == null) throw new IllegalArgumentException("\"datesString\" is required");
        TimeBlock tb = new TimeBlock();
        String[] a = datesString.split(",");
        for (String s : a) {
            if (s.indexOf("-") == -1) {
                Date singleDate = new Date(s);
                tb.add(singleDate);
            }
            else {
                String[] dateRange = s.split("-");
                Date d1 = new Date(dateRange[0]);
                Date d2 = new Date(dateRange[1]);
                tb.add(d1, d2);
            }
        }
        return tb;
    }

    public TimeBlock(Collection<Date> dates) {
        add(dates);
    }

    public void add(Collection<Date> dates) {
        for (Date d : dates) {
            this.dates.add(d);
        }
    }

    public TimeBlock(Date date) {
        add(date);
    }

    public void add(Date date) {
        this.dates.add(date);
    }

    public TimeBlock(Date startDate, int dayCount) {
        add(startDate, dayCount);
    }

    public void add(Date startDate, int dayCount) {
        for (int i = 0; i < dayCount; i++) {
            this.dates.add(startDate.addDays(i));
        }
    }

    public TimeBlock(Date startDate, Date endDate) {
        add(startDate, Date.diff(endDate, startDate));
    }

    public void add(Date startDate, Date endDate) {
        add(startDate, Date.diff(endDate, startDate)+1);
    }

    public SortedSet<Date> getDates() {
        return dates;
    }

    public List<Date> getDatesAsList() {
        return new LinkedList<Date>(getDates());
    }

    public boolean isConflicting(TimeBlock that) {
        for (Date thatDate : that.dates) {
            if (isConflicting(thatDate)) return true;
        }
        return false;
    }

    public boolean isConflicting(Date d) {
        for (Date date : dates) {
            if (date.equals(d)) return true;
        }
        return false;
    }


    public Date getStartDate() {
        return dates.first();
    }

    public Date getEndDate() {
        return dates.last();
    }

    public String toString() {
        return getDatesAsList().toString();
    }

    public int compareTo(Object o) {
        TimeBlock that = (TimeBlock) o;
        Date thisStartDate = this.getStartDate();
        Date thatStartDate = that.getStartDate();
        return thisStartDate.compareTo(thatStartDate);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TimeBlock block = (TimeBlock) o;

        if (!dates.equals(block.dates)) return false;

        return true;
    }

    public int hashCode() {
        return dates.hashCode();
    }

    public int size() {
        return dates.size();
    }


    public TimeBlock copy() {
        return new TimeBlock((Collection<Date>) dates.clone());
    }

    public TimeBlock shift(int days) {
        TreeSet<Date> shifted = new TreeSet<Date>();
        for (Date date : this.dates) {
            shifted.add(date.addDays(days));
        }
        return new TimeBlock(shifted);
    }


    public Date first() {
        return dates.first().copy();
    }

    private Date last() {
        return dates.last();
    }

    public void remove(Date date) {
        dates.remove(date);
    }

    public Iterator<Date> iterator() throws Exception {
        return dates.iterator();
    }

    public void print() {
        for (Date date : dates) {
            System.out.println(date.toString("EEE MM/dd/yyyy"));
        }
    }

    public TimeBlock invert() {
        Date startDate = first();
        Date endDate = last();
        SortedSet<Date> inverts = new TreeSet<Date>();
        for (Date d = startDate; !d.equals(endDate); d = d.addDays(1)) {
            if (!dates.contains(d)) {
                inverts.add(d);
            }
        }
        return new TimeBlock(inverts);
    }


    /**
     * @param dayCount
     * @return Assuming that this TimeBlock represents blackout dates,
     *         find all possible contiguous Period's of length <code>dayCount</code>
     */
    public SortedSet<TimeBlock> getSubBlocks(int dayCount) {
        SortedSet<TimeBlock> subBlocks = new TreeSet<TimeBlock>();
        TimeBlock block = new TimeBlock(getStartDate(), getEndDate());
        SortedSet<Date> set = block.getDates();
        for (Date date : set) {
            TimeBlock proposedBlock = new TimeBlock(date, dayCount);
            if (!this.isConflicting(proposedBlock)) {
                subBlocks.add(proposedBlock);
            }
        }
        return subBlocks;
    }

    public SortedSet<TimeBlock> getSubBlocksNoOverlap(int dayCount) {
        SortedSet<TimeBlock> subBlocks = new TreeSet<TimeBlock>();
        TimeBlock master = new TimeBlock();
        master.add(this);
        SortedSet<Date> set = this.invert().getDates();
        for (Date date : set) {
            TimeBlock proposedBlock = new TimeBlock(date, dayCount);
            if (!master.isConflicting(proposedBlock)) {
                subBlocks.add(proposedBlock);
                master.add(proposedBlock);
            }
        }
        return subBlocks;
    }

    public void add(TimeBlock timeBlock) {
        dates.addAll(timeBlock.getDates());
    }

    public Period getSpan() {
        return new Period(getStartDate(), getEndDate());
    }

    public int getSpanDayCount(){
        return getSpan().getDayCount();
    }

    public Date get(int i){
        return getDatesAsList().get(i);       
    }

}
