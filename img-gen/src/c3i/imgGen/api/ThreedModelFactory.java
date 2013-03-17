package c3i.imgGen.api;

import c3i.threedModel.shared.ThreedModel;

public interface ThreedModelFactory<ID> {

    ThreedModel createThreedModel(ID id);

}
