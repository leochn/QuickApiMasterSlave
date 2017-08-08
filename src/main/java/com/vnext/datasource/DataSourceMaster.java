package com.vnext.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jdbc.master")
public class DataSourceMaster extends DataSourceProperties{
	    
}
