package c3i.repo.shared;

import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.RootTreeId;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static smartsoft.util.lang.shared.Strings.prindent;

public class CommitHistory implements Serializable {

    private static final long serialVersionUID = -6033440876303469460L;

    private boolean isHead;
    private CommitId commitId;
    private RootTreeId rootTreeId;
    private Set<String> tags;
    private int commitTime;
    private String shortMessage;
    private String committer;
    private CommitHistory[] parents;

    private boolean vtc;

    public CommitHistory(boolean head) {
        setHead(head);
    }

    public CommitHistory() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public CommitId getCommitId() {
        return commitId;
    }

    public void setCommitId(CommitId commitId) {
        this.commitId = commitId;
    }

    public RootTreeId getRootTreeId() {
        return rootTreeId;
    }

    public void setRootTreeId(RootTreeId rootTreeId) {
        this.rootTreeId = rootTreeId;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getTag() {
        if (tags == null || tags.isEmpty()) {
            return null;
        } else {
            return tags.toString().replace("[", "").replace("]", "");
        }
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getSpecialTags() {
        HashSet<String> set = new HashSet<String>();

        if (isHead()) {
            set.add("Latest");
        }
        if (isVtc()) {
            set.add("VTC");
        }

        if (set.isEmpty()) {
            return null;
        }


        return set.toString().replace("[", "").replace("]", "");
    }

    public int getCommitTime() {
        return commitTime;
    }

    public Date getCommitTimeAsDate() {
        long t = (long) commitTime * 1000L;
        return new Date(t);
    }

    public void setCommitTime(int commitTime) {
        this.commitTime = commitTime;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public boolean hasParents() {
        return parents != null && parents.length > 0;
    }

    public CommitHistory[] getParents() {
        return parents;
    }

    public void setParents(CommitHistory[] parents) {
        this.parents = parents;
    }

    public String getDisplayName() {
        if (this.isTagged()) {
            return getTag();
        } else if (isHead()) {
            return RevisionParameter.USER_FRIENDLY_ALIAS_FOR_HEAD;
        } else {
            return getCommitId().getName().substring(0, 5) + "...";
        }
    }

    public void print() {
        print(0);
    }

    public void print(int depth) {
        prindent(depth, "CommitId: " + this.getCommitId());
        prindent(depth, "RootTreeId: " + this.getRootTreeId() + "");
        prindent(depth, "Tag: " + this.getTag());
        prindent(depth, "ShortMessage: " + this.getShortMessage());
        prindent(depth, "Committer: " + this.getCommitter());
        prindent(depth, "CommitTime: " + this.getCommitTime());

        if (this.getParents().length > 0) {
            final CommitHistory parent = this.getParents()[0];
            parent.print(depth + 1);
        }

    }

    @Override
    public String toString() {
        return "[CommitHistory]  commitId[" + commitId + "]";
    }

    public boolean isTagged() {
        return tags != null && tags.size() > 0;
    }

    public boolean isUntagged() {
        return !isTagged();
    }

    public void setVtc(boolean vtc) {
        this.vtc = vtc;
    }

    public boolean isVtc() {
        return vtc;
    }


    public CommitKey getCommitKey() {
        return new CommitKey(commitId, rootTreeId);
    }
}
