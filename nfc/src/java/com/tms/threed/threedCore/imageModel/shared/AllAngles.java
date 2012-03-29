package com.tms.threed.threedCore.imageModel.shared;

public class AllAngles implements CacheAheadPolicy {

    @Override
    public boolean isNoCacheAhead() {
        return false;
    }

}
