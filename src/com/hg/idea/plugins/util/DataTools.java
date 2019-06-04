package com.hg.idea.plugins.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTools {
    public static String getDateNowThroughDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(date);
    }
}
