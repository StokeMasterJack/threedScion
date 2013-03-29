package c3i.imgGen.api;

import c3i.threedModel.client.ThreedModel;

public interface ThreedModelService<ID> {

    ThreedModel getThreedModel(ID id);

}
