package c3i.admin.shared;

import smartsoft.util.lang.shared.Path;
import c3i.admin.client.SeriesPickList;
import c3i.core.common.shared.BrandKey;
import c3i.core.imageModel.shared.Profiles;
import c3i.repo.shared.Series;
import c3i.smartClient.client.service.ThreedModelClient;

import java.io.Serializable;
import java.util.ArrayList;

public class BrandInit implements Serializable {

//    private static final long serialVersionUID = 2131642088898848026L;

    private /* final */ BrandKey brandKey;
    private /* final */ ArrayList<Series> seriesNameWithYears;

    private /* final */ String userName;

    private /* final */ ArrayList<BrandKey> visibleBrandsForUser;

    private  /* final */ Path repoContextPath;

    private  /* final */ Profiles profiles;

    private transient SeriesPickList seriesPickList;
    private transient ThreedModelClient threedModelClient;

    public BrandInit(BrandKey brandKey, ArrayList<Series> seriesNameWithYears,  String userName, ArrayList<BrandKey> visibleBrandsForUser, Path repoContextPath, Profiles profiles) {
        if (seriesNameWithYears == null) {
            throw new IllegalArgumentException("seriesNameWithYears must be non-null");
        }
        if (visibleBrandsForUser == null) {
            throw new IllegalArgumentException("visibleBrandsForUser must be non-null");
        }
        if (userName == null) {
            throw new IllegalArgumentException("userName must be non-null");
        }
        this.brandKey = brandKey;
        this.seriesNameWithYears = seriesNameWithYears;
        this.userName = userName;
        this.visibleBrandsForUser = visibleBrandsForUser;
        this.repoContextPath = repoContextPath;
        this.profiles = profiles;
    }

    private BrandInit() {
    }

    public ArrayList<Series> getSeriesNameWithYears() {
        return seriesNameWithYears;
    }

    public SeriesPickList getSeriesPickList() {
        if (seriesPickList == null) {
            seriesPickList = new SeriesPickList(brandKey, seriesNameWithYears);
        }
        return seriesPickList;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<BrandKey> getVisibleBrandsForUser() {
        return visibleBrandsForUser;
    }

    public Path getRepoContextPath() {
        return repoContextPath;
    }

    public Path getRepoBaseUrl() {
        return repoContextPath;
    }

    public Profiles getProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return "InitData{" +
                "seriesNameWithYears=" + seriesNameWithYears +
                ", userName='" + userName + '\'' +
                ", visibleBrandsForUser=" + visibleBrandsForUser +
                ", repoContextPath=" + repoContextPath +
                '}';
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public ThreedModelClient getThreedModelClient() {
        if(threedModelClient==null){
            threedModelClient = new ThreedModelClient(getRepoBaseUrl());
        }
        return threedModelClient;
    }
}
