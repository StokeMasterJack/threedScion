package com.tms.threed.threedAdmin.main.shared;

import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.io.Serializable;
import java.util.ArrayList;

public class InitData implements Serializable {

    private static final long serialVersionUID = 2131642088898848026L;

    private /* final */ ArrayList<SeriesNamesWithYears> seriesNameWithYears;

    private /* final */ RtConfig rtConfig;

    private /* final */ Path repoBaseUrl;

    public InitData(ArrayList<SeriesNamesWithYears> seriesNameWithYears, RtConfig rtConfig, Path repoBaseUrl) {
        if (seriesNameWithYears == null) {
            throw new IllegalArgumentException("seriesNameWithYears must be non-null");
        }
        if (rtConfig == null) {
            throw new IllegalArgumentException("rtConfig must be non-null");
        }
        if (repoBaseUrl == null) {
            throw new IllegalArgumentException("repoBaseUrl must be non-null");
        }
        this.seriesNameWithYears = seriesNameWithYears;
        this.rtConfig = rtConfig;
        this.repoBaseUrl = repoBaseUrl;
    }

    private InitData() {
    }

    public ArrayList<SeriesNamesWithYears> getSeriesNameWithYears() {
        return seriesNameWithYears;
    }

    public RtConfig getRtConfig() {
        return rtConfig;
    }

    public Path getRepoBaseUrl() {
        return repoBaseUrl;
    }

}
