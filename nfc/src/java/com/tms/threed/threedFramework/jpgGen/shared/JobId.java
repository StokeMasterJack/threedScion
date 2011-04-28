package com.tms.threed.threedFramework.jpgGen.shared;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class JobId implements Comparable<JobId>,Serializable {

    private static final long serialVersionUID = 5981643237620253463L;
    private long enqueueTime;

    public JobId() {
        this.enqueueTime = System.currentTimeMillis();
    }

    public JobId(String enqueueTime) throws IllegalArgumentException {
        this.enqueueTime = Long.parseLong(enqueueTime);
    }

    public JobId(long enqueueTime) throws IllegalArgumentException {
        this.enqueueTime = enqueueTime;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobId jobId = (JobId) o;
        return enqueueTime == jobId.enqueueTime;
    }

    @Override
    public int hashCode() {
        return (int) (enqueueTime ^ (enqueueTime >>> 32));
    }

    @Override public int compareTo(@Nonnull JobId that) {
        assert that != null;


        if (enqueueTime < that.enqueueTime) return -1;
        else if (enqueueTime > that.enqueueTime) return 1;
        else if (enqueueTime == that.enqueueTime) {
            return 0;
        } else {
            throw new IllegalStateException();
        }

    }

    @Override public String toString() {
        return enqueueTime + "";
    }
}
