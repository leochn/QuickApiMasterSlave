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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
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
	
	private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
	
	@Autowired
    private DataSourceMaster dataSourceMaster;
	
	@Autowired
	private DataSourceSlave01 dataSourceSlave01;
	
	@Autowired
	private DataSourceSlave02 dataSourceSlave02;
	
	private static final String DATASOURCE_MASTER = "master";
    private static final String DATASOURCE_SLAVE01 = "slave01";
    private static final String DATASOURCE_SLAVE02 = "slave02";
	
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
    	DynamicDataSource dataSourcePlus = new DynamicDataSource();
    	Map<Object, Object> map = new HashMap<Object,Object>();
		map.put(DATASOURCE_MASTER, masterDataSource());
		map.put(DATASOURCE_SLAVE01, slave01DataSource());
		map.put(DATASOURCE_SLAVE02, slave02DataSource());
    	dataSourcePlus.setTargetDataSources(map);                       // 设置多个数据源
    	dataSourcePlus.setDefaultTargetDataSource(masterDataSource());  // 设置默认的数据源,这里默认走写库
    	return dataSourcePlus;
    }
	
    @Bean
    public WRInterceptor mapperInterceptor() {
        return new WRInterceptor();
    }
    
    public static class DynamicDataSource extends AbstractRoutingDataSource implements BeanFactoryAware {

        private static ThreadLocal<String> currentLookupKeyLocal = new ThreadLocal<String>();

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
		public void setBeanFactory(BeanFactory arg0) throws BeansException {
			// TODO Auto-generated method stub
		}
    }
    
    @Aspect
    private static class WRInterceptor {
        @Around("execution(* com.vnext.service..*.*(..))")
        public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
            // 获取到当前执行的方法名
            String methodName = joinPoint.getSignature().getName();
            if (isSlave(methodName)) {
                // 标记为读库,可以自定义选择数据源,设置读库的策略
                Random random = new Random();
                int randomNum = random.nextInt(2);
                if (randomNum == 0) {
                    DynamicDataSource.setCurrentLookupKey(DATASOURCE_SLAVE01);
                } else {
                    DynamicDataSource.setCurrentLookupKey(DATASOURCE_SLAVE02);
                }
            } else {
                // 标记为写库
                DynamicDataSource.setCurrentLookupKey(DATASOURCE_MASTER);
            }
            logger.info("调用 Service的方法是:[" + methodName + "],数据源是:[" + DynamicDataSource.getCurrentLookupKey() +"]");
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
