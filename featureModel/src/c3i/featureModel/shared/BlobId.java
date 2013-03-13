package c3i.featureModel.shared;

import c3i.featureModel.shared.common.FullSha;

/**
 * AKA full 40 digit sha referencing a git blob object.
 */
public class BlobId extends FullSha {

    public BlobId(String src) {
        super(src);
    }

}
