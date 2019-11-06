package com.github.mtdp.quick;

import java.io.Serializable;
import java.util.List;

import com.github.mtdp.util.DateUtil;

import lombok.Data;

@Data
public class DoMainBO implements Serializable{
	
	private static final long serialVersionUID = 7540084023279944405L;
	
	private String domainName;
	
	private String desc;
	
	private String author;
	
	private String date = DateUtil.getCurrentTime2();
	
	/**属性列表**/
	private List<PropertyBO> properties;
	
}
