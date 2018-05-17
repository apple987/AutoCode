// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Entity.java

package com.qdone.radmodel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Referenced classes of package com.qdone.radmodel:
//			ColumnConvert, Field

public class Entity
{

	private String entityName;
	private String primaryKeyFieldName;
	private String primaryKeyPropertyName;
	private Connection connection;
	private String tableName;
	private Field primaryKeyField;
	private ArrayList<Field> fields;
	private String entityBeanName;
	private String selectSqlName;
	private String selectViewName;
	private String insertSqlName;
	private String updateSqlName;
	private String deleteSqlName;
	private String querySqlName;
	private String batchInsertSqlName;
	private String batchUpdateSqlName;
	private String batchDeleteSqlName;
	@SuppressWarnings("rawtypes")
	private List mysqlCommentList;
	private static String dbType;//本次执行数据库类型
	private String tableMemo;//本次执行数据库表名备注

	@SuppressWarnings("rawtypes")
	public Entity(Connection connection, String tableName)
	{
		entityName = ColumnConvert.getJavaBeanNameBy(tableName);
		this.connection = connection;
		this.tableName = tableName;
		fields = new ArrayList<Field>();
		mysqlCommentList=new ArrayList();
		entityBeanName = entityName.substring(0, 1).toLowerCase()+entityName.substring(1);
		selectSqlName = "select";
		selectViewName="view";
		insertSqlName = "insert";
		updateSqlName = "update";
		deleteSqlName = "delete";
		querySqlName="queryBean";
		batchInsertSqlName="batchInsert";
		batchUpdateSqlName="batchUpdate";
		batchDeleteSqlName="batchDelete";
		tableMemo="";//表的备注
		return;
	}
	 
