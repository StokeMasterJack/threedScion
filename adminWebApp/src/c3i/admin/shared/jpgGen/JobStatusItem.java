package c3i.admin.shared.jpgGen;

import java.io.Serializable;
import java.sql.Time;

public class JobStatusItem implements Serializable {

    private /* final */ JobSpec jobSpec;
    private /* final */ JobId jobId;
    private /* final */ JobStatus jobStatus;

    public JobStatusItem(JobSpec jobSpec, JobId jobId, JobStatus jobStatus) {
        this.jobSpec = jobSpec;
        this.jobId = jobId;
        this.jobStatus = jobStatus;

    }

    private JobStatusItem() {
    }

    public JobSpec getJobSpec() {
        return jobSpec;
    }

    public JobId getJobId() {
        return jobId;
    }

    public JobStatus getStatus() {
        return jobStatus;
    }

    public Time getStartTime() {
        return new Time(jobId.getEnqueueTime());
    }

    public JobState getState() {
        return jobStatus.getState();
    }

}

