package c3i.admin.shared;

import c3i.featureModel.shared.common.BrandKey;
import smartsoft.util.shared.Path;
import c3i.admin.client.SeriesPickList;
import c3i.imageModel.shared.Profiles;
import c3i.repo.shared.Series;

import java.io.Serializable;
import java.util.ArrayList;

public class BrandInit implements Serializable {

//    private static final long serialVersionUID = 2131642088898848026L;

    private /* final */ BrandKey brandKey;
    private /* final */ ArrayList<Series> seriesNameWithYears;

    private /* final */ String userName;

    private /* final */ ArrayList<BrandKey> visibleBrandsForUser;

//    private  /* final */ Path repoContextPath;

    private  /* final */ Profiles profiles;

    private transient SeriesPickList seriesPickList;

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


    public Profiles getProfiles() {
        return profiles;
    }

    @Override
    public String toString() {
        return "InitData{" +
                "seriesNameWithYears=" + seriesNameWithYears +
                ", userName='" + userName + '\'' +
                ", visibleBrandsForUser=" + visibleBrandsForUser +
                '}';
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

}
