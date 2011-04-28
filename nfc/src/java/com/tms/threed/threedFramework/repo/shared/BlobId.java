package com.tms.threed.threedFramework.repo.shared;

/**
 * AKA full 40 digit sha referencing a git blob object.
 */
public class BlobId extends FullSha {

    public BlobId(String src) {
        super(src);
    }

}
