package com.tms.threed.threedFramework.imageModel.shared.slice;

import com.tms.threed.threedFramework.util.lang.shared.Path;

public interface ParentMap {

    Parent getParentOf(Node node);

    Path getPathRelativeTo(Node node);

}
