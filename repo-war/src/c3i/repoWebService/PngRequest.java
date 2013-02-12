package c3i.repoWebService;

import c3i.imageModel.shared.PngSegment;
import c3i.repo.server.BrandRepos;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * <repo-url-base>/<repo-name>/3d/pngs/<short-sha>.png
 *
 * http://smartsoftdev.net/configurator-content/avalon/3d/pngs/1cd92.png
 * http://smartsoftdev.net/configurator-content/tundra/3d/pngs/3e498.png
 *
 * http://localhost:8080/configurator-content/avalon/3d/pngs/1cd92.png
 * http://localhost:8080/configurator-content/avalon/3d/pngs/3e498.png
 *
 */
public class PngRequest extends SeriesBasedRepoRequest {

    private final PngSegment pngKey;

    public PngRequest(BrandRepos brandRepos,HttpServletRequest request, HttpServletResponse response) {
        super(brandRepos,request, response);

        String msg = "Invalid PngHandler URL: [" + getUri() + "].";

        String uri = getUri();
        String[] a = uri.split("/3d/pngs/");
        if (a == null || a.length == 0) throw new NotFoundException(msg);

        String s = a[a.length - 1]; //1cd92595b3.png


        int lastDot = s.lastIndexOf('.');
        if (lastDot == -1) throw new NotFoundException(msg);

        a = s.split("\\.");
        if (a == null || a.length != 2)
            throw new NotFoundException(msg);
        pngKey = new PngSegment(a[0]);

    }

    public PngSegment getPngKey() {
        return pngKey;
    }
}
