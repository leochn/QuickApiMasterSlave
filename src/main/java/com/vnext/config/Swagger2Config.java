package com.vnext.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vnext.core.ProjectConstant;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 使用Swagger2生成api文档,生产环境注释掉
 * @author leo
 *
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
	
	@Bean
    public Docket buildDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(buildApiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage(ProjectConstant.CONTROLLER_PACKAGE))  //要扫描的API(Controller)基础包
				.paths(PathSelectors.any())
				.build();
    }
	
	/**
	 * 这里是生成文档基本信息的地方 
	 * @return
	 */
	private ApiInfo buildApiInfo() {
		return new ApiInfoBuilder()
				.title("Spring Boot中使用Swagger2-UI构建API文档")
				.description("自动生成代码API")
				.version("1.0")
				.build();
    }

}
