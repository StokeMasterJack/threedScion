package c3i.smartClient.client.skins.viewSelectors;

import c3i.smartClient.client.model.ThreedSession;

public interface ViewSelectorFactory {

    ViewSelector create(ThreedSession threedSession);

}
