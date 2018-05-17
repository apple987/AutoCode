// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ServiceBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class ServiceBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public ServiceBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveCodeToFile(boolean impl)
	{
		String fileString;
		if (impl)
			fileString = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("serviceImplTemplate.txt").toString();
		else
			fileString = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("serviceTemplate.txt").toString();
		String serviceString;
		try
		{
			serviceString = FileUtils.readFileToString(new File(fileString));
		}
		catch (IOException e)
		{
			serviceString = "";
		}
		String primaryKey = entity.getPrimaryKeyField().getPropertyName();
		String primaryKeyMemo=entity.getPrimaryKeyField().getFieldMemo();
		serviceString = serviceString.replaceAll("_Date_", project.getDateString());
		serviceString = serviceString.replaceAll("_prefix_", project.getPrefix());
		serviceString = serviceString.replaceAll("_package_", project.getRootPackage());
		serviceString = serviceString.replaceAll("_SpringMVCAnnotation_", (new StringBuilder(String.valueOf(entity.getEntityName().substring(0, 1).toLowerCase()))).append(entity.getEntityName().substring(1)).toString());
		serviceString = serviceString.replaceAll("_EntityName_", entity.getEntityName());
		/*主键的类型，备注，属性名称*/
		serviceString = serviceString.replaceAll("_primaryKeyFieldJavaType_", getDataTypeClass(entity.getPrimaryKeyField().getDataType()));
		serviceString = serviceString.replaceAll("_primaryKeyField_", primaryKey);
		serviceString = serviceString.replaceAll("_primaryKeyFieldMemo_", primaryKeyMemo);
		//替换执行的sqlId到mapper下
		serviceString = serviceString.replaceAll("_sqlMapperEntity_", project.getMapperPackage()+"."+entity.getEntityName());
		String dir = project.getServiceImplPath();
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
		String fileName;
		if (!impl)
			fileName = (new StringBuilder(String.valueOf(project.getServicePath()))).append(entity.getEntityName()).append("Service.java").toString();
		else
			fileName = (new StringBuilder(String.valueOf(project.getServiceImplPath()))).append(entity.getEntityName()).append("ServiceImpl.java").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, serviceString, "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
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

	public void saveToFile()
	{
		saveCodeToFile(false);
		saveCodeToFile(true);
	}
}
