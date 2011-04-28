package com.tms.threed.threedFramework.repo.shared;


import java.io.Serializable;

/**
 * The full 40 digit hex sha that identifies a commit object.
 */
public class CommitId extends FullSha implements Serializable {


    private static final long serialVersionUID = 1336914893350139582L;

    /**
     * @param stringValue  full 40 digit hex sha
     */
    public CommitId(String stringValue) {
        super(stringValue);
    }

    protected CommitId() {
    }


}
