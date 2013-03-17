package c3i.imageModel.shared;

import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.featureModel.shared.common.SimplePicks;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import smartsoft.util.shared.Strings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

//import threed.core.imageModel.shared.slice.ImageSlice;
//import threed.core.imageModel.shared.slice.Layer;

public class ImView extends ImChildBase implements IsParent<ImLayer> {

    private final String name;
    private final List<ImLayer> layers;
    private final ViewLiftSpec liftSpec;
    private final int angleCount;
    private final Integer initialAngle;
    private final int index;

    //    transient private ViewKeyOld viewKeyOld;
    transient private ViewKey viewKey;


//    public ImView(int depth, String name, List<ImLayer> layers) {
//        this(depth, name, layers, null);
//    }

    public ImView(int depth, String name, int index, List<ImLayer> layers, @Nullable ViewLiftSpec liftSpec) {
        this(depth, null, index, name, layers, liftSpec);
    }

    public ImView(int depth, Integer initialAngle, int index, String name, List<ImLayer> layers, @Nullable ViewLiftSpec liftSpec) {
        super(depth);

        if (name == null) throw new IllegalArgumentException();
        if (layers == null) throw new IllegalArgumentException();

        if (name.equals("name")) {
            throw new IllegalArgumentException();

        }

        this.name = name;
        this.index = index;

        Collections.sort(layers);
        this.layers = layers;
        this.liftSpec = liftSpec;

        for (ImLayer layer : layers) {
            layer.initParent(this);
        }

        angleCount = getMaxAngle(layers);

        log.info("angleCount for view[" + getName() + "] is [" + angleCount + "]");

        this.initialAngle = getInitialAngle(initialAngle, name);

    }

    private static int getInitialAngle(Integer initialAngle, String viewName) {
        if (initialAngle != null) {
            return initialAngle;
        } else {
            if (viewName.equals("exterior")) {
                return 2;
            } else if (viewName.equals("interior")) {
                return 1;
            } else {
                return 1;
            }
        }
    }

    private static int getMaxAngle(List<ImLayer> layers) {
        int maxAngle = 0;
        for (int i = 0; i < layers.size() && i < 5; i++) {
            ImLayer layer = layers.get(i);
            int angle = layer.getMaxAngle();
            if (angle > maxAngle) {
                maxAngle = angle;
            }
        }
        return maxAngle;
    }

    @Override
    public void initParent(IsParent parent) {
        super.initParent(parent);
//        viewKeyOld = initViewKeyOld();
        viewKey = initViewKey();
        ImmutableList.Builder<ViewSlice> builder = ImmutableList.builder();
        for (int angle = 1; angle <= angleCount; angle++) {
            builder.add(new ViewSlice2(this, angle));
        }

    }


//    private ViewKeyOld initViewKeyOld() {
//        return ((ImSeries) parent).getSeriesInfo().getViewKeyByName(name);
//    }

    private ViewKey initViewKey() {
        ImageModel imageModel = (ImageModel) parent;
        SeriesKey seriesKey = imageModel.getKey();
        return new ViewKey(seriesKey, getIndex());
    }

//    public ViewKeyOld getViewKeyOld() {
//        return viewKeyOld;
//    }

    public ViewKey getViewKey() {
        return viewKey;
    }

//    public int getInitialAngle() {
//        return viewKeyOld.getInitialAngle();
//    }


    public Integer getInitialAngle() {
        return initialAngle;
    }

    public AngleKey getInitialAngleKey() {
        return new AngleKey(getViewKey(), initialAngle);
    }

    @Override
    public boolean isView() {
        return true;
    }

