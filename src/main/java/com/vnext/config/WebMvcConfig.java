package com.vnext.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.vnext.core.ProjectConstant;
/*import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;*/
import com.vnext.core.Result;
import com.vnext.core.ResultCode;
import com.vnext.core.ServiceException;
import com.vnext.utils.JwtUtil;

/**
 * Spring MVC 配置
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Value("${spring.profiles.active}")
    private String environment;     //当前激活的配置文件

    //使用阿里 FastJson 作为JSON MessageConverter
    /*@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue,  //保留空的字段
                SerializerFeature.WriteNullStringAsEmpty,                  //String null -> ""
                SerializerFeature.WriteNullNumberAsZero);                  //Number null -> 0
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(converter);
    }*/

    //统一异常处理
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new HandlerExceptionResolver() {
            public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
                Result result = new Result();
                if (e instanceof ServiceException) {     //业务失败的异常，如“账号或密码错误”
                    result.setCode(ResultCode.FAIL).setMessage(e.getMessage());
                    logger.info(e.getMessage());
                } else if (e instanceof NoHandlerFoundException) {
                    result.setCode(ResultCode.NOT_FOUND).setMessage("接口 [" + request.getRequestURI() + "] 不存在");
                } else if (e instanceof ServletException) {
                    result.setCode(ResultCode.FAIL).setMessage(e.getMessage());
                } else {
                    result.setCode(ResultCode.INTERNAL_SERVER_ERROR).setMessage("接口 [" + request.getRequestURI() + "] 内部错误，请联系管理员");
                    String message;
                    if (handler instanceof HandlerMethod) {
                        HandlerMethod handlerMethod = (HandlerMethod) handler;
                        message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                                request.getRequestURI(),
                                handlerMethod.getBean().getClass().getName(),
                                handlerMethod.getMethod().getName(),
                                e.getMessage());
                    } else {
                        message = e.getMessage();
                    }
                    logger.error(message, e);
                }
                responseResult(response, result);
                return new ModelAndView();
            }

        });
    }

    //解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //registry.addMapping("/**");
    }

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!"dev".equals(environment)) { //开发环境忽略签名认证
            registry.addInterceptor(new HandlerInterceptorAdapter() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    logger.info("请求接口：{}，请求IP：{}，请求参数：{}",request.getRequestURI(), getIpAddress(request), JSON.toJSONString(request.getParameterMap()));
                    // 验证签名,并把登录用户的信息保存在本地线程 UserThreadLocal 
            		// UserThreadLocal.set(null);
            		String authorization = request.getHeader(ProjectConstant.AUTHORIZATION);
            		// ???
            		if (request.getMethod().equals("OPTIONS")) {
            			return true;
            		}
            		boolean flag = false;
                    if (StringUtils.isNotBlank(authorization)) {
                        // 验证时间是否过期,及签名是否正确.
                    	flag = JwtUtil.isJwtValid(authorization);
                    	if (flag) {
                    		// 时间没有过期,继续验证用户是否存在
                    		/*SysUser sysUser = JwtUtil.getSysUser(authorization);
                    		if (sysUser != null) {
                    			SysUser user = sysUserService.queryById(sysUser.getId());
                    			if (user != null) {
                    				// 用户信息写入本地线程
                    				UserThreadLocal.set(user); 
                    				flag = sysUser.getLoginName().equals(user.getLoginName());
                    			}
                    		}*/
            			}
                    }

                    if (!flag) {
                        //response.setStatus(HttpStatus.FORBIDDEN.value());
                        /*response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().print(ProjectConstant.ACCESSTOKENERROR);*/
                        Result result = new Result();
                        result.setCode(ResultCode.UNAUTHORIZED).setMessage("签名认证失败");
                        responseResult(response, result);
                    }
                    
                    if (flag) {
						// 进行api接口访问权限验证.
                    	// request.getRequestURI();
					}
                    
                    return flag;
                }
            }).addPathPatterns("/api/**").excludePathPatterns("/rest/**");
        }
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }
}
