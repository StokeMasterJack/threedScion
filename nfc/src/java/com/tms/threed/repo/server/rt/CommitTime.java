package com.tms.threed.repo.server.rt;


import smartsoft.util.date.Date;

public class CommitTime {

    private final long timestamp;

    public CommitTime(long timestamp) {
        Date tenYearsAgo = new Date().addMonths(12 * -10);
        Date d = new Date(timestamp);

        if (d.isBefore(tenYearsAgo)) {
            throw new IllegalArgumentException("BadCommitTime[" + timestamp + "]");
        }

        this.timestamp = timestamp;
    }

    public CommitTime(String timestamp) {
        this(parseCommitTime(timestamp));
    }

    public static long parseCommitTime(String timestamp) {
        if (timestamp == null) throw new NullPointerException();
        if (timestamp.length() < 3) throw new IllegalArgumentException("BadCommitTime[" + timestamp + "]. ");

        try {
            return new Long(timestamp);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("BadCommitTime[" + timestamp + "]. ");
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override public String toString() {
        return timestamp+"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitTime that = (CommitTime) o;

        if (timestamp != that.timestamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }
}
