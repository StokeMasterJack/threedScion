package com.tms.threed.repoServlets.web;


import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
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
 * http://localhost:8080/configurator-content/avalon/2011/v1/exterior-2/wStd/f1/f2/f2/f4/seriesfp.jpg
 *
 *
 * http://localhost:8080/configurator-content/avalon/2011/1a35cadec60eae8321ae0ed5595193d578fa9f99/exterior-2/wStd/3544/070/LH02/seriesfp.jpg
 *
 * 3544, 070, LH02
 * http://localhost:8080/configurator-content/<series-name>/<series-year>/<series-version>/<view>-<angle>/<jpg-width>/f1/f2/f2/f4/seriesfp.jpg
 */
public class JpgRequestSeriesFingerprint extends SeriesBasedRepoRequest {

    private final SeriesId seriesId;
    private final Slice slice;
    private final JpgWidth jpgWidth;
    private final List<String> varCodes = new ArrayList<String>();


    public JpgRequestSeriesFingerprint(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        String uri = getUri();


        String msg = "Bad jpg uri [" + uri + "]";
        if (isEmpty(uri)) throw new NotFoundException(msg);

        String[] a = uri.split("/");

        String seriesYear = a[3];
        String seriesName = a[2];


        SeriesKey seriesKey1 = new SeriesKey(seriesYear, seriesName);

        String sRootTreeCommitId = a[4];


        seriesId = new SeriesId(seriesKey1, new RootTreeId(sRootTreeCommitId));
        slice = new Slice(a[5]);
        jpgWidth = new JpgWidth(a[6]);


        for (int i = 7; i < a.length-1; i++) {
            varCodes.add(a[i]);
        }

    }

    public SeriesId getSeriesId() {
        return seriesId;
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
