package c3i.imgGen.server;

import c3i.imageModel.shared.RawBaseImage;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.Set;

public class JpgSet implements Iterable<RawBaseImage> {

    private static final long serialVersionUID = 8042356853513828480L;

    private final ImmutableSet<RawBaseImage> jpgSpecs;

    public JpgSet(Set<RawBaseImage> set) {
        if (set instanceof ImmutableSet) {
            jpgSpecs = (ImmutableSet<RawBaseImage>) set;
        } else {
            jpgSpecs = ImmutableSet.copyOf(set);
        }
    }

    @Override
    public Iterator<RawBaseImage> iterator() {
        return jpgSpecs.iterator();
    }

    public ImmutableSet<RawBaseImage> getJpgSpecs() {
        return jpgSpecs;
    }

    public int size() {
        return jpgSpecs.size();
    }


    @Override
    public String toString() {
        return super.toString();
    }


    public int getJpgCount() {
        return size();
    }
}
