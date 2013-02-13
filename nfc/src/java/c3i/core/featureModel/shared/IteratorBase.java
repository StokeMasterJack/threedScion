package c3i.core.featureModel.shared;

import java.util.Iterator;

public class IteratorBase<T> implements Iterator<T> {

    protected long preCount = -1;
    protected long itemsVisitedSoFar;

    public IteratorBase() {

    }


    public void incrementVisitCount() {
        itemsVisitedSoFar++;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public long getPreCount() {
        return preCount;
    }

    public long getItemsVisitedSoFar() {
        return itemsVisitedSoFar;
    }

    public long getProgress() {
        long pre = getPreCount();
        long visits = getItemsVisitedSoFar();
        if (pre == 0) {
            return 0;
        } else if (pre == -1) {
            return itemsVisitedSoFar;
        } else {
            double dPercent = (double) visits / (double) pre;
            double ddPercent = dPercent * 100D;
            return (int) ddPercent;
        }
    }

    public String getProgressMessage() {
        long progress = getProgress();
        if (preCount == -1) {
            return progress + "";
        } else {
            return progress + "%";
        }
    }


    public void printBeforeMessage() {

    }

    public void printProgressMessage() {
        System.out.println(getProgressMessage());
    }

    public void printSummary() {
        System.out.println("# ItemsVisited = [" + getItemsVisitedSoFar() + "]");
    }

    public static int size(Iterator iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    public void selfRun() {
        selfRun(true);
    }

    public void selfRun(boolean printSummary) {
        while (hasNext()) {
            next();
        }
        if (printSummary) printSummary();
    }

}

