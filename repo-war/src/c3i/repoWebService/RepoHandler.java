package c3i.repoWebService;

import c3i.repo.server.BrandRepos;

public abstract class RepoHandler<T extends RepoRequest> extends ReposHandler<T> {

    protected RepoHandler(BrandRepos brandRepos) {
        super(brandRepos);
    }

    public abstract void handle(T repoRequest);

//    protected SeriesRepo getSeriesRepo(T repoRequest) {
//        SeriesKey seriesKey = repoRequest.getSeriesKey();
//        return repos.getSeriesRepo(seriesKey);
//    }



}
