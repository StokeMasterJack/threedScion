package com.tms.threed.repoWebService;

import com.tms.threed.repo.server.SeriesRepo;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.repo.server.Repos;

import javax.servlet.ServletContext;

public abstract class RepoHandler<T extends SeriesBasedRepoRequest> extends ReposHandler<T> {

    protected RepoHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    public abstract void handle(T repoRequest);

    protected SeriesRepo getSeriesRepo(T repoRequest) {
        SeriesKey seriesKey = repoRequest.getSeriesKey();
        return repos.getSeriesRepo(seriesKey);
    }



}
