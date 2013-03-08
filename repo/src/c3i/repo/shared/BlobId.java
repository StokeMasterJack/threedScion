package c3i.repo.shared;

import c3i.core.threedModel.shared.FullSha;

/**
 * AKA full 40 digit sha referencing a git blob object.
 */
public class BlobId extends FullSha {

    public BlobId(String src) {
        super(src);
    }

}
