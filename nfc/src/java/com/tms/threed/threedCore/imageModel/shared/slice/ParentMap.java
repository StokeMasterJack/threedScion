package com.tms.threed.threedCore.imageModel.shared.slice;

import smartsoft.util.lang.shared.Path;

public interface ParentMap {

    Parent getParentOf(Node node);

    Path getPathRelativeTo(Node node);

}
