package com.tms.threed.threedAdmin.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.repo.shared.CommitHistory;
import com.tms.threed.repo.shared.CommitId;
import com.tms.threed.threedCore.threedModel.shared.RootTreeId;
import com.tms.threed.repo.shared.Settings;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;

public interface ThreedAdminServiceAsync {

    void getVtcRootTreeId(SeriesKey seriesKey, AsyncCallback<RootTreeId> async);

    void setVtcRootTreeId(SeriesKey seriesKey, RootTreeId rootTreeId, AsyncCallback<Void> async);

    void getSettings(AsyncCallback<Settings> async);

    void saveSettings(Settings repoConfig, AsyncCallback<Void> async);

    void getInitData(AsyncCallback<InitData> async);

    void tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId, AsyncCallback<CommitHistory> async);

    void addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag, AsyncCallback<CommitHistory> async);

    void getCommitHistory(SeriesKey seriesKey, AsyncCallback<CommitHistory> async);

    void purgeRepoCache(AsyncCallback<Void> async);
}
