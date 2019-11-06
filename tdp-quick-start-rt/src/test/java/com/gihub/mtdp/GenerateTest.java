package com.gihub.mtdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mtdp.quick.req.GenerateRequest;
import com.github.mtdp.util.BeanUtil;
import com.github.mtdp.util.FileUtil;
import com.github.mtdp.util.VelocityUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @Description TODO
 * @author wangguoqing
 * @date 2019年9月7日下午1:54:25
 *
 */
@Slf4j
public class GenerateTest {
	
	private String apiModuleName;
	
	private String rtModuleName;
	
	private String rtSrcRes;
	private String rtSrcResConfig;
	private String rtSrcResSpring;
	private String rtSrcResMybatis;
	
	private static String apiPom = "template/api-pom.xml.vm";
	private static String gitignore = "template/gitignore.vm";
	private static String logback = "template/logback.xml.vm";
	private static String parentPom = "template/parent-pom.xml.vm";
	private static String rtPom = "template/rt-pom.xml.vm";
	private static String springApplication = "template/spring-application.xml.vm";
	private static String mybatisConfig = "template/mybatis-config.xml.vm";
	
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
	 * 			  |-webapp/WEB-INF
	 * 						  |-web.xml
	 * 						  |-index.jsp
	 * 		   |-test/java/${package}
	 * 
	 * 创建目录
	 * 创建默认文件
	 * 生成domian,dao接口及文件
	 * </pre>
	 * @param req
	 * @throws Exception 
	 */
	public void generate(GenerateRequest req) throws Exception {
		List<String> dirs = this.assembleFileDirData(req);
		this.createFileDir(dirs);
		log.info("directory structure created");
		//TODO 
		this.createDefaultFile(req);
		log.info("default file created");
	}
	
	/**
	 * 创建文件及目录
	 * @param files
	 */
	private void createFileDir(List<String> files) {
		for(String f : files) {
			boolean b = FileUtil.createDirectory(f);
			log.info("'{}' path is created result = {}",f,b);
		}
	}
	
	/**
	 * 组装创建文件数据
	 * @param req
	 * @return
	 */
	private List<String> assembleFileDirData(GenerateRequest req) {
		String path = req.getProjectPath() + "/" + req.getProjectName() + "/";
		boolean b = FileUtil.createDirectory(path);
		List<String> dirs = new ArrayList<String>();
		if(b || req.isOverride()) {
			apiModuleName = req.getProjectName() + "-api";
			rtModuleName = req.getProjectName() + "-rt";
			String basePackagePath = req.getBasePackage().replaceAll("\\.", "/");
			
			String apiSrcMain = path + apiModuleName + "/src/main/java/" + basePackagePath;
			String apiSrcTest = path + apiModuleName + "/src/test/java/" + basePackagePath;
			
			String rtSrcMain = path + rtModuleName + "/src/main/java/" + basePackagePath;
			String rtSrcTest = path + rtModuleName + "/src/test/java/" + basePackagePath;
			rtSrcRes = path + rtModuleName + "/src/main/resources/";
			rtSrcResConfig = path + rtModuleName + "/src/main/resources/config/";
			rtSrcResSpring = path + rtModuleName + "/src/main/resources/spring/";
			rtSrcResMybatis = path + rtModuleName + "/src/main/resources/mybatis/";
			
			String rtSrcTestResSpring = path + rtModuleName + "/src/test/resources/spring/";
			String rtSrcTestResConfig = path + rtModuleName + "/src/test/resources/config/";
			String rtSrcTestResMybatis = path + rtModuleName + "/src/test/resources/mybatis/";
			
			dirs.add(apiSrcMain);
			dirs.add(apiSrcTest);
			dirs.add(rtSrcMain);
			dirs.add(rtSrcResConfig);
			dirs.add(rtSrcResSpring);
			dirs.add(rtSrcResMybatis);
			dirs.add(rtSrcTest);
			dirs.add(rtSrcTestResSpring);
			dirs.add(rtSrcTestResConfig);
			dirs.add(rtSrcTestResMybatis);
		}else {
			throw new RuntimeException("create directory fail");
		}
		return dirs;
	}
	
	/**
	 * 创建默认文件
	 * TODO 2019-11-05 10:54:58 使用模版
	 * @param req
	 * @return
	 */
	private Map<String,String> createDefaultFile(GenerateRequest req) throws Exception {
		Map<String,String> defaultFiles = new HashMap<String,String>();
		
		Map<String, Object> params = BeanUtil.beanTrans2Map(req, false);
		
		//parent 
		String parentPath = req.getProjectPath() + "/" + req.getProjectName() + "/";
		//gitignore pom
		VelocityUtil.merge(gitignore, null, parentPath + ".gitignore");
		VelocityUtil.merge(parentPom, params, parentPath + "pom.xml");
		
		//api
		String apiPath = parentPath + "/" + apiModuleName + "/";
		//gitignore pom
		VelocityUtil.merge(gitignore, null, apiPath + ".gitignore");
		VelocityUtil.merge(apiPom, params, apiPath + "pom.xml");
		
		//rt		
		String rtPath = parentPath + "/" + rtModuleName + "/";
		//gitignore pom logback
		VelocityUtil.merge(gitignore, null, rtPath + ".gitignore");
		VelocityUtil.merge(rtPom, params, rtPath + "pom.xml");
		
		VelocityUtil.merge(logback, params, rtSrcRes + "logback.xml");
		VelocityUtil.merge(springApplication, params, rtSrcResSpring + "spring-application.xml");
		VelocityUtil.merge(mybatisConfig, params, rtSrcResMybatis + "mybatis-config.xml");
		
		return defaultFiles;
	}

	
	public static void main(String[] args) {
		GenerateTest gen = new GenerateTest();
		GenerateRequest req = new GenerateRequest();
		
		req.setProjectPath("c:");
		req.setProjectName("demo");
		req.setBasePackage("com.demo");
		req.setOverride(true);
		
		try {
			gen.generate(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
