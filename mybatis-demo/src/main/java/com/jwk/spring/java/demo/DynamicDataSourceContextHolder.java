package com.jwk.spring.java.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>() {
        /**
                   * Use the key of the master data source as the default data source key
         */
        @Override
        protected String initialValue() {
            return "master";
        }
    };


    /**
           * The key collection of the data source, used to determine whether the data source exists when switching
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();

    /**
           * Switch data source
     * @param key
     */
    public static void setDataSourceKey(String key) {
        contextHolder.set(key);
    }

    /**
           * Get the data source
     * @return
     */
    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    /**
           * Reset data source
     */
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

    /**
           * Determine if the data source is included
     * @param key data source key
     * @return
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }
    
    /**
           * Add data source keys
     * @param keys
     * @return
     */
    public static boolean addDataSourceKeys(Collection<? extends Object> keys) {
        return dataSourceKeys.addAll(keys);
    }
}