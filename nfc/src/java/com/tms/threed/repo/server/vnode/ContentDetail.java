package com.tms.threed.repo.server.vnode;

import org.eclipse.jgit.lib.ObjectId;

public class ContentDetail {

    private final ObjectId fullSha;
    private final boolean emptyPng;

    ContentDetail(ObjectId fullSha, boolean emptyPng) {
        this.fullSha = fullSha;
        this.emptyPng = emptyPng;
    }

    public ObjectId getFullSha() {
        return fullSha;
    }

    public boolean isEmptyPng() {
        return emptyPng;
    }

}
