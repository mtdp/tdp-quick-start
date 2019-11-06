package com.github.mtdp.quick.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.mtdp.quick.GenerateService;
import com.github.mtdp.quick.req.GenerateRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 * @Description 生成入口
 * @author wangguoqing
 * @date 2019年9月7日上午11:12:58
 *
 */
@RequestMapping("/")
@Slf4j
public class GenerateController {
	
	@Autowired
	private GenerateService generateService;
	
	@RequestMapping("gen")
	public String gen(GenerateRequest req) {
		log.info("start gen req = {}",req);
		generateService.generate(req);
		return "success";
	}
	
}
