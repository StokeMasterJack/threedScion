package threed.admin.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;
import threed.repo.shared.CommitHistory;
import threed.repo.shared.CommitId;
import threed.core.threedModel.shared.RootTreeId;
import threed.repo.shared.Settings;
import threed.core.threedModel.shared.SeriesKey;

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
