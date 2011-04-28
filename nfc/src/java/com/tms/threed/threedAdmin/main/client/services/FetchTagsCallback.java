package com.tms.threed.threedAdmin.main.client.services;

import com.tms.threed.threedFramework.repo.shared.TagCommit;

import java.util.List;

public interface FetchTagsCallback {
    void onSuccess(List<TagCommit> commitTags);
}