	public void parseEntityInfo()
		throws SQLException
	{
		//执行查询数据
		Statement stmt = connection.createStatement();
		ResultSet result = stmt.executeQuery((new StringBuilder("select * from ")).append(tableName).append(" where 1 = 2 ").toString());
		ResultSetMetaData metData = result.getMetaData();
		int ColumnCount = metData.getColumnCount();
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		ResultSet priKeySet = databaseMetaData.getPrimaryKeys(null, null, tableName);
		//每一次查询都重新开启一个连接
		//获取mysql数据库的字段注释信息,本处一次性查询出所有字段备注信息，存入hashMap需要比对时，自动循环比对(key全部大写)
		//判断数据库类型
		System.err.println("连接数据库类型是:"+databaseMetaData.getDatabaseProductName());
		dbType=databaseMetaData.getDatabaseProductName();
		//执行匹配的数据库查询对应的数据库字段备注信息
		if(dbType.equalsIgnoreCase("mysql")){//MYSQL数据库处理
			/*查询表字段备注信息*/
			Statement stmtCommont = connection.createStatement();
			String mysqlCommont="SELECT COLUMN_NAME , DATA_TYPE , COLUMN_COMMENT  FROM INFORMATION_SCHEMA.COLUMNS ";
			mysqlCommont+=" WHERE table_name = '"+tableName+"' ";
			mysqlCommont+=" AND table_schema = '"+metData.getCatalogName(1)+"' ";
			//mysqlCommont+=" AND column_name  = '"+tableName+"' ";
			ResultSet mysqlResult = stmtCommont.executeQuery(mysqlCommont);
			resultSetToList(mysqlResult);
			mysqlResult.close();
			stmtCommont.close();
			/*查询表名称备注信息*/
			Statement tabStmtCommont = connection.createStatement();
			String tabMysqlCommont="SELECT TABLE_NAME,TABLE_COMMENT  FROM INFORMATION_SCHEMA. TABLES";
			tabMysqlCommont+=" WHERE table_name = '"+tableName+"' ";
			tabMysqlCommont+=" AND table_schema = '"+metData.getCatalogName(1)+"' ";
			ResultSet tabMysqlResult = tabStmtCommont.executeQuery(tabMysqlCommont);
			resultTabSetToList(tabMysqlResult);
			tabMysqlResult.close();
			tabStmtCommont.close();
			
		}
		if(dbType.equalsIgnoreCase("oracle")){//ORACLE数据库处理
			Statement stmtCommont = connection.createStatement();
			String mysqlCommont="SELECT COLUMN_NAME ,COMMENTS AS COLUMN_COMMENT  FROM ALL_COL_COMMENTS ";
			mysqlCommont+=" WHERE TABLE_NAME = '"+tableName.toUpperCase()+"' ";
            //mysqlCommont+=" AND ONWER = '"+metData.getCatalogName(1)+"' ";
			//mysqlCommont+=" AND column_name  = '"+tableName+"' ";
			ResultSet mysqlResult = stmtCommont.executeQuery(mysqlCommont);
			resultSetToList(mysqlResult);
			mysqlResult.close();
			stmtCommont.close();
			/*查询表名称备注信息*/
			Statement tabStmtCommont = connection.createStatement();
			String tabMysqlCommont="select TABLE_NAME,TABLE_TYPE,COMMENTS FROM USER_TAB_COMMENTS";
			tabMysqlCommont+=" WHERE TABLE_NAME = '"+tableName.toUpperCase()+"' ";
			tabMysqlCommont+="   AND table_type ='TABLE' ";
			ResultSet tabMysqlResult = tabStmtCommont.executeQuery(tabMysqlCommont);
			resultOracleTabSetToList(tabMysqlResult);
			tabMysqlResult.close();
			tabStmtCommont.close();
		}
		
		//正常逻辑处理
		if (priKeySet.next())
		{
			primaryKeyFieldName = priKeySet.getString(4);
			primaryKeyPropertyName = ColumnConvert.getJavaBeanPropsNameBy(primaryKeyFieldName);
			primaryKeyField = new Field(primaryKeyFieldName, primaryKeyPropertyName, true);
			if(mysqlCommentList.size()>0){//执行过查询的备注信息(Mysql,Oracle)
				String pkMemo=getFieldMemo(primaryKeyFieldName);//不写备注，默认生成属性名称
				if(!getNotNullStr(pkMemo).equalsIgnoreCase("")){
					primaryKeyField.setFieldMemo(pkMemo);
				}else{
					primaryKeyField.setFieldMemo(primaryKeyPropertyName);
				}
			}else{
				primaryKeyField.setFieldMemo(primaryKeyPropertyName);//其他数据库暂时让备注跟实体bean的属性名称一致
			}
		}
		priKeySet.close();
		for (int i = 1; i <= ColumnCount; i++)
		{
			Object dataType=metData.getColumnClassName(i);
			String fieldName = metData.getColumnName(i);
			int iColumnType = metData.getColumnType(i);
			String iColumnTypeName=metData.getColumnTypeName(i);
			//System.err.println("java字段名称:"+dataType+"\t 数据库类型:"+iColumnTypeName+" \t 字段名称: \t "+fieldName+" \t 数据长度: \t "+metData.getColumnDisplaySize(i));
			if (!fieldName.equalsIgnoreCase(primaryKeyFieldName))
			{
				String propertyName = ColumnConvert.getJavaBeanPropsNameBy(fieldName);
				Field field = new Field(fieldName, propertyName, false);
				field.setDataType(iColumnType);
				field.setDateTypeName(iColumnTypeName);
				field.setJavaDataType(dataType);
				field.setNullAble(metData.isNullable(i));//是不是可以为空
				field.setLength(metData.getColumnDisplaySize(i));
				if(mysqlCommentList.size()>0){//执行过查询的备注信息(Mysql,Oracle)
					String clumMemo=getFieldMemo(fieldName);//不写备注，默认生成属性名称
					if(!getNotNullStr(clumMemo).equalsIgnoreCase("")){
						field.setFieldMemo(clumMemo);
					}else{
						field.setFieldMemo(propertyName);
					}
				}else{
					field.setFieldMemo(propertyName);//其他数据库暂时让数据库字段备注跟该字段的属性名称一致
				}
				fields.add(field);
			}else if (primaryKeyField != null){
				primaryKeyField.setDataType(iColumnType);
				primaryKeyField.setDateTypeName(iColumnTypeName);
				primaryKeyField.setNullAble(metData.isNullable(i));//主键是不是允许为空
			}
				
		}

		result.close();
		stmt.close();
	}

