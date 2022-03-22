package com.laicr.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class CheckTailNoUtil {

    public static boolean checkTailNo(String target, Set<String> tailNo) {
        if (!StringUtils.isNumeric(target) || CollectionUtils.isEmpty(tailNo)) {
            return false;
        }
        char tail = target.charAt(target.length() - 1);
        return tailNo.contains(tail+"");
    }

    public static void main(String[] a){
        Set<String> tailNo= Sets.newHashSet("1","2","5");
        System.out.println(CheckTailNoUtil.checkTailNo("11115",tailNo));
    }
}
