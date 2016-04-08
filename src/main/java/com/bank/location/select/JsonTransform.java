package com.bank.location.select;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wang
 * @func 提供静态Gson, 程式格式转换type;
 */
public class JsonTransform {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping()
            .create();

    // format作为例子,在并发中使用并不合适;
    public static final SimpleDateFormat FORMAT1 = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final SimpleDateFormat FORMAT2 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static final Type LIST_TYPE1 = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    public static final Type LIST_TYPE2 = new TypeToken<Map<String, Object>>() {
    }.getType();
    public static final Type LIST_TYPE3 = new TypeToken<Set<String>>() {
    }.getType();
    public static final Type LIST_TYPE4 = new TypeToken<Map<String, Integer>>() {
    }.getType();
    public static final Type LIST_TYPE5 = new TypeToken<List<List<Map<String, Object>>>>() {
    }.getType();

    public static final Type LIST_TYPE6 = new TypeToken<Map<String, Long>>() {
    }.getType();

    public static final Type LIST_TYPE7 = new TypeToken<Map<String, Map<String, Long>>>() {
    }.getType();

    public static final Type LIST_TYPE8 = new TypeToken<List<String>>() {
    }.getType();
    public static final Type LIST_TYPE9 = new TypeToken<Byte[]>() {
    }.getType();
}
