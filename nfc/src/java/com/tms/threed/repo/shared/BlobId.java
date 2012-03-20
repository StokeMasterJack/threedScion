package com.tms.threed.repo.shared;

import com.tms.threed.threedCore.threedModel.shared.FullSha;

/**
 * AKA full 40 digit sha referencing a git blob object.
 */
public class BlobId extends FullSha {

    public BlobId(String src) {
        super(src);
    }

}
