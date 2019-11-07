package com.gihub.mtdp;

import org.junit.Test;

import com.github.mtdp.quick.GenerateService;
import com.github.mtdp.quick.req.GenerateRequest;
import com.github.mtdp.quick.service.GenerateServiceImpl;

/**
 * 
 *
 * @Description TODO
 * @author wangguoqing
 * @date 2019年11月5日下午6:38:04
 *
 */
public class GenerateServiceTest {
	
	@Test
	public void testGen() {
		GenerateService gen = new GenerateServiceImpl();
		GenerateRequest req = new GenerateRequest();
		req.setOverride(true);
		req.setProjectName("test");
		req.setProjectPath("c:/");
		req.setBasePackage("com.github.test");
		req.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/quick_test?useUnicode=true&amp;characterEncoding=UTF-8");
		req.setUserName("root");
		//TODO 
		req.setPasswrod("****@****");
		req.setDbSchema("quick_test");
		req.setTablePrefix("t_");
		req.setAuthor("wangguoqing");
		gen.generate(req);
	}

}
