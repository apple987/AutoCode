// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   IBatisBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity, Field

public class IBatisBuilder
	implements CodeBuilder
{

	private Project project;

	public IBatisBuilder(Project project)
	{
		this.project = project;
	}

	public void saveToFile(String iBatisConfigContent)
	{
		String fileName = (new StringBuilder(String.valueOf(project.getMapperPath()))).append(project.getEntity().getEntityName()).append(".xml").toString();
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		try
		{
			FileUtils.writeStringToFile(file, iBatisConfigContent, "utf-8");
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
		springConfigBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		springConfigBuffer.append("<!DOCTYPE mapper\r\n");
		springConfigBuffer.append("    PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\r\n");
		springConfigBuffer.append("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n\r\n");
//		springConfigBuffer.append((new StringBuilder("<mapper namespace=\"")).append(project.getDaoPackage()).append(".I").append(entity.getEntityName()).append("Dao\">\r\n\r\n").toString());
		springConfigBuffer.append((new StringBuilder("<mapper namespace=\"")).append(project.getMapperPackage()).append(".").append(entity.getEntityName()).append("\">\r\n\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <resultMap id=\"")).append(entity.getEntityName()).append("Map\" type=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("        <result property=\"")).append(entity.getPrimaryKeyField().getPropertyName()).append("\" column=\"").append(entity.getPrimaryKeyField().getFieldName().toUpperCase()).append("\"/>\r\n").toString());
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			Field field = (Field)entity.getFields().get(i);
			springConfigBuffer.append((new StringBuilder("        <result property=\"")).append(field.getPropertyName()).append("\" column=\"").append(field.getFieldName().toUpperCase()).append("\"/>\r\n").toString());
		}

		springConfigBuffer.append("    </resultMap>\r\n\r\n");
		springConfigBuffer.append("    <sql id=\"allColumns\">\r\n");
		springConfigBuffer.append((new StringBuilder("        ")).append(entity.getPrimaryKeyField().getFieldName().toUpperCase()).toString());
		int count = 1;
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			if (count % 6 == 0)
				springConfigBuffer.append((new StringBuilder(",\r\n        ")).append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).toString());
			else
				springConfigBuffer.append((new StringBuilder(",")).append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).toString());
			count++;
		}

		springConfigBuffer.append("\r\n    </sql>\r\n\r\n");
		springConfigBuffer.append("    <sql id=\"dynamicWhere\">\r\n");
		springConfigBuffer.append("   	   <trim  suffixOverrides=\",\" prefix=\"WHERE\" prefixOverrides=\"AND\">\r\n");
		/*生成主键dynamicWhere*/
		String primaryKey=entity.getPrimaryKeyField().getPropertyName();
