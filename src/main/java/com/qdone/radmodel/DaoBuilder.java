// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DaoBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class DaoBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public DaoBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveCodeToFile(boolean impl)
	{
		String fileString;
		if (impl)
			fileString = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("daoImplTemplate.txt").toString();
		else
			fileString = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("daoTemplate.txt").toString();
		String daoString;
		try
		{
			daoString = FileUtils.readFileToString(new File(fileString));
		}
		catch (IOException e)
		{
			daoString = "";
		}
		daoString = daoString.replaceAll("_Date_", project.getDateString());
		daoString = daoString.replaceAll("_prefix_", project.getPrefix());
		daoString = daoString.replaceAll("_package_", project.getRootPackage());
		daoString = daoString.replaceAll("_SpringMVCAnnotation_", (new StringBuilder(String.valueOf(entity.getEntityName().substring(0, 1).toLowerCase()))).append(entity.getEntityName().substring(1)).toString());
		daoString = daoString.replaceAll("_EntityName_", entity.getEntityName());
		//替换执行的sqlId到mapper下
		daoString = daoString.replaceAll("_sqlMapperEntity_", project.getMapperPackage()+"."+entity.getEntityName());
		//_primaryKeyFieldName_  添加时，自动给主键赋值
		String primaryKey = entity.getPrimaryKeyField().getPropertyName();
		primaryKey = (new StringBuilder(String.valueOf(primaryKey.substring(0, 1).toUpperCase()))).append(primaryKey.substring(1)).toString();
		daoString = daoString.replaceAll("_SetPrimaryKeyFieldName_", (new StringBuilder("set")).append(primaryKey).toString());
		String dir = project.getDaoImplPath();
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
			fileName = (new StringBuilder(String.valueOf(project.getDaoPath()))).append(entity.getEntityName()).append("Dao.java").toString();
		else
			fileName = (new StringBuilder(String.valueOf(project.getDaoImplPath()))).append(entity.getEntityName()).append("DaoImpl.java").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, daoString, "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveToFile()
	{
		saveCodeToFile(false);
		saveCodeToFile(true);
	}
}
