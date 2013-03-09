package c3i.imgGen.external;

public class Limit {

    private final long offset;
    private final long count;

    public Limit(long offset, long count) {
        this.offset = offset;
        this.count = count;
    }

    public Limit() {
        this.offset = 0L;
        this.count = Long.MAX_VALUE;
    }

}
