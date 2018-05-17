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

public class ActionBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public ActionBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveToFile(StringBuffer codeBuffer)
	{
		String dir = project.getActionPath();
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
		String fileName = (new StringBuilder(String.valueOf(project.getActionPath()))).append(entity.getEntityName()).append("Action.java").toString();
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
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("actionTemplate.txt").toString();
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
		actionString = actionString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		actionString = actionString.replaceAll("_package_", project.getRootPackage());
		actionBuffer.append(actionString);
		saveToFile(actionBuffer);
	}
}
