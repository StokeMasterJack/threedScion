package com.tms.threed.threedFramework.repo.web;

import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.repo.server.Repos;

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
