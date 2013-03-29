package c3i.imgGen.api;

import c3i.threedModel.client.ThreedModel;

public interface ThreedModelFactory<ID> {

    ThreedModel createThreedModel(ID id);

}
