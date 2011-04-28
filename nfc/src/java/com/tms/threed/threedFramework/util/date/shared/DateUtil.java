package com.tms.threed.threedFramework.util.date.shared;

import java.util.Date;

@SuppressWarnings({"deprecation"})
public class DateUtil {

    public static boolean dateCompare(Date d1, Date d2){
        if(d1==null || d2==null) return false;
        if(d1.getDate()!=d2.getDate())return false;
        if(d1.getMonth()!=d2.getMonth())return false;
        if(d1.getYear()!=d2.getYear())return false;
        return true;
    }


}
