package com.tms.threed.toyota.ebro;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

/**
 * Translates picks from http request to feature codes, the format required for feature model and threed
 */
public class EBrochureRequest {


    private final HttpServletRequest request;

    private final String modelYear;
    private final String modelCode;
    private final String interiorColorCode;
    private final String exteriorColorCode;
    private final String packageCodes;
    private final String accessoryCodes;

    public EBrochureRequest(HttpServletRequest request) {
        this.request = request;

        this.modelCode = request.getParameter("mc");
        this.modelYear = request.getParameter("my");
        this.interiorColorCode = request.getParameter("int");
        this.exteriorColorCode = request.getParameter("ext");
        this.packageCodes = request.getParameter("pkg");
        this.accessoryCodes = request.getParameter("commaDelimitedAccessoryCodes");

    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ServletContext getServletContext(){
        return request.getSession().getServletContext();
    }

    /*
   private final String modelYear = "2010";
   private final String modelCode = "5338";
   private final String interiorColorCode = "FA12";
   private final String exteriorColorCode = "01F7";
   private final String packageCodes = "CQCFFELSSASR";
   private final String accessoryCodes = "EF,WL,DI04,DI05,";
    */




    public String getModelYear() {
        return modelYear;
    }

    public LinkedHashSet<String> getFeatureCodes() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        set.add(getModelCode());
        set.add(getInteriorColorCode());
        set.add(getExteriorColorCode());
        set.addAll(getPackageCodes());
        set.addAll(getAccessoryCodes());
        return set;

//        if (isPresent(cabCode)) set.add(cabCode);
//        if (isPresent(bedCode)) set.add(bedCode);
//        if (isPresent(driveCode)) set.add(driveCode);
//        if (isPresent(engineCode)) set.add(engineCode);
//        if (isPresent(gradeCode)) set.add(gradeCode);
//        if (isPresent(transmissionCode)) set.add(transmissionCode);


    }

    private static boolean isPresent(String s) {
        if (s == null) return false;
        if (s.trim().equals("")) return false;
        return true;
    }

    public String getModelCode() {
        return modelCode;
    }

    public String getExteriorColorCode() {
        if (exteriorColorCode.length() == 4 && exteriorColorCode.startsWith("0")) {
            return exteriorColorCode.substring(1);
        } else {
            return exteriorColorCode;
        }
    }

    public String getInteriorColorCode() {
        return interiorColorCode;
    }


    public Set<String> getPackageCodes() {
        final HashSet<String> set = new HashSet<String>();
        if (isEmpty(packageCodes)) return set;


        if ((packageCodes.trim().length() % 2) != 0)
            throw new IllegalStateException("packageCodes.length must be even");
        StringBuffer sb = new StringBuffer(packageCodes.trim());
        do {
            final String s = sb.substring(0, 2);
            set.add(s);
            sb.delete(0, 2);
        } while (sb.length() >= 2);

        return set;

    }

    public Set<String> getAccessoryCodes() {
        final HashSet<String> set = new HashSet<String>();
        if (isEmpty(accessoryCodes)) return set;

        final String[] a = accessoryCodes.trim().split(",");
        for (String s : a) {
            set.add(s);
        }
        return set;

    }




}
