package com.github.mtdp.quick;

import java.io.Serializable;

import lombok.Data;
/**
 * 
 *
 * @Description TODO
 * @author wangguoqing
 * @date 2019年11月5日下午6:36:27
 *
 */
@Data
public class ProjectDTO implements Serializable{

	private static final long serialVersionUID = 3746474586038749959L;
	
	private String parentPath;
	
	private String apiPath;
	
	private String rtPath;
	
	private String rtMainResourcePath;
	
	private String rtMainConfigPath;
	
	private String rtMainSpringPath;
	
	private String rtMainMybatisPath;
	
	private String rtMainJavaPath;
	
}
