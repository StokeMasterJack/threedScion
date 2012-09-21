package c3i.repo.server.rt;

import javax.annotation.Nullable;


/**
 * timestamp|tag|modelId|parentCommitId
 * timestamp|-|modelId|-|
 */
public class Commit {

    private final CommitTime commitTime;
    private final CommitTag tag;
    private final ThreedModelSha modelId;
    private final CommitId parentCommitId;

    public Commit(CommitTime commitTime, @Nullable CommitTag tag, ThreedModelSha modelId, @Nullable CommitId parentCommitId) {
        if (commitTime == null) throw new NullPointerException();
        if (modelId == null) throw new NullPointerException();


        this.commitTime = commitTime;

        this.tag = tag;
        this.modelId = modelId;
        this.parentCommitId = parentCommitId;
    }

    public Commit(String serialCommit) {
        if (serialCommit == null) throw new NullPointerException();
        serialCommit = serialCommit.trim();
        String[] a = serialCommit.split("\\|");

        if (a.length != 4) throw new IllegalArgumentException("Bad serialCommit[" + serialCommit + "]");
        commitTime = new CommitTime(a[0]);
        tag = a[1].equals("-") ? null : new CommitTag(a[1]);
        modelId = new ThreedModelSha(a[2]);
        parentCommitId = a[3].equals("-") ? null : new CommitId(a[3]);
    }


    public CommitTime getCommitTime() {
        return commitTime;
    }

    public CommitTag getTag() {
        return tag;
    }

    public ThreedModelSha getModelId() {
        return modelId;
    }

    public CommitId getParentCommitId() {
        return parentCommitId;
    }

    public String serialize() {
        StringBuilder a = new StringBuilder();
        a.append(commitTime);
        a.append('|');
        a.append(tag == null ? "-" : tag);
        a.append('|');
        a.append(modelId);
        a.append('|');
        a.append(parentCommitId == null ? "-" : parentCommitId);
        return a.toString();
    }

    @Override public String toString() {
        return serialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commit commit = (Commit) o;

        if (!commitTime.equals(commit.commitTime)) return false;
        if (!modelId.equals(commit.modelId)) return false;
        if (parentCommitId != null ? !parentCommitId.equals(commit.parentCommitId) : commit.parentCommitId != null)
            return false;
        if (tag != null ? !tag.equals(commit.tag) : commit.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commitTime.hashCode();
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + modelId.hashCode();
        result = 31 * result + (parentCommitId != null ? parentCommitId.hashCode() : 0);
        return result;
    }
}