//		springConfigBuffer.append("          <if test=\"null!=").append(primaryKey).append(" and ''!=").append(primaryKey).append("\">\r\n");
		/*判断数据类型,如果是Date就改成<if test="beginDate!=null">*/
		//主键查询策略
		if(getDataType(entity.getPrimaryKeyField().getDataType()).equals("Date")){
			springConfigBuffer.append("          <if test=\"").append(primaryKey).append("!=null\">\r\n");
			springConfigBuffer.append("              AND ").append(entity.getPrimaryKeyField().getFieldName().toUpperCase()).append(" = #{").append(primaryKey).append("}\r\n");
			springConfigBuffer.append("          </if>\r\n");
		}else{
			springConfigBuffer.append("          <if test=\"").append(primaryKey).append("!=null and ").append(primaryKey).append("!=''\">\r\n");
			springConfigBuffer.append("              AND ").append(entity.getPrimaryKeyField().getFieldName().toUpperCase()).append(" = #{").append(primaryKey).append("}\r\n");
			springConfigBuffer.append("          </if>\r\n");
		}
		
		for (int i = 0; i < entity.getFields().size(); i++)
		{
//			springConfigBuffer.append("          <if test=\"null!=").append(propertyName).append(" and ''!=").append(propertyName).append("\">\r\n");
//			springConfigBuffer.append("          <if test=\"").append(propertyName).append("!=null\">\r\n");
			Field field=entity.getFields().get(i);
			String propertyName=field.getPropertyName();
			if(getDataType(field.getDataType()).equals("Date")){
				springConfigBuffer.append("          <if test=\"").append(propertyName).append("!=null\">\r\n");
				springConfigBuffer.append("             AND ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append("}\r\n");
				springConfigBuffer.append("          </if>\r\n");
			}else if(getDataType(field.getDataType()).equals("String")){//字符串类型,默认采用like查询
				springConfigBuffer.append("          <if test=\"").append(propertyName).append("!=null and ").append(propertyName).append("!=''\">\r\n");
				springConfigBuffer.append("             AND ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" LIKE concat('%',#{").append(((Field)entity.getFields().get(i)).getPropertyName()).append("},'%')\r\n");
				springConfigBuffer.append("          </if>\r\n");
			}else{
				springConfigBuffer.append("          <if test=\"").append(propertyName).append("!=null and ").append(propertyName).append("!=''\">\r\n");
				springConfigBuffer.append("             AND ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append("}\r\n");
				springConfigBuffer.append("          </if>\r\n");
			}
		}
		springConfigBuffer.append("       </trim>\r\n");
		springConfigBuffer.append("    </sql>\r\n\r\n");
		//select
		
		
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getSelectSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <select id=\"")).append(entity.getSelectSqlName()).append("\" parameterType=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\"").append("  resultMap=\"").append(entity.getEntityName()).append("Map\">\r\n").toString());
		springConfigBuffer.append("      SELECT <include refid=\"allColumns\" />\r\n").append((new StringBuilder("      FROM ")).append(entity.getTableName()).append("  <include refid=\"dynamicWhere\" />  \r\n"));
		springConfigBuffer.append("    </select>\r\n\r\n");
		
		//view
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getSelectViewName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <select id=\"")).append(entity.getSelectViewName()).append("\" parameterType=\"").append(getDataTypeClass(entity.getPrimaryKeyField().getDataType())+"\"").append("  resultMap=\"").append(entity.getEntityName()).append("Map\">\r\n").toString());
		springConfigBuffer.append("      SELECT <include refid=\"allColumns\" />\r\n").append((new StringBuilder("      FROM ")).append(entity.getTableName()).append(" WHERE ").toString()).append(entity.getPrimaryKeyField().getFieldName()).append((new StringBuilder(" = #{")).append(entity.getPrimaryKeyField().getPropertyName()).append(",jdbcType=").append(getJdbcType(entity.getPrimaryKeyField().getDataType())).append("}").toString()).append("\r\n");
		springConfigBuffer.append("    </select>\r\n\r\n");
		
		//query
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getQuerySqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <select id=\"")).append(entity.getQuerySqlName()).append("\" parameterType=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\"").append("  resultMap=\"").append(entity.getEntityName()).append("Map\">\r\n").toString());
		springConfigBuffer.append("      SELECT <include refid=\"allColumns\" />\r\n").append((new StringBuilder("      FROM ")).append(entity.getTableName()).append("  <include refid=\"dynamicWhere\" /> \r\n"));
		springConfigBuffer.append("    </select>\r\n\r\n");
			
		
		//batchInsert
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getBatchInsertSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <select id=\"")).append(entity.getBatchInsertSqlName()).append("\" parameterType=\"java.util.List").append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      INSERT INTO ")).append(entity.getTableName()).append(" (<include refid=\"allColumns\" />) \r\n").toString());
		springConfigBuffer.append((new StringBuilder("          VALUES(\r\n    ")));
		springConfigBuffer.append((new StringBuilder("      <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\"> \r\n " )));
		springConfigBuffer.append((new StringBuilder("           (\r\n                #{item.")).append(entity.getPrimaryKeyField().getPropertyName()).append(",jdbcType=").append(getJdbcType(entity.getPrimaryKeyField().getDataType())).append("}").toString());
		count = 1;
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			if (count % 6 == 0)
				springConfigBuffer.append((new StringBuilder(",\r\n        #{item.")).append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}").toString());
			else
			    springConfigBuffer.append((new StringBuilder(",#{item.")).append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}").toString());
			count++;
		}
		springConfigBuffer.append("      \r\n            )");
		springConfigBuffer.append("      \r\n          </foreach>\r\n");
		springConfigBuffer.append("    </select>\r\n\r\n");
		
		
		//insert
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getInsertSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <insert id=\"")).append(entity.getInsertSqlName()).append("\" parameterType=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      INSERT INTO ")).append(entity.getTableName()).append(" (<include refid=\"allColumns\" />) \r\n").toString());
		springConfigBuffer.append((new StringBuilder("      VALUES(\r\n        #{")).append(entity.getPrimaryKeyField().getPropertyName()).append(",jdbcType=").append(getJdbcType(entity.getPrimaryKeyField().getDataType())).append("}").toString());
		count = 1;
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			if (count % 6 == 0)
				springConfigBuffer.append((new StringBuilder(",\r\n        #{")).append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}").toString());
			else
			    springConfigBuffer.append((new StringBuilder(",#{")).append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}").toString());
			count++;
		}

		springConfigBuffer.append("\r\n        )\r\n");
		springConfigBuffer.append("    </insert>\r\n\r\n");
		//update
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getUpdateSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <update id=\"")).append(entity.getUpdateSqlName()).append("\" parameterType=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      UPDATE  ")).append(entity.getTableName()).append("  \r\n").toString());
		springConfigBuffer.append((new StringBuilder("         <set>  ")).append("  \r\n").toString());
		String strSQL = "";
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			String propertyName=((Field)entity.getFields().get(i)).getPropertyName();
			if(i==entity.getFields().size()-1){
//				springConfigBuffer.append("          <if test=\"null!=").append(propertyName).append("\">\r\n");
				//update时,判断数据库非空字段加一个if test!="null" 0不可以为空，1可为空，2不知道
				if(entity.getFields().get(i).getNullAble()==1){//可为空时，不加if test ！=null
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}\r\n").toString();
				}else if(entity.getFields().get(i).getNullAble()==0){//不可为空
					if(getDataType(entity.getFields().get(i).getDataType()).equals("Date")){//日期类型,不写beginDate!=''
						strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}</if>\r\n").toString();
					}else{
						strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}</if>\r\n").toString();
					}
				}else{//默认都是不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}</if>\r\n").toString();
				}
			}else{
//				strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").toString();
//				strSQL = (new StringBuilder(String.valueOf(strSQL))).append(propertyName).append("!=null and ").append(propertyName).append("!=''\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append("},</if>\r\n").toString();
				//update时,判断数据库非空字段加一个if test!="null" 0不可以为空，1可为空，2不知道
				if(entity.getFields().get(i).getNullAble()==1){//可为空时，不加if test ！=null
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},\r\n").toString();
				}else if(entity.getFields().get(i).getNullAble()==0){//不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},</if>\r\n").toString();
				}else{//默认都是不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},</if>\r\n").toString();
				}
			}
		}
		springConfigBuffer.append(strSQL);
		springConfigBuffer.append("         </set>\r\n").toString();
		springConfigBuffer.append("		WHERE ").append(entity.getPrimaryKeyField().getFieldName()).append((new StringBuilder(" = #{")).
				append(entity.getPrimaryKeyField().getPropertyName()).append(",jdbcType=").append(getJdbcType(entity.getPrimaryKeyField().getDataType())).append("}").toString()).append("\r\n");
		springConfigBuffer.append("    </update>\r\n\r\n").toString();
		
		//batchUpdate
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getBatchUpdateSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <update id=\"")).append(entity.getBatchUpdateSqlName()).append("\" parameterType=\"java.util.Map").append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      UPDATE  ")).append(entity.getTableName()).append("  \r\n").toString());
		springConfigBuffer.append((new StringBuilder("         <set>  ")).append("  \r\n").toString());
	    strSQL = "";//置空
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			String propertyName=((Field)entity.getFields().get(i)).getPropertyName();
			if(i==entity.getFields().size()-1){
//				springConfigBuffer.append("          <if test=\"null!=").append(propertyName).append("\">\r\n");
				//update时,判断数据库非空字段加一个if test!="null" 0不可以为空，1可为空，2不知道
				if(entity.getFields().get(i).getNullAble()==1){//可为空时，不加if test ！=null
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}\r\n").toString();
				}else if(entity.getFields().get(i).getNullAble()==0){//不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}</if>\r\n").toString();
				}else{//默认都是不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("}</if>\r\n").toString();
				}
			}else{
//				strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").toString();
//				strSQL = (new StringBuilder(String.valueOf(strSQL))).append(propertyName).append("!=null and ").append(propertyName).append("!=''\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append("},</if>\r\n").toString();
				//update时,判断数据库非空字段加一个if test!="null" 0不可以为空，1可为空，2不知道
				if(entity.getFields().get(i).getNullAble()==1){//可为空时，不加if test ！=null
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             ").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},\r\n").toString();
				}else if(entity.getFields().get(i).getNullAble()==0){//不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},</if>\r\n").toString();
				}else{//默认都是不可为空
					strSQL = (new StringBuilder(String.valueOf(strSQL))).append("             <if test=\"").append(propertyName).append("!=null\">").append(((Field)entity.getFields().get(i)).getFieldName().toUpperCase()).append(" = #{").append(((Field)entity.getFields().get(i)).getPropertyName()).append(",jdbcType=").append(getJdbcType(((Field)entity.getFields().get(i)).getDataType())).append("},</if>\r\n").toString();
				}
			}
		}
		springConfigBuffer.append(strSQL);
		springConfigBuffer.append("         </set>\r\n").toString();
		springConfigBuffer.append("		WHERE ").append(entity.getPrimaryKeyField().getFieldName()).append(" IN \r\n");
		/*<foreach collection="pkList" index="index" item="item" open="(" separator="," close=")">   
        #{item}   
    </foreach>*/  
		springConfigBuffer.append("      <foreach collection=\"pkList\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> \r\n");	
		springConfigBuffer.append("        #{item}   ").append("\r\n");
		springConfigBuffer.append("      </foreach>").append("\r\n");
		springConfigBuffer.append("    </update>\r\n\r\n").toString();
		
		
		
		//delete
