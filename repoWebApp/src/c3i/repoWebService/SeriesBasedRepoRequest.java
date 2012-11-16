package c3i.repoWebService;

import c3i.core.common.shared.SeriesKey;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import com.google.common.base.Strings;
import java.util.logging.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SeriesBasedRepoRequest extends RepoRequest {

    protected final SeriesKey seriesKey;

    public SeriesBasedRepoRequest(BrandRepos brandRepos, HttpServletRequest request, HttpServletResponse response) {
        super(brandRepos, request, response);

        String pathInfo = request.getPathInfo();

        if (Strings.isNullOrEmpty(pathInfo)) throw new NotFoundException(baseErrorMessage);

        pathInfo = pathInfo.trim();
        if (pathInfo.substring(0, 1).equals("/")) {
            pathInfo = pathInfo.substring(1);
        }


        String[] a = pathInfo.split("/");
        if (a == null || a.length < 3) throw new NotFoundException(baseErrorMessage);


        String brand = a[INDEX_BRAND];
        String seriesName = a[INDEX_SERIES_NAME];
        String seriesYear = a[INDEX_SERIES_YEAR];


        try {
            this.seriesKey = new SeriesKey(brand, seriesYear, seriesName);
        } catch (Exception e) {
            throw new NotFoundException(baseErrorMessage + e.toString(), e);
        }


    }

    public BrandRepos getBrandRepos() {
        return brandRepos;
    }



    protected SeriesRepo getSeriesRepo() {
        SeriesKey seriesKey = getSeriesKey();
        return getRepos().getSeriesRepo(seriesKey);
    }

    public String getSeriesName() {
        return seriesKey.getName();
    }

    public Integer getSeriesYear() {
        return seriesKey.getYear();
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    private static Logger log = Logger.getLogger("c3i");


}
