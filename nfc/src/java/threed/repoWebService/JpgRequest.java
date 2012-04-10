package threed.repoWebService;


import threed.core.threedModel.shared.JpgKey;
import threed.core.threedModel.shared.JpgWidth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <repo-url-base>/<repo-name>/3d/jpgs/wStd/<png-short-sha>-<png-short-sha>-<png-short-sha>.jpg
 * <repo-url-base>/<repo-name>/3d/jpgs/w300/<png-short-sha>-<png-short-sha>-<png-short-sha>.jpg
 * <repo-url-base>/<repo-name>/3d/jpgs/w200/<png-short-sha>-<png-short-sha>-<png-short-sha>.jpg
 * <p/>
 * http://smartsoftdev.net/configurator-content/avalon/3d/jpgs/wStd/1cd92-3e498.jpg
 * http://localhost:8080/configurator-content/avalon/3d/jpgs/wStd/1cd92-3e498.jpg
 * <p/>
 * /configurator-content/avalon/gen.repo/ab/cde.png
 */
public class JpgRequest extends SeriesBasedRepoRequest {

    private JpgKey jpgKey;

    public JpgRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        String uri = getUri();


        String msg = "Bad jpg uri [" + uri + "]";

        String[] a = uri.split("/3d/jpgs/");
        if (a == null || a.length == 0) {
            throw new NotFoundException("a: " + msg);
        }


        String lastSegment = a[a.length - 1]; // w300/1cd92-3e498.jpg

        a = lastSegment.split("/");

        String jpgWidthSegment = a[0]; //w300
        String jpgFingerprintSegment = a[1]; //  1cd92-3e498.jpg


        JpgWidth jpgWidth = new JpgWidth(jpgWidthSegment);

        int lastDot = jpgFingerprintSegment.lastIndexOf('.');
        if (lastDot == -1)
            throw new NotFoundException(msg + ". jpgFingerprintSegment[" + jpgFingerprintSegment + "] has not dot!");

        a = jpgFingerprintSegment.split("\\.");
        if (a == null || a.length != 2) throw new NotFoundException("c: " + msg);

        String fingerprint = a[0];  //1cd92-3e498


        this.jpgKey = new JpgKey(seriesKey, jpgWidth, fingerprint);


    }

    public String getJpgFingerprint() {
        return jpgKey.getFingerprint();
    }

    public JpgWidth getJpgWidth() {
        return jpgKey.getWidth();
    }

    public JpgKey getJpgKey() {
        return jpgKey;
    }
}
