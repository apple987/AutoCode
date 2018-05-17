// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Project.java

package com.qdone.radmodel;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.qdone.radmodel:
//			StringUtil, Entity

public class Project
{

	private String projPath;
	private String rootPackage;
	private String srcPath;
	private String rootPackagePath;
	private String jspRootPath;
	private String jsRootPath;
	private Entity entity;
	private String entitiesPath;
	private String entitiesPackage;
	private String actionPath;
	private String actionPackage;
	//添加一个controller层
	private String controllerPath;
	private String controllerPackage;
	
	private String servicePath;
	private String servicePackage;
	private String serviceImplPath;
	private String serviceImplPackage;
	private String outputPath;
	private String templatePath;
	private String prefix;
	private String dateString;
	//新增dao层
	private String daoPath;
	private String daoPackage;
	private String daoImplPath;
	private String daoImplPackage;
	//新增mapper配置xml
	private String mapperPath;
	private String mapperPackage;
	//所有的JdbcType加载到map
	private Map<Integer,String> jdbcMap;

	public Project(String projPath, String rootPackage)
	{
		this.projPath = projPath;
		this.rootPackage = rootPackage;
		this.jdbcMap=initJdbcMap();
		if (this.projPath.endsWith(File.separator)) {
		outputPath = (new StringBuilder(String.valueOf(this.projPath))).append("output").append(File.separator).toString();
		srcPath = (new StringBuilder(String.valueOf(outputPath))).append("src").append(File.separator).toString();
		rootPackagePath = (new StringBuilder(String.valueOf(srcPath))).append(StringUtil.getSeperatedPathFromPackage(this.rootPackage)).toString();
		jspRootPath = (new StringBuilder(String.valueOf(this.projPath))).append("WebRoot").append(File.separator).append("WEB-INF").append(File.separator).append("pages").append(File.separator).toString();
		jsRootPath = (new StringBuilder(String.valueOf(this.projPath))).append("WebRoot").append(File.separator).append("js").append(File.separator).toString();
		entitiesPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("model").append(File.separator).toString();
		entitiesPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".model").toString();
		
		mapperPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("mapper").append(File.separator).toString();
		mapperPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".mapper").toString();
		
		
//		actionPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("action").append(File.separator).toString();
//		actionPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".action").toString();
		//添加controller层
		controllerPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("controller").append(File.separator).toString();
		controllerPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".controller").toString();
		//添加业务逻辑service层
		servicePath = (new StringBuilder(String.valueOf(rootPackagePath))).append("service").append(File.separator).toString();
		servicePackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".service").toString();
		serviceImplPath = (new StringBuilder(String.valueOf(servicePath))).append("impl").append(File.separator).toString();
		serviceImplPackage = (new StringBuilder(String.valueOf(servicePackage))).append(".impl").toString();
		//添加一个dao层
		daoPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("dao").append(File.separator).toString();
		daoPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".dao").toString();
		daoImplPath = (new StringBuilder(String.valueOf(daoPath))).append("impl").append(File.separator).toString();
		daoImplPackage = (new StringBuilder(String.valueOf(daoPackage))).append(".impl").toString();
		
		templatePath = (new StringBuilder(String.valueOf(this.projPath))).append("\\src\\main\\resources\\template").append(File.separator).toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		dateString = sdf.format(new Date());
		return;
	   }else{
		outputPath = (new StringBuilder(String.valueOf(this.projPath))).append(File.separator).append("output").append(File.separator).toString();
		srcPath = (new StringBuilder(String.valueOf(outputPath))).append("src").append(File.separator).toString();
		rootPackagePath = (new StringBuilder(String.valueOf(srcPath))).append(StringUtil.getSeperatedPathFromPackage(this.rootPackage)).toString();
		jspRootPath = (new StringBuilder(String.valueOf(this.projPath))).append("WebRoot").append(File.separator).append("WEB-INF").append(File.separator).append("pages").append(File.separator).toString();
		jsRootPath = (new StringBuilder(String.valueOf(this.projPath))).append("WebRoot").append(File.separator).append("js").append(File.separator).toString();
		entitiesPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("model").append(File.separator).toString();
		entitiesPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".model").toString();

		mapperPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("mapper").append(File.separator).toString();
		mapperPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".mapper").toString();

		
		
		
