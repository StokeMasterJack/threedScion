package c3i.repoWebService;

import c3i.repo.server.Repos;
import com.google.common.base.Preconditions;

import javax.servlet.ServletContext;

public abstract class ReposHandler<T extends RepoRequest> {

    protected final Repos repos;
    protected final ServletContext application;

    protected ReposHandler(Repos repos, ServletContext application) {
        Preconditions.checkNotNull(repos);
        Preconditions.checkNotNull(application);

        this.repos = repos;
        this.application = application;
    }

    public Repos getRepos() {
        return repos;
    }

    public ServletContext getApplication() {
        return application;
    }

    public abstract void handle(T reposRequest);

}
