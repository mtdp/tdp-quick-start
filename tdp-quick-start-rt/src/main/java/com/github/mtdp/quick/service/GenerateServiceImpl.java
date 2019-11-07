package com.github.mtdp.quick.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.github.mtdp.TdpUtilConstants;
import com.github.mtdp.quick.DoMainBO;
import com.github.mtdp.quick.GenerateService;
import com.github.mtdp.quick.ProjectDTO;
import com.github.mtdp.quick.PropertyBO;
import com.github.mtdp.quick.QuickConstants;
import com.github.mtdp.quick.TableInfoBO;
import com.github.mtdp.quick.req.GenerateRequest;
import com.github.mtdp.util.BeanUtil;
import com.github.mtdp.util.FileUtil;
import com.github.mtdp.util.StringUtil;
import com.github.mtdp.util.VelocityUtil;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 *
 * @Description TODO
 * @author wangguoqing
 * @date 2019年11月5日下午6:36:13
 *
 */

@Slf4j
@Service
public class GenerateServiceImpl implements GenerateService {
	

	@Override
	public boolean generate(GenerateRequest req) {
		ProjectDTO pro = createDir(req);
		log.info("dir created");
		createDefaultFile(req,pro);
		log.info("default file created");
		if(StringUtil.isNotBlank(req.getJdbcUrl())) {
			generateCode(req,pro);
			log.info("code created");
		}
		return true;
	}
	
