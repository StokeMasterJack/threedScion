package com.tms.threed.jpgGen.shared;

import java.io.Serializable;

public class JobStatus implements Serializable {

    private static final long serialVersionUID = -3046165653326222244L;

    private /*final*/ Integer sliceCount;
    private /*final*/ Integer slicesComplete;

    private /*final*/ Integer jpgCount;
    private /*final*/ JpgStateCounts jpgStateCounts;

    private /*final*/ JobState state;

    private /*final*/ String serializedStackTrace;

    public JobStatus(JobState state, Integer sliceCount, Integer slicesComplete, Integer jpgCount, JpgStateCounts jpgStateCounts) {
        this.sliceCount = sliceCount;
        this.slicesComplete = slicesComplete;
        this.jpgCount = jpgCount;
        this.jpgStateCounts = jpgStateCounts;
        this.state = state;
    }

    public static JobStatus createNothingToReportYet() {
        return new JobStatus(JobState.JustStarted, null, null, null, null);
    }

    public static JobStatus createInProcessOrComplete(Integer sliceCount, Integer slicesComplete, Integer jpgCount, JpgStateCounts jpgStateCounts) {
        JobState s;
        if (jpgCount != null && jpgStateCounts != null && jpgStateCounts.getCompleteCount() == jpgCount) {
            s = JobState.Complete;
        } else {
            s = JobState.InProcess;
        }
        return new JobStatus(s, sliceCount, slicesComplete, jpgCount, jpgStateCounts);
    }

    private JobStatus() {
    }

    public static JobStatus createCanceled() {
        return new JobStatus(JobState.Canceled, null, null, null, null);
    }

    public static JobStatus createExceptionStatus(String serializedStackTrace) {
        JobStatus jobStatus = new JobStatus(JobState.Error, null, null, null, null);
        jobStatus.serializedStackTrace = serializedStackTrace;
        return jobStatus;
    }

    public boolean isDone() {
        return state.equals(JobState.Complete) || state.equals(JobState.Error) || state.equals(JobState.Canceled);
    }

    public JobState getState() {
        return state;
    }

    public String getSerializedStackTrace() {
        return serializedStackTrace;
    }

    public Integer getSliceCount() {
        return sliceCount;
    }

    public Integer getSlicesComplete() {
        return slicesComplete;
    }

    public Integer getJpgCount() {
        return jpgCount;
    }

    public Integer getJpgsComplete() {
        if (jpgStateCounts == null) return null;
        return jpgStateCounts.getCompleteCount();
    }

    public Integer getJpgsDone() {
        if (jpgStateCounts == null) return null;
        return jpgStateCounts.getDoneCount();
    }

    public Integer getJpgsCanceled() {
        if (jpgStateCounts == null) return null;
        return jpgStateCounts.getCancelCount();
    }

    public Integer getJpgsErrored() {
        if (jpgStateCounts == null) return null;
        return jpgStateCounts.getErrorCount();
    }

    public void printBrief() {
//        System.err.print(TIME_FORMAT.format(new Date()) + "\t" + state);
//        if (state.equals(JobState.InProcess) || state.equals(JobState.Complete)) {
//            System.err.println(getPercentJpgsCompleteFormatted() + "\t" + getJpgsComplete() + "/" + jpgCount);
//        } else {
//            System.out.println();
//        }
    }

//    private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();
//    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance();

//    public String getPercentJpgsCompleteFormatted() {
//        return PERCENT_FORMAT.format(getPercentJpgsComplete());
//    }

    public double getPercentJpgsComplete() {
        if (jpgCount == null) return 0;
        Integer jpgsComplete = getJpgsComplete();
        if (jpgsComplete == null) return 0;
        double dJpgCount = (double) jpgCount;
        double dJpgsComplete = (double) jpgsComplete;

        double percent = dJpgsComplete / dJpgCount;

        return percent;
    }

    public void printDetail() {
        System.err.println("status:" + state);
        if (state.equals(JobState.InProcess) || state.equals(JobState.Complete)) {
            System.err.println("sliceCount = " + sliceCount);
            System.err.println("slicesComplete = " + slicesComplete);
            System.err.println("jpgCount = " + jpgCount);
            System.err.println("jpgsComplete = " + getJpgsComplete());
            System.err.println();
        }
    }


}
