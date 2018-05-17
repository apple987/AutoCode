// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SpringBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class SpringBuilder
	implements CodeBuilder
{

	private Project project;

	public SpringBuilder(Project project)
	{
		this.project = project;
	}

	public void saveToFile(String springConfigContent)
	{
		String fileName = (new StringBuilder(String.valueOf(project.getOutputPath()))).append("config").append(File.separator).append("spring").append(File.separator).append("spring_").append(project.getEntity().getEntityBeanName()).append(".xml").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, springConfigContent, "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveToFile()
	{
		StringBuffer springConfigBuffer = new StringBuffer();
		Entity entity = project.getEntity();
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("springConfig.txt").toString();
		String springHeader;
		try
		{
			springHeader = FileUtils.readFileToString(new File(file));
		}
		catch (IOException e)
		{
			springHeader = "";
		}
		springConfigBuffer.append(springHeader);
		springConfigBuffer.append("\r\n");
		springConfigBuffer.append((new StringBuilder("<bean id=\"")).append(entity.getEntityBeanName()).append("Service\" class=\"").append(project.getServiceImplPackage()).append(".").append(entity.getEntityName()).append("ServiceImpl\" scope=\"prototype\">\r\n").toString());
		springConfigBuffer.append("  <property name=\"sqlDao\" ref=\"sqlDao\"/>\r\n");
		springConfigBuffer.append("  <property name=\"pageService\" ref=\"pageService\"/>\r\n");
		springConfigBuffer.append("</bean>\r\n\r\n");
		springConfigBuffer.append((new StringBuilder("<bean id=\"")).append(entity.getEntityBeanName()).append("Action\" class=\"").append(project.getActionPackage()).append(".").append(entity.getEntityName()).append("Action\" scope=\"prototype\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("  <property name=\"")).append(entity.getEntityBeanName()).append("Service\" ref=\"").append(entity.getEntityBeanName()).append("Service\" />\r\n").toString());
		springConfigBuffer.append("</bean>\r\n\r\n");
		springConfigBuffer.append("</beans>\r\n");
		saveToFile(springConfigBuffer.toString());
	}
}
