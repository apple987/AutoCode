// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   EntityBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Field, ColumnConvert, 
//			Entity

public class EntityBuilder
	implements CodeBuilder
{

	private Entity entity;
	private Project project;

	public EntityBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}
    
	/**
	 * 将数据库字段类型jdbc映射为JavaObject类型
	 * @param iDataType
	 * @return
	 */
	private String getDataType(int iDataType)
	{
		//数据类型参考java.sql.Types
		String dataType = "";
		if (iDataType == Types.CHAR ||iDataType == Types.LONGNVARCHAR|| iDataType == Types.VARCHAR || iDataType == Types.NCHAR 
			|| iDataType == Types.NVARCHAR || iDataType == Types.LONGVARCHAR)
			dataType = "String";
		else if (iDataType == Types.INTEGER || iDataType == Types.BIGINT  || 
		    iDataType == Types.TINYINT || iDataType == Types.SMALLINT|| 
		    iDataType == Types.NUMERIC)
			dataType = "Integer";
		else if (iDataType == Types.DOUBLE   || iDataType == Types.FLOAT || iDataType == Types.REAL)
			dataType = "double";
		else if (iDataType == Types.DATE||iDataType == Types.TIME||iDataType == Types.TIMESTAMP)
			dataType = "Date";
		else if (iDataType == Types.BIT || iDataType == Types.BOOLEAN)
			dataType = "Boolean";
		else if (iDataType == Types.BLOB||iDataType == Types.CLOB
		       ||iDataType == Types.BINARY||iDataType == Types.VARBINARY
		       ||iDataType == Types.LONGVARBINARY)
			dataType = "byte[]";
		else if(iDataType==Types.NUMERIC||iDataType==Types.DECIMAL){
			dataType = "BigDecimal";
		}
		return dataType;
	}
	public String createSetter(Field field)
	{
		StringBuffer codeBuffer = new StringBuffer();
		codeBuffer.append((new StringBuilder("    public void set")).append(ColumnConvert.getGetSetterNameByProp(field.getPropertyName())).append("(").append(getDataType(field.getDataType())).append(" ").append(field.getPropertyName()).append(") {\r\n").toString());
		codeBuffer.append((new StringBuilder("        this.")).append(field.getPropertyName()).append(" = ").append(field.getPropertyName()).append(";\r\n").toString());
		codeBuffer.append("    }\r\n\r\n");
		return codeBuffer.toString();
	}

	public String createGetter(Field field)
	{
		StringBuffer codeBuffer = new StringBuffer();
		codeBuffer.append((new StringBuilder("    public ")).append(getDataType(field.getDataType())).append(" get").append(ColumnConvert.getGetSetterNameByProp(field.getPropertyName())).append("() {\r\n").toString());
		codeBuffer.append((new StringBuilder("        return this.")).append(field.getPropertyName()).append(";\r\n").toString());
		codeBuffer.append("    }\r\n\r\n");
		return codeBuffer.toString();
	}

	@SuppressWarnings("unused")
	public void saveToFile()
	{
		StringBuffer codeBuffer = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		codeBuffer.append((new StringBuilder("package ")).append(project.getEntitiesPackage()).append(";\r\n\r\n").toString());
		codeBuffer.append("import java.util.Date;\r\n\r\n");
		codeBuffer.append("import com.qdone.framework.core.page.MutiSort;\r\n\r\n");
		/*codeBuffer.append("import java.io.Serializable;\r\n\r\n");*/
		/*codeBuffer.append("import com.fasterxml.jackson.annotation.JsonFormat;\r\n\r\n");*/
		codeBuffer.append("import org.springframework.format.annotation.DateTimeFormat;\r\n\r\n");
		codeBuffer.append("import io.swagger.annotations.ApiModel;\r\n\r\n");
		codeBuffer.append("import io.swagger.annotations.ApiModelProperty;\r\n\r\n");
		codeBuffer.append("/**\r\n");
		codeBuffer.append("  *该代码由付为地的编码机器人自动生成\r\n");
		codeBuffer.append((new StringBuilder("  *时间：")).append(sdf.format(new Date())).append("\r\n").toString());
		codeBuffer.append("*/\r\n");
		/***添加swagger实体描述注解**/
		codeBuffer.append("@ApiModel(value = \""+entity.getTableMemo()+"\", description = \""+entity.getEntityName()+"实体类\") \r\n");
		codeBuffer.append((new StringBuilder("public class ")).append(entity.getEntityName()).append("  extends MutiSort ").append("{\r\n\r\n").toString());
		codeBuffer.append("    private static final long serialVersionUID = 1L;\r\n\r\n");
		codeBuffer.append("    // Fields\r\n\r\n");
		codeBuffer.append("    @ApiModelProperty(value = \""+entity.getPrimaryKeyField().getFieldMemo()+"\", required = true) \r\n");
		codeBuffer.append((new StringBuilder("    private ")).append(getDataType(entity.getPrimaryKeyField().getDataType())).append(" ").append(entity.getPrimaryKeyField().getPropertyName()).append(";\r\n").toString());
		int iFieldCount = entity.getFields().size();
		for (int i = 0; i < iFieldCount; i++)
		{
			Field field = (Field)entity.getFields().get(i);
			//日期格式
			if(getDataType(field.getDataType()).equalsIgnoreCase("Date")){//日期格式添加 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh" , timezone="GMT+8")
				codeBuffer.append((new StringBuilder("    @DateTimeFormat(pattern=\"yyyy-MM-dd HH:mm:ss\")")).append("\r\n").toString());
			}
			/**添加SWAGGER接口,字段注解描述,非必需选择**/
			if(field.getNullAble()==1){//0不可以为空，1可为空，2不知道
				codeBuffer.append("    @ApiModelProperty(value = \""+field.getFieldMemo()+"\", required = false) \r\n");
			}else{
				codeBuffer.append("    @ApiModelProperty(value = \""+field.getFieldMemo()+"\", required = true) \r\n");
			}
			codeBuffer.append((new StringBuilder("    private ")).append(getDataType(field.getDataType())).append(" ").append(field.getPropertyName()).append(";").toString());
			//属性后面添加备注信息
			if(!StringUtils.isEmpty(field.getFieldMemo())){
				codeBuffer.append("//").append(field.getFieldMemo()).append("\r\n");
			}
			if(field==null&&field.getFieldMemo()!=null&&field.getFieldMemo()!=""&&!field.getFieldMemo().equals(""))
			codeBuffer.append("\r\n");
			
			
		}

		codeBuffer.append("    \r\n");
		codeBuffer.append("    // Constructors\r\n\r\n");
		codeBuffer.append("    /** default constructor */\r\n");
		codeBuffer.append((new StringBuilder("    public ")).append(entity.getEntityName()).append("() {\r\n").toString());
		codeBuffer.append("    }\r\n\r\n");
		codeBuffer.append("    // Property accessors\r\n\r\n");
		codeBuffer.append(createSetter(entity.getPrimaryKeyField()));
		//主键如果为日期格式
		if(getDataType(entity.getPrimaryKeyField().getDataType()).equalsIgnoreCase("Date")){//日期格式添加 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh" , timezone="GMT+8")
			codeBuffer.append((new StringBuilder("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", locale = \"zh\" , timezone=\"GMT+8\") ")).append(";\r\n").toString());
		}
		codeBuffer.append(createGetter(entity.getPrimaryKeyField()));
		for (int i = 0; i < iFieldCount; i++)
		{
			Field field = (Field)entity.getFields().get(i);
			codeBuffer.append(createSetter(field));
			//其他属性
			/*if(getDataType(field.getDataType()).equalsIgnoreCase("Date")){//日期格式添加 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh" , timezone="GMT+8")
				codeBuffer.append((new StringBuilder("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", locale = \"zh\" , timezone=\"GMT+8\") ")).append(";\r\n").toString());
			}*/
			codeBuffer.append(createGetter(field));
		}

		codeBuffer.append("}");
		String fileName = (new StringBuilder(String.valueOf(project.getEntitiesPath()))).append(entity.getEntityName()).append(".java").toString();
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
}
