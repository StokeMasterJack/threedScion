package com.tms.threed.threedFramework.repo.web;

import com.google.common.base.Strings;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
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
        if (a == null || a.length < 2) throw new NotFoundException(baseErrorMessage);



        String seriesName = a[0];
        String seriesYear = a[1];


        try {
            this.seriesKey = new SeriesKey(seriesYear,seriesName);
        } catch (Exception e) {
            throw new NotFoundException(baseErrorMessage + e.toString(),e);
        }


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
