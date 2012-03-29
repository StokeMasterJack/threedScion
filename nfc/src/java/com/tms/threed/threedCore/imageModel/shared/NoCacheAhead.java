package com.tms.threed.threedCore.imageModel.shared;

public class NoCacheAhead implements CacheAheadPolicy {

    @Override
    public boolean isNoCacheAhead() {
        return true;
    }

}
