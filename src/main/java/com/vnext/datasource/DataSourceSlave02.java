package com.vnext.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jdbc.slave02")
public class DataSourceSlave02 extends DataSourceProperties{
	
}
