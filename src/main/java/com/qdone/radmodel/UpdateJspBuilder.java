// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UpdateJspBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity, Field

public class UpdateJspBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public UpdateJspBuilder(Project project)
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
		String fileName = (new StringBuilder(String.valueOf(project.getOutputPath()))).append("view").append(File.separator).append(entity.getEntityBeanName()).append(File.separator).append("update").append(entity.getEntityName()).append(".html").toString();
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
		String file = (new StringBuilder(String.valueOf(project.getTemplatePath()))).append("updateHeader.txt").toString();
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
		actionString = actionString.replaceAll("_EntityName_", entity.getEntityName());
		actionString = actionString.replaceAll("_EntityBeanName_", entity.getEntityBeanName());
		/*生成datagrid循环数据*/
		/*StringBuffer dataArea=new StringBuffer();*/
		String primaryKey=entity.getPrimaryKeyField().getPropertyName();
		String primaryKeyMemo=entity.getPrimaryKeyField().getFieldMemo();
		 //生成主键
		//dataArea.append("            <td >"+primaryKey+":</td><td ><input id='"+primaryKey+"' name='"+primaryKey+"' class='mini-textbox'   /></td>\r\n");
		//生成主键以外的字段
		/*for (Iterator iterator = entity.getFields().iterator(); iterator.hasNext();)
		{
			
			Field field = (Field)iterator.next();
			dataArea.append("            <td >"+field.getPropertyName()+":</td><td ><input id='"+field.getPropertyName()+"' name='"+field.getPropertyName()+"' class='mini-textbox'   /></td>\r\n");
		}
		actionString = actionString.replaceAll("_DataGridCloumn_", dataArea.toString());
		actionBuffer.append(actionString);*/
		/***********************************渲染表格部分*********************************************************************/
		//更新表格
		StringBuffer searchAreaParam = new StringBuffer(1024);
		//主键部分，更新时需要只读,时暂时不需要
		/*searchAreaParam.append("         <tr>\r\n");
		searchAreaParam.append("                      ").append((new StringBuilder("<td>")).append(primaryKeyMemo).append("</td>\r\n").toString());
		searchAreaParam.append("                            ").append((new StringBuilder("<td><input name='")).append(primaryKey).append("' class=\"easyui-validatebox\" autocomplete=\"off\" readonly=\"true\" /></td>\r\n").toString());
		searchAreaParam.append("                      ").append("</tr>\r\n");*/
		/*绘制表格，默认每行2列*/
		ArrayList<Field> arr=entity.getFields();
		int rowCloumSize=2;//每行两列显示
		if(arr.size()%rowCloumSize==0){//刚好整数倍，分拨次生成多行
			for (int i = 0; i <arr.size()/rowCloumSize; i++) {//共计多少行，field分多少组执行
				createRow(searchAreaParam,arr.subList(i*rowCloumSize, (i+1)*rowCloumSize));
			}
		} else {
			if (arr.size() / rowCloumSize == 0) {// 不够rowCloumSize列，直接全部字段生成一行
				createRow(searchAreaParam, arr);
			} else {
				for (int i = 0; i < arr.size() / rowCloumSize + 1; i++) {// 不是整数倍，分拨次生成多行，最后一行直接生成
					if (i == arr.size() / rowCloumSize) {
						createRow(searchAreaParam, arr.subList(i * rowCloumSize, arr.size()));
					} else {
						createRow(searchAreaParam, arr.subList(i * rowCloumSize, (i + 1) * rowCloumSize));
					}
				}
			}
		}
		/***********************************渲染表格部分*********************************************************************/
		actionString = actionString.replaceAll("_primaryKeyField_", primaryKey);
		actionString = actionString.replaceAll("_primaryKeyMemoField_", primaryKeyMemo);
		actionString = actionString.replaceAll("_UpdateFormParam_", searchAreaParam.toString());
		actionBuffer.append(actionString);
		saveToFile(actionBuffer);
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
	
	 /**
	   * 创建某行
	   */
	  private String createRow(StringBuffer searchAreaParam,List<Field> arr){
		 searchAreaParam.append("                      ").append("<tr>\r\n");
		 for (int i = 0; i < arr.size(); i++) {
			  createOneProperty(searchAreaParam, arr.get(i));
		 }
		 searchAreaParam.append("                      ").append("</tr>\r\n");
		 return searchAreaParam.toString();
	  }
	
	
	/**
	 * 创建某列字段
	 * @param sb
	 * @param field
	 * @return
	 * updateby 付为地 2017-07-04 添加表格字段的长度控制,针对数据库varchar比较长的字段做处理,生成textarea方式
	 *          数据库可为空的,默认全部都是非必填,不能为空这里全部都是必填
	 */
	private String createOneProperty(StringBuffer searchAreaParam,Field field){
		//form表单的标题
		searchAreaParam.append("                            ").append((new StringBuilder("<th>")).append(field.getFieldMemo()).append("</th>\r\n").toString());
		//form表单的内容，生成对应easyui控件
		if(field.getNullAble()==0){//不可为空
			//日期类型
			if(getDataType(field.getDataType()).equals("Date")){
				searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("'  name='"+field.getPropertyName()+"' class=\"required date\" readonly='true' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\"    value=\"<#if "+entity.getEntityBeanName()+"."+field.getPropertyName() +" ??>\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"?string('yyyy-MM-dd HH:mm:ss')\\}</#if>\"/></td>\r\n").toString());
			}
			//number类型
			else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
				searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("' name='"+field.getPropertyName()+"'  class=\"required number\" /></td>\r\n").toString());
			}
			//字符串类型
			else{
				 if(field.getLength()>=100){//超过100生成textarea
					 searchAreaParam.append("                            ").append((new StringBuilder("<td><textarea id='")).append(field.getPropertyName()).append("' name='"+field.getPropertyName()+"' value='\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}' class=\"{required:true,maxlength:"+field.getLength()+"}\"  rows=\"2\" >\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}</textarea></td>\r\n").toString());
				 }else{
					 searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("' name='"+field.getPropertyName()+"' value='\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}' class=\"{required:true,maxlength:"+field.getLength()+"}\"/></td>\r\n").toString());
				 }
			}
		}else{//可为空
			//日期类型
			if(getDataType(field.getDataType()).equals("Date")){//日期类型
				searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("'  name='"+field.getPropertyName()+"' class=\"required date\" readonly='true' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\"    value=\"<#if "+entity.getEntityBeanName()+"."+field.getPropertyName() +" ??>\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"?string('yyyy-MM-dd HH:mm:ss')\\}</#if>\"/></td>\r\n").toString());
			
			}
			//number类型
			else if(getDataType(field.getDataType()).equals("Integer")||getDataType(field.getDataType()).equals("double")||getDataType(field.getDataType()).equals("BigDecimal")){
				searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("'  name='"+field.getPropertyName()+"' value='\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}'/></td>\r\n").toString());
			}
			//字符串类型
			else{
				 if(field.getLength()>=100){//超过100生成textarea
					 searchAreaParam.append("                            ").append((new StringBuilder("<td><textarea id='")).append(field.getPropertyName()).append("'  name='"+field.getPropertyName()+"' value='\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}'  rows=\"2\" >\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}</textarea></td>\r\n").toString());
				 }else{
					 searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(field.getPropertyName()).append("' name='"+field.getPropertyName()+"' value='\\${"+entity.getEntityBeanName()+"."+field.getPropertyName()+"\\}' /></td>\r\n").toString());
				 }
			}
		}
		
		return searchAreaParam.toString();
	}
	
}
