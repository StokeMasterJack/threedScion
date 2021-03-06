package c3i.threedModel.shared;

import c3i.featureModel.shared.common.RootTreeId;

import java.io.Serializable;

public final class CommitKey implements Serializable {

    private /*final*/ CommitId commitId;
    private /*final*/ RootTreeId rootTreeId;

    public CommitKey(CommitId commitId, RootTreeId rootTreeId) {
        this.commitId = commitId;
        this.rootTreeId = rootTreeId;
    }

    public CommitKey() {
    }

    public CommitId getCommitId() {
        return commitId;
    }

    public RootTreeId getRootTreeId() {
        return rootTreeId;
    }
}
