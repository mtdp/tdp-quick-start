package com.github.mtdp.quick.req;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;
/**
 * 
 *
 * @Description 请求
 * @author wangguoqing
 * @date 2019年9月7日上午11:14:24
 *
 */
@Data
@ToString
public class GenerateRequest implements Serializable {

	private static final long serialVersionUID = -695811929677352055L;
	
	/**工程名称**/
	private String projectName;
	
	/**工程保存路径**/
	private String projectPath;
	
	/**是否覆盖**/
	private boolean override;
	
	/**连接数据库url,例如:jdbc:mysql://127.0.0.1:3306/tdp?useUnicode=true&amp;characterEncoding=UTF-8 **/
	private String jdbcUrl;
	
	/**用户名**/
	private String userName;
	
	/**密码**/
	private String passwrod;
	
	/**数据库名称**/
	private String dbSchema;
	
	/**数据库表名前缀**/
	private String tablePrefix;
	
	/**基础包名**/
	private String basePackage;
	
	/**作者**/
	private String author;
	

}
