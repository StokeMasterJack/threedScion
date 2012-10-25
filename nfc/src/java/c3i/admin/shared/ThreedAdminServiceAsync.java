package c3i.admin.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.Settings;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ThreedAdminServiceAsync {

    void getVtcRootTreeId(SeriesKey seriesKey, AsyncCallback<RootTreeId> async);

    void setVtc(SeriesKey seriesKey, CommitKey commitKey, AsyncCallback<CommitHistory> async);


    void tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId, AsyncCallback<CommitHistory> async);

    void addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag, AsyncCallback<CommitHistory> async);

    void getCommitHistory(SeriesKey seriesKey, AsyncCallback<CommitHistory> async);

    void purgeRepoCache(AsyncCallback<Void> async);

    void getInitData(BrandKey brandKey, AsyncCallback<BrandInit> async);

    void getSettings(BrandKey brandKey, AsyncCallback<Settings> async);

    void saveSettings(BrandKey brandKey, Settings repoConfig, AsyncCallback<Void> async);
}