	/**
	 * 生成dao domain代码
	 * 目录结构如下
	 * src/main/java/${basepackage}/dal
	 * 								 |-domain
	 * 								 |-dao		
	 * @return
	 */
	public boolean generateCode(GenerateRequest req,ProjectDTO pro) {
		//生成对应目录
		//生成文件 xml & 接口,domain
		Map<String, Object> params = BeanUtil.beanTrans2Map(req, false);
		List<DoMainBO> list = queryTableInfo(req);
		try {
			String domainPath = pro.getRtMainJavaPath() + QuickConstants.DOMAIN_PATH; 
			String mapperPath = pro.getRtMainJavaPath() + QuickConstants.MAPPER_PATH;
			for(DoMainBO domain : list) {
				domain.setMapperName(domain.getDomainName() + QuickConstants.MAPPER_SUFFIX);
				
				Map<String, Object> bParams = BeanUtil.beanTrans2Map(domain, false);
				params.putAll(bParams);
				VelocityUtil.merge(QuickConstants.DOMAIN, params, domainPath + domain.getDomainName() + QuickConstants.JAVA_FILE_SUFFIX);
				//xml & 接口
				
				VelocityUtil.merge(QuickConstants.MAPPER, params, mapperPath + domain.getMapperName() + QuickConstants.JAVA_FILE_SUFFIX);
				
				VelocityUtil.merge(QuickConstants.MAPPER_XML, params,mapperPath + domain.getMapperName() + QuickConstants.MAPPER_FILE_SUFFIX);
			}
		} catch (Exception e) {
			log.error("generate code ex",e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * 查询所有符合的规则的表名
	 * @param req
	 */
	private List<DoMainBO> queryTableInfo(GenerateRequest req) {
		List<DoMainBO> domains = Lists.newArrayList();
		if(StringUtil.isNotBlank(req.getJdbcUrl()) && req.getJdbcUrl().indexOf("mysql") > 0) {
			try {
				Class.forName(QuickConstants.CLASS_NAME);
				Connection conn = DriverManager.getConnection(req.getJdbcUrl(), req.getUserName(), req.getPasswrod());
				
				String sql = "select table_name,table_comment from information_schema.tables where table_schema = ? and table_name like concat('%',?,'%')";
				String sql2 = "select column_name,data_type,column_comment,column_key,extra from information_schema.columns where table_schema = ? and table_name = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, req.getDbSchema());
				pstmt.setString(2, req.getTablePrefix());
				ResultSet resultSet = pstmt.executeQuery();
				//查询需要生成的表结构
				List<TableInfoBO> tableInfos = Lists.newArrayList();
				while(resultSet.next()) {
					String tblName = resultSet.getString("table_name");
					String tblComment = resultSet.getString("table_comment");
					TableInfoBO tbl = new TableInfoBO();
					tbl.setTblName(tblName);
					tbl.setTblComment(tblComment);
					tableInfos.add(tbl);
				}
				//查询字段
				for(TableInfoBO info : tableInfos) {
					DoMainBO domain = new DoMainBO();
					domain.setAuthor(req.getAuthor());
					domain.setTableName(info.getTblName());
					String tempName = info.getTblName().replace(req.getTablePrefix(), "");
					domain.setDomainName(BeanUtil.firstLetter2UpperCase(BeanUtil.underline2Camel(tempName)));
					domain.setDesc(info.getTblComment());
					
					pstmt = conn.prepareStatement(sql2);
					pstmt.setString(1, req.getDbSchema());
					pstmt.setString(2, info.getTblName());
					ResultSet result = pstmt.executeQuery();
					List<PropertyBO> properties = Lists.newArrayList();
					while(result.next()) {
						String columnName = result.getString("column_name");
						String dataType = result.getString("data_type");
						String comment = result.getString("column_comment");
						
						PropertyBO prop = new PropertyBO();
						prop.setColumnName(columnName);
						prop.setName(BeanUtil.underline2Camel(columnName));
						//类型转换
						prop.setType(dataTypeCoverJavaType(dataType));
						prop.setComment(comment);
						//主键及自增
						if(QuickConstants.PRIMARY_KEY.equalsIgnoreCase(result.getString("column_key"))) {
							prop.setPrimaryKey(true);
							domain.setPrimaryName(prop.getName());
						}
						if(QuickConstants.AUTO_INCREMENT.equalsIgnoreCase(result.getString("extra"))){
							prop.setAutoCrement(true);
							domain.setAutoCrement(true);
						}
						properties.add(prop);
					}
					domain.setProperties(properties);
					domains.add(domain);
				}
			} catch (Exception e) {
				log.error("query data ex",e);
			}
		}
		return domains;
	}
	
	/**
	 * 数据类型跟java类型转换
	 * @param dataType
	 * @return
	 */
	private String dataTypeCoverJavaType(String dataType) {
		String type = "String";
		switch (dataType) {
		case "int":
			type = "Integer";
			break;
		case "varchar":
			type = "String";
			break;
		case "datetime":
			type = "Date";
			break;
		case "char":
			type = "Integer";
			break;
		case "longtext":
			type = "String";
			break;
		default:
			type = "String";
			break;
		}
		return type;
	}
	
	/**
	 * 生成目录
	 * @param req
	 * @return
	 */
	private ProjectDTO createDir(GenerateRequest req) {
		String parentPath = req.getProjectPath() + req.getProjectName() + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String apiPath = parentPath + req.getProjectName() + QuickConstants.LINK + QuickConstants.API_SUFFIX + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String rtPath = parentPath + req.getProjectName() + QuickConstants.LINK + QuickConstants.RT_SUFFIX + TdpUtilConstants.FILE_PATH_SEPARATOR;

		String basePackage = req.getBasePackage().replaceAll("\\.", TdpUtilConstants.FILE_PATH_SEPARATOR);
		
		String apiMainJavaPath = apiPath + QuickConstants.MAIN_JAVA_PATH + basePackage + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String apiTestJavaPath = apiPath + QuickConstants.TEST_JAVA_PATH + basePackage + TdpUtilConstants.FILE_PATH_SEPARATOR;
		
		String rtMainJavaPath = rtPath + QuickConstants.MAIN_JAVA_PATH + basePackage + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String rtTestJavaPath = rtPath + QuickConstants.TEST_JAVA_PATH + basePackage + TdpUtilConstants.FILE_PATH_SEPARATOR;
		
		String rtMainServicePath = rtMainJavaPath + QuickConstants.SERVICE_PATH;
		String rtMainResourcePath = rtPath + QuickConstants.MAIN_RESOURCES_PATH;
		String rtMainConfigPath = rtMainResourcePath + QuickConstants.RT_CONFIG_DIR_NAME + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String rtMainSpringPath = rtMainResourcePath + QuickConstants.RT_SPRING_DIR_NAME + TdpUtilConstants.FILE_PATH_SEPARATOR;
		String rtMainMybatisPath = rtMainResourcePath + QuickConstants.RT_MYBATIS_DIR_NAME + TdpUtilConstants.FILE_PATH_SEPARATOR;
		
		List<String> listPath = Lists.newArrayList();
		listPath.add(parentPath);
		listPath.add(apiPath);
		listPath.add(rtPath);
		listPath.add(apiMainJavaPath);
		listPath.add(apiTestJavaPath);
		listPath.add(rtMainJavaPath);
		listPath.add(rtTestJavaPath);
		listPath.add(rtMainResourcePath);
		listPath.add(rtMainConfigPath);
		listPath.add(rtMainSpringPath);
		listPath.add(rtMainMybatisPath);
		listPath.add(rtMainServicePath);
		
		for(String path : listPath) {
			FileUtil.createDirectory(path);
		}
		
		ProjectDTO pro = new ProjectDTO();
		pro.setParentPath(parentPath);
		pro.setApiPath(apiPath);
		pro.setRtPath(rtPath);
		pro.setRtMainResourcePath(rtMainResourcePath);
		pro.setRtMainConfigPath(rtMainConfigPath);
		pro.setRtMainMybatisPath(rtMainMybatisPath);
		pro.setRtMainSpringPath(rtMainSpringPath);
		pro.setRtMainJavaPath(rtMainJavaPath);
		return pro;
	}
	
	/**
	 * 生成默认文件
	 * @param req
	 * @param pro
	 * @return
	 */
	private boolean createDefaultFile(GenerateRequest req,ProjectDTO pro) {
		Map<String, Object> params = BeanUtil.beanTrans2Map(req, false);
		try {
			//parent
			VelocityUtil.merge(QuickConstants.PARENT_POM_TEMPLATE, params, pro.getParentPath() + QuickConstants.POM_FILE_NAME);
			VelocityUtil.merge(QuickConstants.GITIGNORE_TEMPLATE, params, pro.getParentPath() + QuickConstants.GITIGNORE_FILE_NAME);
			//api
			VelocityUtil.merge(QuickConstants.API_POM_TEMPLATE, params, pro.getApiPath() + QuickConstants.POM_FILE_NAME);
			VelocityUtil.merge(QuickConstants.GITIGNORE_TEMPLATE, params, pro.getApiPath() + QuickConstants.GITIGNORE_FILE_NAME);
			//rt
			VelocityUtil.merge(QuickConstants.RT_POM_TEMPLATE, params, pro.getRtPath()+ QuickConstants.POM_FILE_NAME);
			VelocityUtil.merge(QuickConstants.GITIGNORE_TEMPLATE, params, pro.getRtPath() + QuickConstants.GITIGNORE_FILE_NAME);
			VelocityUtil.merge(QuickConstants.LOGBACK_TEMPLATE, params, pro.getRtMainResourcePath() + QuickConstants.LOG_FILE_NAME);
			VelocityUtil.merge(QuickConstants.SPRING_APPLICATION_TEMPLATE, params, pro.getRtMainSpringPath() + QuickConstants.RT_SPRING_CONFIG_NAME);
			VelocityUtil.merge(QuickConstants.MYBATIS_CONFIG_TEMPLATE, params, pro.getRtMainMybatisPath() + QuickConstants.RT_MYBATIS_CONFIG_NAME);
			return true;
		}catch (Exception e) {
			log.error("create default file ex",e);
		}
		return false;
	}

}
