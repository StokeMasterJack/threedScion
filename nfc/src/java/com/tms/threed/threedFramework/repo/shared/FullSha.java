package com.tms.threed.threedFramework.repo.shared;

import java.io.Serializable;

/**
 * The full 40 digit hex sha that identifies a repo object.
 */
abstract public class FullSha implements Serializable {

    private static final long serialVersionUID = -6238810143893657950L;

    private String stringValue;

    /**
     * @param fullSha  full 40 digit hex sha
     */
    public FullSha(String fullSha) {
        if (fullSha == null) throw new NullPointerException();
        if (fullSha.length() != 40) throw new IllegalArgumentException("fullSha must be 40 digits long");
        this.stringValue = fullSha;
    }


    protected FullSha() {
    }


    public String stringValue() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullSha that = (FullSha) o;

        return this.stringValue.equals(that.stringValue);

    }

    @Override
    public int hashCode() {
        return stringValue.hashCode();
    }


    public String getName() {
        return stringValue();
    }
}
