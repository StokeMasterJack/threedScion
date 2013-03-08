package c3i.imgGen;

public class Progress {

    private final long total;
    private final long current;

    private final double dTotal;
    private final double dCurrent;

    public Progress(long total, long current) {
        this.total = total;
        this.current = current;

        dTotal = (double) total;
        dCurrent = (double) current;
    }

    public long getTotal() {
        return total;
    }

    public long getCurrent() {
        return current;
    }

    public double getRatioComplete() {
        if (total == -1) {
            return -1;
        }
        return dCurrent / dTotal;
    }

    public int getPercentComplete() {
        if (total == -1) {
            return -1;
        }
        return (int) Math.round(getRatioComplete() * 100);
    }

    @Override
    public String toString() {
        if (total == -1) {
            return current + " of " + total;
        }
        return current + " of " + total + "  (" + getPercentComplete() + "% complete)";
    }
}
