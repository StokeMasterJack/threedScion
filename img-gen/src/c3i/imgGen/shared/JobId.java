package c3i.imgGen.shared;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class JobId implements Comparable<JobId>,Serializable {

    private static final long serialVersionUID = 5981643237620253463L;
    private long enqueueTime;

    // for serialization
    private JobId() {}

//    public JobId(String enqueueTime) throws IllegalArgumentException {
//        this.enqueueTime = Long.parseLong(enqueueTime);
//    }

    private JobId(long enqueueTime) throws IllegalArgumentException {
        if(enqueueTime < 1){
            throw new IllegalArgumentException();
        }
        this.enqueueTime = enqueueTime;
    }

    public long getEnqueueTime() {
        return enqueueTime;
    }

    public static JobId generateNew(){
        return new JobId(System.currentTimeMillis());
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
