package com.github.mtdp.quick;

import java.io.Serializable;

import lombok.Data;

@Data
public class TableInfoBO implements Serializable{

	private static final long serialVersionUID = -3885796122265170805L;
	
	private String Name;
	
	private String comment;

}
