package com.tms.threed.threedFramework.imageModel.shared;

import java.util.List;

public interface IsParent<CT extends IsChild> extends ImNode {

    List<CT> getChildNodes();

    void printTree();

    
}