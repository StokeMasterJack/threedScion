package com.tms.threed.threedFramework.featureModel.server;

import com.google.common.base.Preconditions;
import com.tms.threed.threedFramework.featureModel.shared.Cardinality;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.VarCodeFixer;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Conflict;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Iff;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Imp;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;
import static com.tms.threed.threedFramework.util.lang.shared.Strings.notEmpty;

/**
 * Not thread safe. Use and throw away
 */
public class FeatureModelBuilderXml {

    public static final String DEFAULT_VALUE = "defaultValue";  //  true/[false]
    public static final String MANDATORY = "mandatory";         //  true/[false]
    public static final String PICK = "pick";                   //  1,0-1,all
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String INCLUDES = "includes";
    public static final String CONFLICTS = "conflicts";
    public static final String IFFS = "iffs";
    public static final String DERIVED = "derived";
    private static final String VERSION = "version";
    private static final String MODEL_TAG_NAME = "model";
    private static final String FEATURE_TAG_NAME = "feature";
    private static final String FEATURES_TAG_NAME = FEATURE_TAG_NAME + "s";


    private SeriesKey seriesKey;
    private String seriesDisplayName;
    private int seriesYear;
    private Element featuresElement;

    private FeatureModel fm;

    //    private File modelPngRoot;


//    private File xmlFile;


    private Path modelPngRoot;


    private Map<String, BoolExprString> includeMap = new HashMap<String, BoolExprString>();

    private Map<String, BoolExprString> iffMap = new HashMap<String, BoolExprString>();
    private Map<String, BoolExprString> conflictMap = new HashMap<String, BoolExprString>();

    public static FeatureModel create(@Nonnull SeriesKey seriesKey, @Nonnull Element featuresElement) {
        return create(seriesKey, seriesKey.getName(), seriesKey.getYear(), featuresElement);
    }

    public static FeatureModel create(@Nonnull SeriesKey seriesKey, @Nonnull String seriesDisplayName, int seriesYear, @Nonnull Element featuresElement) {
        return new FeatureModelBuilderXml().buildModel(seriesKey, seriesDisplayName, seriesYear, featuresElement);
    }

    private FeatureModel buildModel(SeriesKey seriesKey, String seriesDisplayName, int seriesYear, Element featuresElement) {
        //log.debug("creating FeatureModel from XML Document");
        this.seriesKey = seriesKey;
        this.seriesDisplayName = seriesDisplayName;
        this.seriesYear = seriesYear;
        this.featuresElement = featuresElement;
        processFeaturesElement();
        fm.performSemiHumanFixup();
        return fm;
    }

    private void processFeaturesElement() {
        Preconditions.checkNotNull(featuresElement);


        fm = new FeatureModel(seriesKey, seriesDisplayName); //todo

        Var rootVar = fm.getRootVar();
        processFeatureChildren(rootVar, featuresElement);
        processIncludes();
        processIffs();
        processConflicts();
    }

    private void processFeature(Var parentVar, Element childElement) {
        final boolean isSol = isSol(childElement);
//        System.out.println("processFeature: ");
//        System.out.println("\t parentVar: " + parentVar);
//        System.out.println("\t childElement: " + childElement.getName() + "." + childElement.attributeValue("code") + " sol: " + isSol);

        if (isSol) {
            processSolitaryFeature(parentVar, childElement);
        } else {
            processFeatureGroup(parentVar, childElement);
        }
    }

    private boolean isSol(Element childElement) {
        return childElement.getName().equals(FEATURE_TAG_NAME);
    }

    private void processIncludes() {
        for (String varCode : includeMap.keySet()) {
            BoolExprString boolExprString = includeMap.get(varCode);
            processInclude(varCode, boolExprString);
        }
    }

    private void processIffs() {
        for (String varCode : iffMap.keySet()) {
            BoolExprString iffs = iffMap.get(varCode);
            processIff(varCode, iffs);
        }
    }

    private void processConflicts() {
        for (String varCode : conflictMap.keySet()) {
            BoolExprString conflicts = conflictMap.get(varCode);
            processConflict(varCode, conflicts);
        }
    }

    private void processInclude(String varCode, BoolExprString exprString) {
        Preconditions.checkNotNull(varCode);
        Preconditions.checkNotNull(exprString);

        ExprParser exprParser = new ExprParser(fm);
        final Imp implication = exprParser.createImplication(varCode, exprString, fm);
        fm.addConstraint(implication);
    }

