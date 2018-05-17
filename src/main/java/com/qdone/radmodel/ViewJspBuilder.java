// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ViewJspBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity, Field

public class ViewJspBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public ViewJspBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveToFile(StringBuffer codeBuffer)
	{
		String dir = project.getSrcPath();
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
		String fileName = (new StringBuilder(String.valueOf(project.getOutputPath()))).append("jsp").append(File.separator).append(entity.getTableName().toLowerCase()).append(File.separator).append("selectPopEdit.html").toString();
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
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("viewHeader.txt").toString();
		String actionString;
		try
		{
			actionString = FileUtils.readFileToString(new File(file));
		}
		catch (IOException e)
		{
			actionString = "";
		}
		actionString = actionString.replaceAll("_prefix_", project.getPrefix());
		actionString = actionString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		/*生成查询字段框*/
		StringBuffer searchArea=new StringBuffer();
		String primaryKey=entity.getPrimaryKeyField().getPropertyName();
		searchArea.append("\r\n");
		searchArea.append("                      ").append("<td class='form-label' style='width: 80px;'>"+primaryKey+":</td>\r\n");
		searchArea.append("                      ").append("<td style='width: 150px;'><input id='"+primaryKey+"' name='"+primaryKey+"' class='mini-textbox' emptyText='请输入' style='width:150px;' /></td>\r\n");
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			Field field = (Field)iterator.next();
			searchArea.append("                      ").append("<td class='form-label' style='width: 80px;'>"+field.getPropertyName()+":</td>\r\n");
			searchArea.append("                      ").append("<td style='width: 150px;'><input id='"+field.getPropertyName()+"' name='"+field.getPropertyName()+"' class='mini-textbox' emptyText='请输入' style='width:150px;' /></td>\r\n");
		}
		actionString=actionString.replaceAll("_SearchAreaFileds_", searchArea.toString());
		/*排序主键*/
		actionString=actionString.replaceAll("_PrimaryKeyField_", primaryKey);
		/*生成datagrid循环数据*/
		StringBuffer dataArea=new StringBuffer();
		 //生成主键
		dataArea.append("            <div name='"+primaryKey+"'  field='"+primaryKey+"' headerAlign='center' allowSort='true' width='150' >"+primaryKey+"</div>\r\n");
		//生成主键以外的字段
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			
			Field field = (Field)iterator.next();
			dataArea.append("            <div name='"+field.getPropertyName()+"'  field='"+field.getPropertyName()+"' headerAlign='center' allowSort='true' width='150' >"+field.getPropertyName()+"</div>\r\n");
		}
		actionString = actionString.replaceAll("_DataGridCloumn_", dataArea.toString());
		/*拼接查询方法传递参数*/
		StringBuffer searchAreaParam=new StringBuffer(1024);
		searchAreaParam.append("'").append(primaryKey).append("'").append(":").append("mini.get('").append(primaryKey).append("').getValue()").append(",\r\n");
		int searchCount=0;
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			
			Field field = (Field)iterator.next();
			if (++searchCount == entity.getFields().size())
				searchAreaParam.append("                ").append("'").append(field.getPropertyName()).append("'").append(":").append("mini.get('").append(field.getPropertyName()).append("').getValue()").append("\r\n");
			else
				searchAreaParam.append("                ").append("'").append(field.getPropertyName()).append("'").append(":").append("mini.get('").append(field.getPropertyName()).append("').getValue()").append(",\r\n");
		}
		actionString = actionString.replaceAll("_SearchMethodParam_", searchAreaParam.toString());
		actionBuffer.append(actionString);
		saveToFile(actionBuffer);
	}
}
