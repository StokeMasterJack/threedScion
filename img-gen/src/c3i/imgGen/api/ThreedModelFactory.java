package c3i.imgGen.api;

import c3i.core.threedModel.shared.ThreedModel;

public interface ThreedModelFactory<ID> {

    ThreedModel createThreedModel(ID id);

}
