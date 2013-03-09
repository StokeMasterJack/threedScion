package c3i.imgGen.server;

import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.SimplePicks;
import c3i.repo.server.Repos;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static c3i.core.threedModel.shared.ImFeatureModel.toSimplePicks;

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


}
