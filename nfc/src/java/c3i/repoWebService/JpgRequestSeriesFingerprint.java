package c3i.repoWebService;


import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.Slice;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static smartsoft.util.lang.shared.Strings.isEmpty;


/**
 *
 * http://localhost:8080/configurator-content/avalon/2011/v1/exterior-2/wStd/f1/f2/f2/f4/seriesfp.jpg
 *
 *
 * http://localhost:8080/configurator-content-v2/toyota/avalon/2011/1a35cadec60eae8321ae0ed5595193d578fa9f99/exterior-2/wStd/3544/070/LH02/seriesfp.jpg
 *
 * 3544, 070, LH02
 * http://localhost:8080/configurator-content/<series-name>/<series-year>/<series-version>/<view>-<angle>/<jpg-width>/f1/f2/f2/f4/seriesfp.jpg
 */
public class JpgRequestSeriesFingerprint extends SeriesBasedRepoRequest {

    private final SeriesId seriesId;
    private final Slice slice;
    private final String profileKey;
    private final List<String> varCodes = new ArrayList<String>();


    public JpgRequestSeriesFingerprint(BrandRepos repos, HttpServletRequest request, HttpServletResponse response) {
        super(repos, request, response);

        String uri = getUri();

        String pathInfo = request.getPathInfo();


        String msg = "Bad jpg uri [" + uri + "]";
        if (isEmpty(uri)) throw new NotFoundException(msg);

        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        String[] a = uri.split("/");

        String brand = a[1];
        String seriesName = a[2];
        String seriesYear = a[3];

        SeriesKey seriesKey1 = new SeriesKey(brand, seriesYear, seriesName);

        String sRootTreeCommitId = a[4];


        seriesId = new SeriesId(seriesKey1, new RootTreeId(sRootTreeCommitId));
        slice = new Slice(a[5]);

        this.profileKey = a[6];


        int firstFeature = 7;
        for (int i = firstFeature; i < a.length - 1; i++) {
            varCodes.add(a[i]);
        }

    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public Slice getSlice() {
        return slice;
    }

    public Profile getProfile() {
        Repos repos = getRepos();
        return repos.getProfilesCache().getProfile(brandKey, profileKey);
    }

    public List<String> getVarCodes() {
        return varCodes;
    }
}
