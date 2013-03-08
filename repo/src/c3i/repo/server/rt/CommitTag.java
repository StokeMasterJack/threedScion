package c3i.repo.server.rt;

import smartsoft.util.shared.Strings;

public class CommitTag {

    protected final String tag;

    public CommitTag(String tag) {
        if (tag == null) throw new NullPointerException();
        tag = Strings.nullNormalize(tag);
        if (tag.contains("|")) throw new IllegalArgumentException("tag name cannot contain the | character");
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag == null ? "-" : tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitTag commitTag = (CommitTag) o;

        if (!tag.equals(commitTag.tag)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }
}
