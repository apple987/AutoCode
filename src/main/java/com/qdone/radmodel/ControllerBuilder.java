// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ActionBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class ControllerBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public ControllerBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveToFile(StringBuffer codeBuffer)
	{
		String dir = project.getControllerPath();
		File fileDir = new File(dir);
		if (!fileDir.exists())
			try
			{
				FileUtils.forceMkdir(fileDir);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		String fileName = (new StringBuilder(String.valueOf(project.getControllerPath()))).append(entity.getEntityName()).append("Controller.java").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, codeBuffer.toString(), "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveToFile()
	{
		StringBuffer actionBuffer = new StringBuffer();
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("controllerTemplate.txt").toString();
		String actionString;
		try
		{
			actionString = FileUtils.readFileToString(new File(file));
		}
		catch (IOException e)
		{
			actionString = "";
		}
		actionString = actionString.replaceAll("_Date_", project.getDateString());
		actionString = actionString.replaceAll("_EntityName_", entity.getEntityName());
		//删除时，自动取出主键
		String primaryKey = entity.getPrimaryKeyField().getPropertyName();
		actionString = actionString.replaceAll("_primaryKeyField_",primaryKey);
		primaryKey = (new StringBuilder(String.valueOf(primaryKey.substring(0, 1).toUpperCase()))).append(primaryKey.substring(1)).toString();
		actionString = actionString.replaceAll("_GetPrimaryKeyFieldName_", "get"+primaryKey+"()");
		actionString = actionString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		actionString = actionString.replaceAll("_EntityTableMemo_",entity.getTableMemo());
		actionString = actionString.replaceAll("_package_", project.getRootPackage());
		actionString = actionString.replaceAll("_PrimaryKeyField_",primaryKey);
		actionString = actionString.replaceAll("_PrimaryKeyFieldJavaType_",getDataTypeClass(entity.getPrimaryKeyField().getDataType())+"\"");
		//view方法针对性处理,主要针对string，int，long
		String pkJavaType=getDataTypeClass(entity.getPrimaryKeyField().getDataType());
		String viewMethodType="";
		if(pkJavaType.contains("Integer")){
			viewMethodType=entity.getEntityBeanName()+"Service.view(Integer.parseInt(request.getParameter(\""+entity.getPrimaryKeyField().getPropertyName()+"\"))";
		}else if(pkJavaType.contains("Double")){
			viewMethodType=entity.getEntityBeanName()+"Service.view(Double.parseDouble(request.getParameter(\""+entity.getPrimaryKeyField().getPropertyName()+"\"))";
		}else{
			viewMethodType=entity.getEntityBeanName()+"Service.view(request.getParameter(\""+entity.getPrimaryKeyField().getPropertyName()+"\"))";
		}
		actionString = actionString.replaceAll("_ViewMethodJavaType_",viewMethodType);
		actionBuffer.append(actionString);
		saveToFile(actionBuffer);
	}
	
	
	/**
	 * 数据库类型对应到具体的数据库类型
	 */
	private String getDataTypeClass(int iDataType)
	{
		//数据类型参考java.sql.Types
		String dataType = "";
		if (iDataType == 12 || iDataType == 1 || iDataType == -1 || iDataType == 2005)
			dataType = "String";
		else
		if (iDataType == 4 || iDataType == -5 || iDataType == -7 || iDataType == -6 || iDataType == 2)
			dataType = "Integer";
		else
		if (iDataType == 8 || iDataType == 6)
			dataType = "Double";
		else
		if (iDataType == 91 || iDataType == 93 || iDataType == 92)
			dataType = "java.util.Date";
		else
		if (iDataType == 2004)
			dataType = "java.lang.Byte[]";
		else if(iDataType==3){
			dataType = "java.math.BigDecimal";
		}
		return dataType;
	}
}
