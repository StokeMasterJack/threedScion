package com.tms.threed.repo.shared;

import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import smartsoft.util.lang.shared.Path;

/**
 * jgit calls this a:
 *      git revision string or a
 *      git object references expression
 *
 * git calls it:
 *      revision parameter using an "extended SHA1"
 *
 * For a more complete list of ways to spell object names, see SPECIFYING REVISIONS [object names] section in git-rev-parse
 *
 * In our case it will mostly be:
 *      pngId: either a fullSha or a shortSha
 *      commitId: (aka version, aka rev) either a fullSha or a shortSha or a refName (aka a tagName or a headName)
 */
public class RevisionParameter {

    public static final String USER_FRIENDLY_ALIAS_FOR_HEAD = "Latest";
    public static final String HEAD = "HEAD";
    public static final String MASTER = "master";

    public static final RevisionParameter HEAD_REVISION_PARAMETER = new RevisionParameter(HEAD);
    public static final RevisionParameter MASTER_REVISION_PARAMETER = new RevisionParameter(MASTER);

    private final String stringValue;

    public RevisionParameter(RootTreeId rootTreeId, Path repoRelativeFilePath) {
        this.stringValue = rootTreeId.getName() + ":" + repoRelativeFilePath.toStringNoLeadingSlash();
    }

    public RevisionParameter(String stringValue) {
        this.stringValue = stringValue;
    }

    public String stringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionParameter that = (RevisionParameter) o;

        if (!stringValue.equals(that.stringValue)) return false;

        return true;
    }

    public boolean isFullSha() {
        return stringValue.length()==40;
    }

    public boolean isRef() {
        if (stringValue.equals(HEAD_REVISION_PARAMETER.stringValue)) return true;
        if (stringValue.equals(MASTER_REVISION_PARAMETER.stringValue)) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return stringValue.hashCode();
    }

    public boolean isHead() {
        return this.equals(HEAD_REVISION_PARAMETER);
    }

     public boolean isUserFriendlyHeadAlias() {
        return isUserFriendlyHeadAlias(stringValue);
    }

    public boolean isHeadOrHeadAlias() {
        return isHeadOrHeadAlias(stringValue);
    }

    public static boolean isHeadOrHeadAlias(String revisionParameter) {
        if (revisionParameter == null) return false;
        return isUserFriendlyHeadAlias(revisionParameter) || isHead(revisionParameter);
    }

    public static boolean isHead(String revisionParameter) {
        if (revisionParameter == null) return false;
        return revisionParameter.equals(HEAD);
    }

    public static boolean isUserFriendlyHeadAlias(String revisionParameter) {
        if (revisionParameter == null) return false;
        return revisionParameter.equalsIgnoreCase(USER_FRIENDLY_ALIAS_FOR_HEAD);
    }
}
