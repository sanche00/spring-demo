package com.jwk.spring.java.demo;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    
    
    /**
           * If you don't want the data source to load when you start the configuration, you can customize this method to read and return the data source from whatever you want.
           * For example, reading data source information from a database, a file, an external interface, etc., and finally returning a DataSource implementation class object
     */
    @Override
    protected DataSource determineTargetDataSource() {
    	return super.determineTargetDataSource();	
    }
    
    /**
           * If you want all data sources to be loaded when the configuration is started, here to change the data by setting the data source Key value, customize this method
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }
    
    /**
           * Set default data source
     * @param defaultDataSource
     */
    public void setDefaultDataSource(Object defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
    }
    
    /**
           * Set the data source
     * @param dataSources
     */
    public void setDataSources(Map<Object, Object> dataSources) {
        super.setTargetDataSources(dataSources);
        DynamicDataSourceContextHolder.addDataSourceKeys(dataSources.keySet());
    }
    
//    public void addDataSources(String key, DataSource) {
//        super.setTargetDataSources(dataSources);
//        DynamicDataSourceContextHolder.addDataSourceKeys(dataSources.keySet());
//    }
    
}