/*package com.vnext.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableConfigurationProperties({DataSourceMaster.class,DataSourceSlave01.class,DataSourceSlave02.class})
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DataSourceConfiguration {
	
	@Autowired
    private DataSourceMaster dataSourceMaster;
	
	@Autowired
	private DataSourceSlave01 dataSourceSlave01;
	
	@Autowired
	private DataSourceSlave02 dataSourceSlave02;
	
	private DruidDataSource getDataSource(DataSourceProperties properties) {
		DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        if (properties.getInitialSize() > 0) {
            dataSource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMinIdle() > 0) {
            dataSource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getMaxActive() > 0) {
            dataSource.setMaxActive(properties.getMaxActive());
        }
        dataSource.setTestOnBorrow(properties.isTestOnBorrow());
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dataSource;
	}
	
	*//**
	 * 获取主库
	 * @return
	 *//*
    public DruidDataSource masterDataSource() {
        return getDataSource(dataSourceMaster);
    }
	
    public DruidDataSource slave01DataSource() {
    	return getDataSource(dataSourceSlave01);
    }
    
    public DruidDataSource slave02DataSource() {
    	return getDataSource(dataSourceSlave02);
    }
    
    @Bean
    public DataSource dataSourcePlus() {
    	DynamicDataSource dataSourcePlus = new DynamicDataSource();
    	Map<Object, Object> map = new HashMap<Object,Object>();
    	map.put("master", masterDataSource());
    	map.put("slave01", slave01DataSource());
    	dataSourcePlus.setTargetDataSources(map);
    	dataSourcePlus.setDefaultTargetDataSource(masterDataSource());
    	return dataSourcePlus;
    }

}
*/