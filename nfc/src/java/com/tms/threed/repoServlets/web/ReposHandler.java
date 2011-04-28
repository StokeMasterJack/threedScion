package com.tms.threed.repoServlets.web;

import com.tms.threed.threedFramework.repo.server.Repos;

import javax.servlet.ServletContext;

public abstract class ReposHandler<T extends RepoRequest> {

    protected final Repos repos;
    protected final ServletContext application;

    protected ReposHandler(Repos repos, ServletContext application) {
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
