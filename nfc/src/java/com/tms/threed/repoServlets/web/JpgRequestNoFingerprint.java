package com.tms.threed.repoServlets.web;


import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedCore.shared.Slice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;


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
    private final JpgWidth jpgWidth;
    private final List<String> varCodes = new ArrayList<String>();


    public JpgRequestNoFingerprint(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        String uri = getUri();


        String msg = "Bad jpg uri [" + uri + "]";
        if (isEmpty(uri)) throw new NotFoundException(msg);

        String[] a = uri.split("/");

        String seriesYear = a[3];
        String seriesName = a[2];


        this.seriesKey = new SeriesKey(seriesYear, seriesName);

        slice = new Slice(a[4]);
        jpgWidth = new JpgWidth(a[5]);


        for (int i = 6; i < a.length-1; i++) {
            varCodes.add(a[i]);
        }

    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public Slice getSlice() {
        return slice;
    }

    public JpgWidth getJpgWidth() {
        return jpgWidth;
    }

    public List<String> getVarCodes() {
        return varCodes;
    }
}
