package c3i.imgGen.shared;

import java.io.Serializable;

public class Stats implements Serializable {

    private static final long serialVersionUID = -1010209065205998197L;

    public volatile long masterJobStartTime;
    public volatile int combinePngsDeltaSum;
    public volatile int readSrcPngDeltaSum;
    public volatile int maybeScalePngDeltaSum;
    public volatile int writeBaseImageDeltaSum;
    public volatile long masterJobEndTime = -1;
    public volatile TerminalStatus finalStatus;

    public void printDeltas() {
        System.out.println("masterJobStartTime = " + masterJobStartTime);
        System.out.println("combinePngsDeltaSum = " + combinePngsDeltaSum);
        System.out.println("readSrcPngDeltaSum = " + readSrcPngDeltaSum);
        System.out.println("maybeScalePngDeltaSum = " + maybeScalePngDeltaSum);
        System.out.println("writeBaseImageDeltaSum = " + writeBaseImageDeltaSum);
        System.out.println("masterJobEndTime = " + masterJobEndTime);
        System.out.println("finalStatus = " + finalStatus);
        System.out.println();
    }

    public double getDurationInMinutes() {
        long et = masterJobEndTime == -1 ? System.currentTimeMillis() : masterJobEndTime;
        double diff = et - masterJobStartTime;
        double seconds = diff / 1000.0;
        double minutes = seconds / 60.0;
        return minutes;
    }


}
