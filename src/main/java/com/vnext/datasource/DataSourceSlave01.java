package com.vnext.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jdbc.slave01")
public class DataSourceSlave01 extends DataSourceProperties{
    
}
