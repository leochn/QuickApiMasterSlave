package com.vnext.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@EnableConfigurationProperties({DataSourceMaster.class,DataSourceSlave01.class,DataSourceSlave02.class})
@ConditionalOnClass(DruidDataSource.class)
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DataSourceConfig {
	
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
	
	/**
	 * 获取主库
	 * @return
	 */
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
    @Primary
    public DataSource dataSourcePlus() {
    	DynamicDataSource01 dataSourcePlus = new DynamicDataSource01();
    	Map<Object, Object> map = new HashMap<Object,Object>();
    	map.put("master", masterDataSource());
    	map.put("slave01", slave01DataSource());
    	dataSourcePlus.setTargetDataSources(map);
    	dataSourcePlus.setDefaultTargetDataSource(masterDataSource());
    	return dataSourcePlus;
    }
	
	
	
/*	
    private static final String DATASOURCE_WRITE = "write";

    private static final String DATASOURCE_READ1 = "read1";

    private static final String DATASOURCE_READ2 = "read2";*/
    
/*    @Bean
    @Primary
    public DataSource dynamicDataSource() {
    	DynamicDataSource01 dynamicDataSource = new DynamicDataSource01();
        return dynamicDataSource;
    }*/
    
  /*  @Bean(DATASOURCE_WRITE)
    @ConfigurationProperties(prefix = "datasource.write")
    public DataSource WriteDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(DATASOURCE_READ1)
    @ConfigurationProperties(prefix = "datasource.read1")
    public DataSource Read1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(DATASOURCE_READ2)
    @ConfigurationProperties(prefix = "datasource.read2")
    public DataSource Read2DataSource() {
        return DataSourceBuilder.create().build();
    }*/

    @Bean
    public WRInterceptor mapperInterceptor() {
        return new WRInterceptor();
    }
    
    public static class DynamicDataSource01 extends AbstractRoutingDataSource implements BeanFactoryAware {

        private static ThreadLocal<String> currentLookupKeyLocal = new ThreadLocal<String>();
        private BeanFactory beanFactory;

        public static String getCurrentLookupKey() {
            return currentLookupKeyLocal.get();
        }

        public static void setCurrentLookupKey(String key) {
            currentLookupKeyLocal.set(key);
        }

        @Override
        protected Object determineCurrentLookupKey() {
            return currentLookupKeyLocal.get();
        }

        @Override
        public void afterPropertiesSet() {
            //
            Map<String, DataSource> targetDataSources = BeanFactoryUtils
                    .beansOfTypeIncludingAncestors((ListableBeanFactory) beanFactory, DataSource.class);
            super.setTargetDataSources(new HashMap<Object, Object>(targetDataSources));
            //
            super.afterPropertiesSet();
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }
    
    @Aspect
    private static class WRInterceptor {

        @Around("execution(* cc.qianglovepei.service..*.*(..))")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            // 获取到当前执行的方法名
            String methodName = joinPoint.getSignature().getName();
            if (isSlave(methodName)) {
                // 标记为读库,可以自定义选择数据源
                Random random = new Random();
                int randomNum = random.nextInt(3);
                if (randomNum == 0) {
                    DynamicDataSource01.setCurrentLookupKey("slave01");
                } else {
                    DynamicDataSource01.setCurrentLookupKey("slave01");
                }
            } else {
                // 标记为写库
                DynamicDataSource01.setCurrentLookupKey("master");
            }
            return joinPoint.proceed();
        }

        /**
         * 判断是否为读库
         *
         * @param methodName
         * @return
         */
        private Boolean isSlave(String methodName) {
            // 方法名以query、find、get开头的方法名走从库
            return StringUtils.startsWithAny(methodName, new String[] { "query", "find", "get" });
        }

    }
}
