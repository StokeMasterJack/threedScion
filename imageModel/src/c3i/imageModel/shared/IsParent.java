package c3i.imageModel.shared;

import java.util.List;

public interface IsParent<CT extends IsChild, V> extends ImNode<V> {

    List<CT> getChildNodes();

    void printTree();


}