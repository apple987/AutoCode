// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StrutsBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class StrutsBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public StrutsBuilder(Project project)
	{
		this.project = project;
		entity = project.getEntity();
	}

	public void saveToFile(String strutsConfigContent)
	{
		String fileName = (new StringBuilder(String.valueOf(project.getOutputPath()))).append("config").append(File.separator).append("struts").append(File.separator).append("struts-").append(entity.getEntityBeanName()).append(".xml").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, strutsConfigContent, "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveToFile()
	{
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("actionConfigTemplate.txt").toString();
		String strutsString;
		try
		{
			strutsString = FileUtils.readFileToString(new File(file));
		}
		catch (IOException e)
		{
			strutsString = "";
		}
		strutsString = strutsString.replaceAll("_prefix_", project.getPrefix());
		strutsString = strutsString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		saveToFile(strutsString);
	}
}
