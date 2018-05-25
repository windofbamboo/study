package com.common.util;

import java.util.ArrayList;
import java.util.List;

public class PathUtil {

    public static final String SEPERATOR = "/";

    public static String full_path(String parent, String name) {
        return normalize_path(parent + SEPERATOR + name);
    }

    public static String normalize_path(String path) {
        String rtn = toks_to_path(tokenize_path(path));
        return rtn;
    }

    public static List<String> tokenize_path(String path) {
        String[] toks = path.split(SEPERATOR);
        java.util.ArrayList<String> rtn = new ArrayList<String>();
        for (String str : toks) {
            if (!str.isEmpty()) {
                rtn.add(str);
            }
        }
        return rtn;
    }

    public static String toks_to_path(List<String> toks) {
        StringBuffer buff = new StringBuffer();
        buff.append(SEPERATOR);
        int size = toks.size();
        for (int i = 0; i < size; i++) {
            buff.append(toks.get(i));
            if (i < (size - 1)) {
                buff.append(SEPERATOR);
            }

        }
        return buff.toString();
    }
}
