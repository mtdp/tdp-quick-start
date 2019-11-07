package com.github.mtdp.quick;

import java.io.Serializable;

import lombok.Data;

@Data
public class PropertyBO implements Serializable{
	
	private static final long serialVersionUID = -5592210781049155L;
	
	private String type;
	
	private String name;
	
	private String columnName;
	
	private String comment; 
	
	private boolean isPrimaryKey = false;
	
	private boolean isAutoCrement = false;
	
}
