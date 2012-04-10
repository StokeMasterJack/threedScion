package threed.repoWebService;

import com.google.common.base.Strings;
import threed.repo.server.Repos;
import threed.repo.server.SeriesRepo;
import threed.core.threedModel.shared.SeriesKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SeriesBasedRepoRequest extends RepoRequest {

    protected final SeriesKey seriesKey;

    public SeriesBasedRepoRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);

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
            this.seriesKey = new SeriesKey(brand,seriesYear, seriesName);
        } catch (Exception e) {
            throw new NotFoundException(baseErrorMessage + e.toString(), e);
        }


    }

    protected SeriesRepo getSeriesRepo() {
        SeriesKey seriesKey = getSeriesKey();
        return Repos.get().getSeriesRepo(seriesKey);
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

    private static Log log = LogFactory.getLog(SeriesBasedRepoRequest.class);


}
