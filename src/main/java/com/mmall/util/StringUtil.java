package com.mmall.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {
    public  static List<Integer> splitToListInt(String s){
          /* String[] ss= s.split(",");*/
       List<String> strList=Splitter.on(",").trimResults().omitEmptyStrings().splitToList(s);
       return strList.stream().map(x->Integer.parseInt(x)).collect(Collectors.toList());
    }
}
