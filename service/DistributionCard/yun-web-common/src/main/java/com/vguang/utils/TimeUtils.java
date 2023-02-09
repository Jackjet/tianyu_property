package com.vguang.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeUtils {
        public static String Time(Timestamp s){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(s);
            return format;
        }
    }

