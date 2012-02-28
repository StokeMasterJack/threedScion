package com.tms.threed.threedFramework.repo.web;


import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;

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
public class JpgRequestFingerprint extends SeriesBasedRepoRequest {

    private JpgId jpgId;

    public JpgRequestFingerprint(HttpServletRequest request, HttpServletResponse response) {
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


        this.jpgId = new JpgId(seriesKey, jpgWidth, fingerprint);


    }

    public String getJpgFingerprint() {
        return jpgId.getFingerprint();
    }

    public JpgWidth getJpgWidth() {
        return jpgId.getWidth();
    }

    public JpgId getJpgId() {
        return jpgId;
    }
}
