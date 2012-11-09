package c3i.jpgGen.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.imageModel.shared.Profile;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class JobSpec implements Serializable {

    private static final long serialVersionUID = 4085390083416203543L;

    private SeriesId seriesId;
    private Profile profile;

    public JobSpec(SeriesId seriesId, Profile profile) {
        Preconditions.checkNotNull(profile);
        profile.getBaseImageType();
        this.seriesId = seriesId;
        this.profile = profile;
    }

    private JobSpec() {
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobSpec jobSpec = (JobSpec) o;

        if (!profile.equals(jobSpec.profile)) return false;
        if (!seriesId.equals(jobSpec.seriesId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seriesId.hashCode();
        result = 31 * result + profile.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return seriesId.toString() + " " + profile.toString();
    }

    public BrandKey getBrandKey() {
        return seriesId.getBrandKey();
    }
}
