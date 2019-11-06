package com.github.mtdp.quick;

import com.github.mtdp.quick.req.GenerateRequest;
/**
 * 
 *
 * @Description TODO
 * @author wangguoqing
 * @date 2019年11月5日下午6:36:20
 *
 */
public interface GenerateService {
	
	/**
	 * <pre>
	 * 目录结构
	 * demo
	 * 	|-demo-api
	 * 		|-src
	 * 		   |-main/java/${package}
	 * 		   |-test/java/${package}
	 * 	|-demo-rt
	 * 		|-src
	 * 		   |-main
	 * 			  |-java/${package}
	 * 			  |-resources
	 * 				   |-config
	 * 				   |-mybatis
	 * 						|-mybatis-config.xml
	 * 				   |-spring
	 * 						|-spring-application.xml
	 *				   |-logback.xml
	 * 		   |-test/java/${package}
	 * 
	 * 创建目录
	 * 创建默认文件
	 * 生成domian,dao接口及文件
	 * </pre>
	 * @param req
	 */
	public boolean generate(GenerateRequest req);
	
}
