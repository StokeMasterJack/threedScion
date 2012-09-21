package c3i.repo.shared;

import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.RootTreeId;

import java.io.Serializable;

import static smartsoft.util.lang.shared.Strings.isEmpty;

public class TagCommit implements Comparable<TagCommit>, Serializable {

    private static final long serialVersionUID = -7437514769316080936L;

    private String tagShortName;
    private CommitId commitId;
    private   RootTreeId rootTreeId;
    private   boolean isHead;

    public TagCommit(String tagShortName, String commitId, String rootTreeId, boolean head) {
        if (isEmpty(tagShortName)) throw new IllegalArgumentException("tagShortName cannot be empty");
        if (isEmpty(commitId)) throw new IllegalArgumentException("commitId cannot be empty");
        if (isEmpty(rootTreeId)) throw new IllegalArgumentException("rootTreeId cannot be empty");

        this.tagShortName = tagShortName;
        this.commitId = new CommitId(commitId);
        this.rootTreeId = new RootTreeId(rootTreeId);
        this.isHead = head;
    }

    public TagCommit(String tagShortName, CommitId commitId, RootTreeId rootTreeId, boolean head) {
        if (isEmpty(tagShortName)) throw new IllegalArgumentException("tagShortName cannot be empty");
        if (commitId == null) throw new IllegalArgumentException("commitId cannot be null");
        if (rootTreeId == null) throw new IllegalArgumentException("rootTreeId cannot be null");

        this.tagShortName = tagShortName;
        this.commitId = commitId;
        this.rootTreeId = rootTreeId;
        this.isHead = head;
    }

    protected TagCommit() {
    }

    public void check() {
        if (isEmpty(tagShortName)) throw new IllegalStateException("tagShortName cannot be empty. Unmarshal failed");
        if (commitId == null) throw new IllegalStateException("commitId cannot be null. Unmarshal failed");
        if (rootTreeId == null) throw new IllegalStateException("rootTreeId cannot be null. Unmarshal failed");
    }

    public String getTagShortName() {
        return tagShortName;
    }

    public CommitId getCommitId() {
        return commitId;
    }

    public RootTreeId getRootTreeId() {
        return rootTreeId;
    }

    public boolean isHead() {
        return isHead;
    }

    public boolean isTaggedAsHead() {
        return RevisionParameter.isHead(tagShortName);
    }

    public String getDisplayName() {
        if (isTaggedAsHead()) {
            return RevisionParameter.USER_FRIENDLY_ALIAS_FOR_HEAD;
        } else {
            return tagShortName;
        }
    }

    public boolean isUntagged() {
        return tagShortName.equals("HEAD");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagCommit tagCommit = (TagCommit) o;

        if (isHead != tagCommit.isHead) return false;
        if (!commitId.equals(tagCommit.commitId)) return false;
        if (!rootTreeId.equals(tagCommit.rootTreeId)) return false;
        if (!tagShortName.equals(tagCommit.tagShortName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tagShortName.hashCode();
        result = 31 * result + commitId.hashCode();
        result = 31 * result + rootTreeId.hashCode();
        result = 31 * result + (isHead ? 1 : 0);
        return result;
    }

    @Override public String toString() {
        return "TagCommit{" +
                "tagShortName='" + tagShortName + '\'' +
                ", commitId=" + commitId +
                ", rootTreeId=" + rootTreeId +
                ", isHead=" + isHead +
                '}';
    }

    public String getShortTagName() {
        return tagShortName;
    }

    @Override public int compareTo(TagCommit that) {
        if (this.isTaggedAsHead() && !that.isTaggedAsHead()) {
            return -1;
        } else if (!this.isTaggedAsHead() && that.isTaggedAsHead()) {
            return 1;
        } else {
            return this.tagShortName.compareTo(that.tagShortName);
        }
    }
}