    private void processIff(String varCode, BoolExprString exprString) {
        Preconditions.checkNotNull(varCode);
        Preconditions.checkNotNull(exprString);

        ExprParser exprParser = new ExprParser(fm);
        final Iff iff = exprParser.createIff(varCode, exprString, fm);
        fm.addConstraint(iff);
    }

    private void processConflict(String varCode, BoolExprString exprString) {
        Preconditions.checkNotNull(varCode);
        Preconditions.checkNotNull(exprString);
        ExprParser exprParser = new ExprParser(fm);
        final Conflict conflict = exprParser.createConflict(varCode, exprString, fm);
        fm.addConstraint(conflict);
    }

    private void processFeatureGroup(Var parentVar, Element childElement) {
        String varCode = childElement.getName();
        varCode = VarCodeFixer.fixupVarCode(varCode, parentVar.getCode());
        final Var childVar = parentVar.addChild(varCode);
        common(childVar, childElement);
        processFeatureChildren(childVar, childElement);
    }

    private void processFeatureChildren(Var parentVar, Element parentElement) {
        List<Element> childElements = parentElement.elements();
        for (Element childElement : childElements) {
            processFeature(parentVar, childElement);
        }
    }

    private Boolean isDerived(Element e) {
        String derived = e.attributeValue(DERIVED);
        if (isEmpty(derived)) return null;
        return derived.trim().equalsIgnoreCase("true");
    }

    private Cardinality getCardinality(Element e) {
        String pick = e.attributeValue(PICK);
        if (isEmpty(pick)) return null;
        pick = pick.trim();
        if (pick.equals("1")) return Cardinality.PickOneGroup;
        else if (pick.equals("0-1")) return Cardinality.ZeroOrOneGroup;
        else if (pick.equals("all")) return Cardinality.AllGroup;
        throw new IllegalStateException("Unsupported pick value (cardinality): [" + pick + "] ");
    }

    private Boolean getMandatory(Element e) {
        String mandatory = e.attributeValue(MANDATORY);
        if (isEmpty(mandatory)) {
            return null;
        } else {
            mandatory = mandatory.trim();
        }
        if (mandatory.equalsIgnoreCase("false")) return null;
        if (mandatory.equalsIgnoreCase("true")) return true;
        return null;
    }


    private Boolean getDefaultValue(Element e) {
        String defaultValue = e.attributeValue(DEFAULT_VALUE);
        if (isEmpty(defaultValue)) {
            return null;
        }
        return defaultValue.trim().equalsIgnoreCase("true");
    }

    private void common(Var var, Element e) {
        assert var != null;
        assert e != null;
        var.setDefaultValue(getDefaultValue(e));
        var.setDerived(isDerived(e));
        var.setCardinality(getCardinality(e));
        var.setMandatory(getMandatory(e));
    }

    private void processSolitaryFeature(Var parentVar, Element childElement) {
        String varCode = childElement.attributeValue(CODE);
        String varName = childElement.attributeValue(NAME);
        String iffs = childElement.attributeValue(IFFS);
        String includes = childElement.attributeValue(INCLUDES);
        String conflicts = childElement.attributeValue(CONFLICTS);


        if (isEmpty(varCode)) {
            String msg = "FeatureElement [" + childElement.getPath() + "] is missing required attribute 'code'";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        varCode = VarCodeFixer.fixupVarCode(varCode, parentVar.getCode());

        if (isEmpty(varName)) {
            varName = varCode;
        }


        try {
            Var newChildVar = parentVar.addChild(varCode, varName);
            common(newChildVar, childElement);
        } catch (Exception e1) {
            throw new RuntimeException("Problem adding element [" + childElement.asXML() + "] to parent [" + parentVar + "]", e1);
        }

        //process constraints
        if (notEmpty(includes)) {
            String source = "XML file, the includes attribute of feature tag: \n=========\n" + childElement.toString() + "===========";
            BoolExprString exprString = new BoolExprString(includes, source, BoolExprString.Op.AND);
            includeMap.put(varCode, exprString);
        }


        if (notEmpty(iffs)) {
            String source = "XML file, the iff attribute of feature tag: \n=========\n" + childElement.toString() + "===========";
            BoolExprString exprString = new BoolExprString(iffs, source, BoolExprString.Op.AND);
            iffMap.put(varCode, exprString);
        }

        if (notEmpty(conflicts)) {
            String source = "XML file, the conflicts attribute of feature tag: \n=========\n" + childElement.toString() + "===========";
            BoolExprString exprString = new BoolExprString(conflicts, source, BoolExprString.Op.OR);
            conflictMap.put(varCode, exprString);
        }

    }


    private static final Log log = LogFactory.getLog(FeatureModelBuilderXml.class);

}