    @Override
    public boolean containsAngle(int angle) {
        if (layers == null) return false;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).containsAngle(angle)) return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public List<ImLayer> getLayers() {
        return layers;
    }

    public List<ImLayer> getLayers(int angle) {
        ArrayList<ImLayer> a = new ArrayList<ImLayer>();
        for (ImLayer layer : layers) {
            if (layer.containsAngle(angle)) {
                a.add(layer);
            }
        }
        return a;
    }

    public CacheAheadPolicy getCacheAheadPolicy() {
        if (isExterior()) {
            return new NumAnglesBothEitherSide(this, 3);
        } else {
            return new AllAngles(this);
        }
    }

    public boolean isExterior() {
        return name.equals("exterior");
    }

    public boolean isInterior() {
        return name.equals("interior");
    }

    public RawBaseImage getPngSegments(SimplePicks picks, int angle) {
        ImmutableList.Builder<PngSegment> pngs = ImmutableList.builder();
        ImmutableList<PngSpec> pngSpecs = getPngSpecs(picks, angle);
        for (PngSpec pngSpec : pngSpecs) {
            PngSegment pngSegment = pngSpec.getKey();
            pngs.add(pngSegment);
        }
        return new RawBaseImage(pngs.build());
    }

    public ImmutableList<PngSpec> getPngSpecs(SimplePicks picks, int angle) {
        Preconditions.checkNotNull(picks);
        Preconditions.checkArgument(angle > 0, "Angle must be greater than zero for " + getName() + " view");
        int angleCount = getAngleCount();
        Preconditions.checkArgument(angle <= angleCount, "Angle must <= " + angleCount + " for " + getName() + " view");

        ImmutableList.Builder<PngSpec> pngs = ImmutableList.builder();
        for (ImLayer layer : layers) {
            PngSpec png = layer.getPngSpec(picks, angle);
            if (png != null) {
                pngs.add(png);
            }
        }
        return pngs.build();

    }

    public RawImageStack getRawImageStack(RawImageStack.Key spec) {
        ImmutableList<PngSpec> srcPngs = getPngSpecs(spec.getPicks(), spec.getAngle());
        return new RawImageStack(spec, this, srcPngs);

    }

    public RawImageStack getRawImageStack(SimplePicks picks, int angle) {
        if (picks.isValidBuild()) {
            ImmutableList<PngSpec> srcPngs = getPngSpecs(picks, angle);
            return new RawImageStack(picks, angle, this, srcPngs);
        } else {
            return new RawImageStack(picks, angle, this, ImmutableList.<PngSpec>of());
        }
    }

    public CoreImageStack getCoreImageStack(CoreImageStack.Key spec) {
        RawImageStack rawImageStack = getRawImageStack(spec.getRawKey());
        return new CoreImageStack(rawImageStack, spec.getProfile(), spec.getImageMode());
    }


    public boolean is(ViewKeyOld viewKey) {
        return is(viewKey.getName());
    }

    public boolean is(String viewName) {
        return this.name.equalsIgnoreCase(viewName);
    }

    public int getLayerCount() {
        if (layers == null) return 0;
        return layers.size();
    }

    @Override
    public List<ImLayer> getChildNodes() {
        return layers;
    }

    public Set<Object> getPngVars() {
        HashSet<Object> vars = new HashSet<Object>();
        getPngVars(vars);
        return vars;
    }

    public void getPngVars(Set<Object> varSet) {
        for (int i = 0; i < layers.size(); i++) {
            ImLayer imLayer = layers.get(i);
            if (imLayer.isZLayer()) continue;
            imLayer.getVars(varSet);
        }
    }

    public Set<Var> getPngVars(int angle) {
        HashSet<Var> vars = new HashSet<Var>();

        if (liftSpec != null) {
            Var triggerFeature = liftSpec.getTriggerFeature();
            vars.add(triggerFeature);
        }

        getPngVars(vars, angle);
        return vars;
    }

    public void getPngVars(Set<Var> varSet, int angle) {
        for (int i = 0; i < layers.size(); i++) {
            ImLayer imLayer = layers.get(i);
            if (imLayer.isZLayer()) continue;
            imLayer.getVars(varSet, angle);
        }
    }

    public Set<SrcPng> getAllSourcePngs() {
        HashSet<SrcPng> set = new HashSet<SrcPng>();
        populatePngSet(set);
        return set;
    }

    public void populatePngSet(Set<SrcPng> pngs) {
        for (ImLayer imLayer : layers) {
            imLayer.getPngs(pngs);
        }
    }

    public String getVarCode() {
        return getName() + "View";
    }

    public int getAngleCount() {
        return angleCount;
    }


    public ImageModel getSeries() {
        return getParent().asSeries();
    }

    public String getSeriesName() {
        return getSeries().getName();
    }


    public ViewSlice getViewSlice(int angle) {
        return new ViewSlice2(this, angle);
    }

    public void printSummary() {
        System.out.println("\t " + getName() + "\tLayers[ " + layers.size() + "]\t vars[" + getPngVars().size() + "]");
    }

    public Slice getSlice(int angle) {
        return new Slice(getName(), angle);
    }

    public Slice2 getSlice2(int angle) {
        return new Slice2(this, angle);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != ImView.class) return false;

        ImView that = (ImView) obj;

        boolean vkEq = (name.equals(that.name));

        boolean layersEq = (layers.equals(that.layers));

        return vkEq && layersEq;
    }

//    public String getJpgFingerprint(ImmutableList<PngSpec> pngs) {
//        return Jpg.getFingerprint(pngs);
//    }


//    public Path getJpgUrl(ImmutableList<ImPng> pngs, JpgWidth jpgWidth) {
//        String fp = getJpgFingerprint(pngs);
//        Path threedBaseJpgUrl = getSeries().getThreedBaseJpgUrl(jpgWidth);
//        return threedBaseJpgUrl.append(fp).appendName(".jpg");
//    }

    public int getNext(int currentAngle) {
        boolean isLast = currentAngle == angleCount;
        int retVal;
        if (isLast) {
            retVal = 1;
        } else retVal = currentAngle + 1;
        return retVal;
    }

    private static Logger log = Logger.getLogger(ImView.class.getName());

    public int getPrevious(int currentAngle) {
        boolean isFirst = currentAngle == 1;
        int retVal;
        if (isFirst) retVal = angleCount;
        else retVal = currentAngle - 1;
        return retVal;
    }

//    public int getPrevious(int currentAngle) {
//        return getViewKeyOld().getPrevious(currentAngle);
//    }

    public int getPrevious(int currentAngle, int count) {
        if (currentAngle == 0) throw new IllegalStateException();
        int cur = currentAngle;
        for (int i = 0; i < count; i++) {
            cur = getPrevious(cur);
        }
        if (cur == 0) throw new IllegalStateException();
        return cur;
    }

    public int getNext(int currentAngle, int count) {
        if (currentAngle == 0) throw new IllegalStateException();
        int cur = currentAngle;
        for (int i = 0; i < count; i++) {
            cur = getNext(cur);
        }
        if (cur == 0) throw new IllegalStateException();
        return cur;
    }

    public boolean isDragToSpin() {
        return this.angleCount > 1;
//        return isExterior();
    }

    public ViewLiftSpec getLiftSpec() {
        return liftSpec;
    }

    public List<Integer> getAngles() {
        ArrayList<Integer> angles = new ArrayList<Integer>();
        for (int a = 1; a <= getAngleCount(); a++) {
            angles.add(a);
        }
        return angles;
    }

    public String getLabel() {
        return Strings.capFirstLetter(name);
    }
}