//	actionPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("action").append(File.separator).toString();
//		actionPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".action").toString();
		//添加controller层
		controllerPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("controller").append(File.separator).toString();
		controllerPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".controller").toString();
		//添加业务逻辑service层
		servicePath = (new StringBuilder(String.valueOf(rootPackagePath))).append("service").append(File.separator).toString();
		servicePackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".service").toString();
		serviceImplPath = (new StringBuilder(String.valueOf(servicePath))).append("impl").append(File.separator).toString();
		serviceImplPackage = (new StringBuilder(String.valueOf(servicePackage))).append(".impl").toString();
		//添加一个dao层
		daoPath = (new StringBuilder(String.valueOf(rootPackagePath))).append("dao").append(File.separator).toString();
		daoPackage = (new StringBuilder(String.valueOf(this.rootPackage))).append(".dao").toString();
		daoImplPath = (new StringBuilder(String.valueOf(daoPath))).append("impl").append(File.separator).toString();
		daoImplPackage = (new StringBuilder(String.valueOf(daoPackage))).append(".impl").toString();
		
		templatePath = (new StringBuilder(String.valueOf(this.projPath))).append(File.separator).append("src\\main\\resources\\template").append(File.separator).toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		dateString = sdf.format(new Date());
		return;
	   }
	}
	/**
	 * 初始化
	 * @param iDataType
	 * @return
	 */
	private Map<Integer,String> initJdbcMap(){
		Map<Integer, String> mp = new HashMap<Integer, String>();
		Field[] fields = java.sql.Types.class.getFields();
		for (int i = 0, len = fields.length; i < len; ++i) {
			if (Modifier.isStatic(fields[i].getModifiers())) {
				try {
					String name = fields[i].getName();
					Integer value = (Integer) fields[i].get(java.sql.Types.class);
					mp.put(value, name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mp;
	}
	public String getSrcPath()
	{
		return srcPath;
	}

	public String getRootPackagePath()
	{
		return rootPackagePath;
	}

	public String getJspRootPath()
	{
		return jspRootPath;
	}

	public String getJsRootPath()
	{
		return jsRootPath;
	}

	public void generate()
	{
	}

	public Entity getEntity()
	{
		return entity;
	}

	public String getProjPath()
	{
		return projPath;
	}

	public String getRootPackage()
	{
		return rootPackage;
	}

	public String getEntitiesPath()
	{
		return entitiesPath;
	}

	public String getActionPath()
	{
		return actionPath;
	}

	public String getServicePath()
	{
		return servicePath;
	}

	public String getServiceImplPath()
	{
		return serviceImplPath;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	public String getEntitiesPackage()
	{
		return entitiesPackage;
	}

	public String getActionPackage()
	{
		return actionPackage;
	}

	public String getServicePackage()
	{
		return servicePackage;
	}

	public String getServiceImplPackage()
	{
		return serviceImplPackage;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getOutputPath()
	{
		return outputPath;
	}

	public void setOutputPath(String outputPath)
	{
		this.outputPath = outputPath;
	}

	public String getTemplatePath()
	{
		return templatePath;
	}

	public void setTemplatePath(String templatePath)
	{
		this.templatePath = templatePath;
	}

	public String getDateString()
	{
		return dateString;
	}

	public String getDaoImplPath() {
		return daoImplPath;
	}


	public String getDaoImplPackage() {
		return daoImplPackage;
	}

	

	public String getDaoPath() {
		return daoPath;
	}

	

	public String getDaoPackage() {
		return daoPackage;
	}

	

	public String getControllerPath() {
		return controllerPath;
	}


	public String getControllerPackage() {
		return controllerPackage;
	}

	public String getMapperPath() {
		return mapperPath;
	}

	public String getMapperPackage() {
		return mapperPackage;
	}

	public Map<Integer, String> getJdbcMap() {
		return jdbcMap;
	}


	
	
	
}
