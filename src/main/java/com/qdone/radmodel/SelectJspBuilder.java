// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   SelectJspBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity, Field

public class SelectJspBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public SelectJspBuilder(Project project)
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
		String fileName = (new StringBuilder(String.valueOf(project.getOutputPath()))).append("view").append(File.separator).append(entity.getEntityBeanName()).append(File.separator).append("select").append(entity.getEntityName()).append(".html").toString();
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
		StringBuffer codeBuffer = new StringBuffer();
		String fileHeader = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("selectHeader.txt").toString();
		String insertHeaderString;
		try
		{
			insertHeaderString = FileUtils.readFileToString(new File(fileHeader));
		}
		catch (IOException e)
		{
			insertHeaderString = "";
		}
//		System.err.println("主键的备注名称是:"+entity.getPrimaryKeyField().getFieldMemo());
		//datagrid数据集合
		StringBuffer dataGridArea = new StringBuffer();
		String primaryKey = entity.getPrimaryKeyField().getPropertyName();
		String primaryKeyMemo=entity.getPrimaryKeyField().getFieldMemo();
		dataGridArea.append("                      ").append("<th data-field=\"state\"  data-align=\"center\"  data-checkbox=\"true\"></th>\r\n");
		dataGridArea.append("                      ").append("<th data-field=\"index\" data-title=\"序号\" data-align=\"center\"  data-formatter=\"getRowIndex\"></th>\r\n");
		//dataGridArea.append("                      ").append((new StringBuilder("   <th data-options=\"field:'")).append(primaryKey).append("',width:150,sortable:true\">").append(primaryKeyMemo).append("</th>\r\n").toString());
		dataGridArea.append("                      ").append((new StringBuilder("<th data-field='")).append(primaryKey).append("' data-sortable='true' data-align='center'  data-title='").append(primaryKeyMemo).append("'></th>\r\n").toString());
		/*dataGridArea.append("                      ").append((new StringBuilder("<th data-options=\"field:'")).append(primaryKey).append("',width:150,sortable:true\">").append(primaryKey).append("</th>\r\n").toString());*/
		int dataCount = 0;
		//将属性名称替换成备注信息
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			Field field = (Field)iterator.next();
//			System.err.println("非主键的备注名称是:"+field.getFieldMemo());
			if (++dataCount == entity.getFields().size())
				dataGridArea.append("                      ").append((new StringBuilder("<th data-field='")).append(field.getPropertyName()).append("' data-sortable='true' data-align='center'  data-title='").append(field.getFieldMemo()).append("'></th>").toString());
				//dataGridArea.append("                      ").append((new StringBuilder("<th data-options=\"field:'")).append(field.getPropertyName()).append("',width:150,sortable:true\">").append(field.getFieldMemo()).append("</th>").toString());
			else
				//dataGridArea.append("                      ").append((new StringBuilder("<th data-options=\"field:'")).append(field.getPropertyName()).append("',width:150,sortable:true\">").append(field.getFieldMemo()).append("</th>\r\n").toString());
			dataGridArea.append("                      ").append((new StringBuilder("<th data-field='")).append(field.getPropertyName()).append("' data-sortable='true' data-align='center'  data-title='").append(field.getFieldMemo()).append("'></th>\r\n").toString());
		}
		insertHeaderString = insertHeaderString.replaceAll("_primaryKeyField_", primaryKey);
		insertHeaderString = insertHeaderString.replaceAll("_primaryKeyFieldMemo_",primaryKeyMemo);
		insertHeaderString = insertHeaderString.replaceAll("_DataGridColnumFields_", dataGridArea.toString());
		//查询框
		StringBuffer searchArea = new StringBuffer();
		searchArea.append("      ").append("<td>").append((new StringBuilder(String.valueOf(primaryKeyMemo))).append(":<input id='").append(primaryKey).append("' name='").append(primaryKey).append("'  style=\"width: 80px;height:25px;\"/></td>\r\n").toString());
		int searchAreaCount = 0;
		/*bootstrap-table查询附加参数queryParams*/
		StringBuffer queryParamsArea = new StringBuffer();
		//替换查询框的内容更加智能
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			Field field = (Field)iterator.next();
			if (++searchAreaCount == entity.getFields().size()){
				//生成对应easyui控件
				if(getDataType(field.getDataType()).equals("Date")){
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("' readonly='true' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\" style=\"width: 80px;height:25px;\"/></td>").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();");
				}
				else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("'  style=\"width: 80px;height:25px;\"/></td>").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();");
				}else{
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("'  style=\"width: 80px;height:25px;\"/></td>").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();");
				}
			}
			else{
				//生成对应easyui控件
				if(getDataType(field.getDataType()).equals("Date")){
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("'  readonly='true' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\" style=\"width: 80px;height:25px;\"/></td>\r\n").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();\r\n");
				}
				else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("'  style=\"width: 80px;height:25px;\"/></td>\r\n").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();\r\n");
				}else{
					searchArea.append("                                      ").append("<td>").append((new StringBuilder(String.valueOf(field.getFieldMemo()))).append(":<input id='").append(field.getPropertyName()).append("' name='").append(field.getPropertyName()).append("'  style=\"width: 80px;height:25px;\"/></td>\r\n").toString());
					queryParamsArea.append("			").append("params.").append(field.getPropertyName()).append("=\\$('#"+field.getPropertyName()+"').val();\r\n");
				}
			}
		}
		insertHeaderString = insertHeaderString.replaceAll("_SearchAreaFileds_", searchArea.toString());
		insertHeaderString = insertHeaderString.replaceAll("_prefix_", project.getPrefix());
		insertHeaderString = insertHeaderString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		insertHeaderString = insertHeaderString.replaceAll("_EntityName_", entity.getEntityName());
		codeBuffer.append(insertHeaderString);
		String fileFooter = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("selectFooter.txt").toString();
		String insertFooterString;
		try
		{
			insertFooterString = FileUtils.readFileToString(new File(fileFooter));
		}
		catch (IOException e)
		{
			insertFooterString = "";
		}
		insertFooterString = insertFooterString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		//弹出可编辑列表
		StringBuffer searchAreaParam = new StringBuffer(1024);
		searchAreaParam.append("         <tr>\r\n");
		searchAreaParam.append("                      ").append((new StringBuilder("<td>")).append(primaryKeyMemo).append("</td>\r\n").toString());
		searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(primaryKey).append("' class=\"easyui-validatebox\" autocomplete=\"off\" readonly=\"true\" /></td>\r\n").toString());
		searchAreaParam.append("                      ").append("</tr>\r\n");
		int searchCount = 0;
		for (Iterator<?> iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			Field field = (Field)iterator.next();
			if (++searchCount == entity.getFields().size())
			{
				searchAreaParam.append("                      ").append("<tr>\r\n");
				searchAreaParam.append("                      ").append((new StringBuilder("<td>")).append(field.getFieldMemo()).append("</td>\r\n").toString());
				//生成对应easyui控件
				if(getDataType(field.getDataType()).equals("Date")){
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-datebox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}
				else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-numberbox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}else{
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-validatebox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}
				searchAreaParam.append("                      ").append("</tr>\r\n");
			} else
			{
				searchAreaParam.append("                      ").append("<tr>\r\n");
				searchAreaParam.append("                      ").append((new StringBuilder("<td>")).append(field.getFieldMemo()).append("</td>\r\n").toString());
				//生成对应easyui控件
				if(getDataType(field.getDataType()).equals("Date")){
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-datebox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}
				else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-numberbox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}else{
					searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(field.getPropertyName()).append("' class=\"easyui-validatebox\" autocomplete=\"off\" required=\"true\" /></td>\r\n").toString());
				}
				searchAreaParam.append("                      ").append("</tr>\r\n");
			}
		}
		insertFooterString = insertFooterString.replaceAll("_EntityName_", entity.getEntityName());
		insertFooterString = insertFooterString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		insertFooterString = insertFooterString.replaceAll("_primaryKeyField_",primaryKey);
		insertFooterString = insertFooterString.replaceAll("_primaryKeyFieldMemo_",primaryKeyMemo);
		insertFooterString = insertFooterString.replaceAll("_SearchMethodParam_", searchAreaParam.toString());
		insertFooterString = insertFooterString.replaceAll("_QueryParamsAreaFileds_", queryParamsArea.toString());
		codeBuffer.append(insertFooterString);
		saveToFile(codeBuffer);
	}
	/**
	 * 界面弹出dialog编辑，遇到对应类型，自动生成easyui的对应控件
	 * @param iDataType
	 * @return
	 */
	private String getDataType(int iDataType)
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
			dataType = "double";
		else
		if (iDataType == 91 || iDataType == 93 || iDataType == 92)
			dataType = "Date";
		else
		if (iDataType == 2004)
			dataType = "byte[]";
		else if(iDataType==3){
			dataType = "BigDecimal";
		}
		return dataType;
	}
}
