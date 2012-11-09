package c3i.repoWebService;


import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.Slice;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.ProfilesCache;
import c3i.repo.server.Repos;
import com.google.common.collect.ImmutableSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static smartsoft.util.lang.shared.Strings.isEmpty;


/**
 *
 * http://localhost:8080/configurator-content/avalon/2011/exterior-2/wStd/3544/070/LH02/nofp.jpg
 *
 *
 * 3544, 070, LH02
 * http://localhost:8080/configurator-content/<series-name>/<series-year>/<view>-<angle>/<jpg-width>/f1/f2/f2/f4/nofp.jpg
 */
public class JpgRequestNoFingerprint extends SeriesBasedRepoRequest {

    private final SeriesKey seriesKey;
    private final Slice slice;
    private final String profileKey;
    private final ImmutableSet<String> varCodes;

    public JpgRequestNoFingerprint(BrandRepos repos, HttpServletRequest request, HttpServletResponse response) {
        super(repos, request, response);
        String uri = getUri();


        String msg = "Bad jpg uri [" + uri + "]";
        if (isEmpty(uri)) throw new NotFoundException(msg);

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        String[] a = uri.split("/");

        String seriesYear = a[3];
        String seriesName = a[2];


        BrandKey brandKey = getBrandKey();
        this.seriesKey = new SeriesKey(brandKey, seriesYear, seriesName);

        slice = new Slice(a[4]);
        profileKey = a[5];

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();

        for (int i = 6; i < a.length - 1; i++) {
            String varCode = a[i];
            builder.add(varCode);
        }

        this.varCodes = builder.build();

    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public Slice getSlice() {
        return slice;
    }

    public Profile getProfile() {
        Repos repos = getRepos();
        ProfilesCache profilesCache = repos.getProfilesCache();
        return profilesCache.getProfile(brandKey, profileKey);
    }

    public ImmutableSet<String> getVarCodes() {
        return varCodes;
    }
}
