package threed.repoWebService;

import threed.repo.server.Repos;

import javax.servlet.ServletContext;

public abstract class RepoHandler<T extends RepoRequest> extends ReposHandler<T> {

    protected RepoHandler(Repos repos, ServletContext application) {
        super(repos, application);
    }

    public abstract void handle(T repoRequest);

//    protected SeriesRepo getSeriesRepo(T repoRequest) {
//        SeriesKey seriesKey = repoRequest.getSeriesKey();
//        return repos.getSeriesRepo(seriesKey);
//    }



}
