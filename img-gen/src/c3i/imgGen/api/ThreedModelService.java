package c3i.imgGen.api;

import c3i.threedModel.shared.ThreedModel;

public interface ThreedModelService<ID> {

    ThreedModel getThreedModel(ID id);

}