	public Field getPrimaryKeyField()
	{
		return primaryKeyField;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public String getTableName()
	{
		return tableName;
	}

	public ArrayList<Field> getFields()
	{
		return fields;
	}

	public String getEntityBeanName()
	{
		return entityBeanName;
	}

	public String getSelectSqlName()
	{
		return selectSqlName;
	}


	public String getInsertSqlName()
	{
		return insertSqlName;
	}

	public String getUpdateSqlName()
	{
		return updateSqlName;
	}

	public String getDeleteSqlName()
	{
		return deleteSqlName;
	}
	
	
	public String getSelectViewName() {
		return selectViewName;
	}

	public void setSelectViewName(String selectViewName) {
		this.selectViewName = selectViewName;
	}

	public String getQuerySqlName() {
		return querySqlName;
	}

	public void setQuerySqlName(String querySqlName) {
		this.querySqlName = querySqlName;
	}

	
	public String getBatchInsertSqlName() {
		return batchInsertSqlName;
	}

	public void setBatchInsertSqlName(String batchInsertSqlName) {
		this.batchInsertSqlName = batchInsertSqlName;
	}

	public String getBatchUpdateSqlName() {
		return batchUpdateSqlName;
	}

	public void setBatchUpdateSqlName(String batchUpdateSqlName) {
		this.batchUpdateSqlName = batchUpdateSqlName;
	}

	public String getBatchDeleteSqlName() {
		return batchDeleteSqlName;
	}

	public void setBatchDeleteSqlName(String batchDeleteSqlName) {
		this.batchDeleteSqlName = batchDeleteSqlName;
	}
	
	public String getTableMemo() {
		return tableMemo;
	}

	public void setTableMemo(String tableMemo) {
		this.tableMemo = tableMemo;
	}

	/**
	 * Mybatis的JdbcType映射为javaType
	 * @param iDataType
	 * @return
	 */
	@SuppressWarnings("unused")
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
	/**
	 * 将Mysql查询的表字段备注信息,转换成存储表信息的HashMap其中的key全部大写
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private  List resultSetToList(ResultSet rs) throws java.sql.SQLException {   
        if (rs == null)   
            return Collections.EMPTY_LIST;   
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
        Map rowData = new HashMap();   
        while (rs.next()) {   
         rowData = new HashMap(columnCount);   
         for (int i = 1; i <= columnCount; i++) {   
                 rowData.put(md.getColumnName(i), rs.getObject(i));   
         }   
         mysqlCommentList.add(rowData);   
        }   
        return mysqlCommentList;   
   }  
	
	
	/**
	 * 将Mysql查询的表名称备注信息,保存
	 * @param rs
	 * @return
	 */
	private  List<?> resultTabSetToList(ResultSet rs) throws java.sql.SQLException {   
        if (rs == null)   
            return Collections.EMPTY_LIST;   
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
        while (rs.next()) {   
         for (int i = 1; i <= columnCount; i++) { 
        	   if(i==2){
        		   tableMemo=(String) rs.getObject(i);
        	   }
         }   
         /*mysqlCommentList.add(rowData); */ 
        }   
        return null;   
   }  
	
	/**
	 * 将Oracle查询的表名称备注信息,保存
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	private  List<?> resultOracleTabSetToList(ResultSet rs) throws java.sql.SQLException {   
        if (rs == null)   
            return Collections.EMPTY_LIST;   
        ResultSetMetaData md = rs.getMetaData(); //得到结果集(rs)的结构信息，比如字段数、字段名等   
        int columnCount = md.getColumnCount(); //返回此 ResultSet 对象中的列数   
        while (rs.next()) {   
         for (int i = 1; i <= columnCount; i++) { 
        	   if(i==3){
        		   tableMemo=(String) rs.getObject(i);
        	   }
         }   
         /*mysqlCommentList.add(rowData); */ 
        }   
        return null;   
   }  
	
	
	
    /**
     * mysql数据库通过，一个数据库表的字段名称获取对应的字段备注信息
     * @param mysqlMemoList
     * @param filedName
     * @return
     */
	@SuppressWarnings({ "rawtypes" })
	private  String getFieldMemo(String filedName){
		String fieldMemo="";
		try {
			if(mysqlCommentList!=null&&mysqlCommentList.size()>0){
				for (int i = 0; i < mysqlCommentList.size(); i++) {
					Map mysqlMap=(Map) mysqlCommentList.get(i);
					if(mysqlMap.containsKey("COLUMN_NAME")){
						String columnName=(String) mysqlMap.get("COLUMN_NAME");
						if(columnName.equalsIgnoreCase(filedName)){
							fieldMemo=(String) mysqlMap.get("COLUMN_COMMENT");
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fieldMemo;
	}
	/**
	 * 返回非空字符串 
	 * @param obj
	 * @return
	 */
	private  String getNotNullStr(Object obj){
		return (obj==null||obj.toString().length()==0)?"":obj.toString();
	}
} 
