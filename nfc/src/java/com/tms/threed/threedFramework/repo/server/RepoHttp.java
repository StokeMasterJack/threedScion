package com.tms.threed.threedFramework.repo.server;

import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.server.config.ThreedConfig;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.io.File;

public class RepoHttp {

    public static Repos getRepos(ServletContext application) {
        String attName = Repos.class.getName();
        Repos repos = (Repos) application.getAttribute(attName);
        if (repos == null) {
            repos = createRepos();
            application.setAttribute(attName, repos);
        }
        return repos;
    }

    public static ThreedModel getVtcThreedModel(ServletContext application, String seriesName, int seriesYear) {
        return getVtcThreedModel(application, new SeriesKey(seriesYear, seriesName));
    }

    public static ThreedModel getVtcThreedModel(ServletContext application, SeriesKey seriesKey) {
        Repos repos = getRepos(application);
        VtcService vtcService = new VtcService(repos);
        RootTreeId vtcRootTreeId = vtcService.getVtcRootTreeId(seriesKey);
        SeriesId seriesId = new SeriesId(seriesKey, vtcRootTreeId);
        return getThreedModel(application, seriesId);

    }

    public static ThreedModel getThreedModel(ServletContext application, SeriesId seriesId) {
        Repos repos = getRepos(application);
        return repos.getThreedModel(seriesId);
    }

    public static ThreedModel getThreedModel(ServletContext application, String seriesName, int seriesYear, String rootTreeId) {
        return getThreedModel(application, new SeriesId(seriesName, seriesYear, rootTreeId));
    }

    private static Repos createRepos() {
        File repoBaseDir = ThreedConfig.getRepoBaseDir();
        return new Repos(repoBaseDir);
    }


    private static Log log = LogFactory.getLog(RepoHttp.class);

}