//		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getDeleteSqlName()).append(" -->\r\n").toString());
//		springConfigBuffer.append((new StringBuilder("    <delete id=\"")).append(entity.getDeleteSqlName()).append("\" parameterType=\"").append(project.getEntitiesPackage()).append(".").append(entity.getEntityName()).append("\">\r\n").toString());
//		springConfigBuffer.append((new StringBuilder("      DELETE FROM ")).append(entity.getTableName())).append(" WHERE "+entity.getPrimaryKeyField().getPropertyName()+" = #{"+entity.getPrimaryKeyField().getPropertyName()+"}").append("\r\n");
//		springConfigBuffer.append("    </delete>\r\n\r\n");
		
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getDeleteSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <delete id=\"")).append(entity.getDeleteSqlName()).append("\" parameterType=\"").append(getDataTypeClass(entity.getPrimaryKeyField().getDataType())).append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      DELETE FROM ")).append(entity.getTableName())).append(" WHERE "+entity.getPrimaryKeyField().getFieldName()+" = #{"+entity.getPrimaryKeyField().getPropertyName()+",jdbcType="+getJdbcType(entity.getPrimaryKeyField().getDataType())+"}").append("\r\n");
		
		springConfigBuffer.append("    </delete>\r\n\r\n");
		//batchDelete
	    
		springConfigBuffer.append((new StringBuilder("    <!-- ")).append(entity.getBatchDeleteSqlName()).append(" -->\r\n").toString());
		springConfigBuffer.append((new StringBuilder("    <delete id=\"")).append(entity.getBatchDeleteSqlName()).append("\" parameterType=\"java.util.List").append("\">\r\n").toString());
		springConfigBuffer.append((new StringBuilder("      DELETE FROM ")).append(entity.getTableName())).append("  WHERE "+entity.getPrimaryKeyField().getFieldName()+ "  IN  \r\n");
		springConfigBuffer.append("       <foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">  \r\n ");
		springConfigBuffer.append("          #{item."+entity.getPrimaryKeyField().getFieldName()+","+"jdbcType="+getJdbcType(entity.getPrimaryKeyField().getDataType())+"}\r\n");
		springConfigBuffer.append("       </foreach> \r\n");
		  
		springConfigBuffer.append("    </delete>\r\n\r\n");
		
		springConfigBuffer.append("</mapper>");
		saveToFile(springConfigBuffer.toString());
	}
	/**
	 * 数据库类型对应到具体的数据库类型
	 */
	private String getDataTypeClass(int iDataType)
	{
		//数据类型参考java.sql.Types
		String dataType = "";
		if (iDataType == 12 || iDataType == 1 || iDataType == -1 || iDataType == 2005)
			dataType = "java.lang.String";
		else
		if (iDataType == 4 || iDataType == -5 || iDataType == -7 || iDataType == -6 || iDataType == 2)
			dataType = "java.lang.Integer";
		else
		if (iDataType == 8 || iDataType == 6)
			dataType = "java.lang.Double";
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
	/**
	 * 返回数据库字段的Jdbc类型
	 * @param iDataType
	 * @return
	 */
	private String getJdbcType(int iDataType){
		String result="null";
		Map<Integer,String> jdbcMap=project.getJdbcMap();
		Iterator<?> it=jdbcMap.keySet().iterator();
		while(it.hasNext()){
			int key=(Integer) it.next();
			if(iDataType==key){
				result=jdbcMap.get(key);
				break;
			}
		}
		return result.toUpperCase();
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
