package com.ke.schedule.basic.support;

import com.alibaba.fastjson.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @Author: zhaoyuguang
 * @Date: 2018/7/31 下午7:59
 */

public class KobUtils {

    private static String charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    public static boolean isEmpty(Integer i) {
        return i == null;
    }

    public static boolean isEmpty(Long l) {
        return l == null;
    }

    public static boolean isEmpty(Boolean b) {
        return b == null;
    }

    public static String digits62system(Integer int10) {
        return String.valueOf(charSet.toCharArray()[int10]);
    }

    public static int digits62system(String str62) {
        return charSet.indexOf(str62);
    }

    public static boolean isJson(String text) {
        try {
            JSONObject.parseObject(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Date addMin(Date date, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    public static Date addHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    public static Date initDateByDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static java.lang.String exception2String(Exception e) {
        String string = "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.getBuffer().toString();
            sw.close();
            pw.close();
        } catch (Exception ex) {
        }
        return string;
    }
}
