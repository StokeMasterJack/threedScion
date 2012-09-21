package c3i.repo.shared;

import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;

public class SeriesCommit {

    private final SeriesKey seriesKey;
    private final CommitHistory commitHistory;

    public SeriesCommit(SeriesKey seriesKey, CommitHistory commitHistory) {
        this.seriesKey = seriesKey;
        this.commitHistory = commitHistory;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public CommitHistory getCommitHistory() {
        return commitHistory;
    }

    @Override
    public String toString() {
        return seriesKey + "[" + commitHistory.getCommitId() + "]";
    }

    public CommitId getCommitId() {
        return commitHistory.getCommitId();
    }

    public RootTreeId getRootTreeId() {
        return commitHistory.getRootTreeId();
    }

    public SeriesId getSeriesId() {
        return new SeriesId(seriesKey, commitHistory.getRootTreeId());
    }
}
