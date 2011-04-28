package com.tms.threed.threedFramework.jpgGen.shared;

import java.io.Serializable;

public class JpgStateCounts implements Serializable{

    private static final long serialVersionUID = 4837089457962891271L;

    private /*final*/ int completeCount;
    private /*final*/ int cancelCount;
    private /*final*/ int errorCount;

    public JpgStateCounts(int completeCount, int cancelCount, int errorCount) {
        this.completeCount = completeCount;
        this.cancelCount = cancelCount;
        this.errorCount = errorCount;
    }

    protected JpgStateCounts(){}

    public int getCompleteCount() {
        return completeCount;
    }

    public int getDoneCount() {
        return completeCount + cancelCount + errorCount;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public static class Builder {

        private int newCount;
        private int completeCount;
        private int cancelCount;
        private int errorCount;

        public void count(JpgState state) {
            switch (state) {
                case COMPLETE:
                    completeCount++;
                    return;
                case CANCELED:
                    cancelCount++;
                    return;
                case ERROR:
                    errorCount++;
                    return;
                default:
                    throw new IllegalStateException();
            }

        }

        public void count(JpgStateCounts states) {
            if(states==null) return;
            completeCount += states.completeCount;
            cancelCount += states.cancelCount;
            errorCount += states.errorCount;
        }

        public JpgStateCounts build() {
            return new JpgStateCounts(completeCount, cancelCount, errorCount);
        }


    }

}
