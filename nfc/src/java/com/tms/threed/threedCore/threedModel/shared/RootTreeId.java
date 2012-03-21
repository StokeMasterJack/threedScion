package com.tms.threed.threedCore.threedModel.shared;

import java.io.Serializable;

/**
 * The full 40 digit hex sha that identifies a the RootTree object.
 */
public class RootTreeId extends FullSha implements Serializable {

    private static final long serialVersionUID = 7063715891125340824L;
    public static final String NAME = "rootTreeId";


    /**
     * @param stringValue  full 40 digit hex sha
     */
    public RootTreeId(String stringValue) {
        super(stringValue);
    }

    protected RootTreeId() {
    }

    @Override
    public String toString() {
        return stringValue();
    }


}
