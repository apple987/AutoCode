// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Field.java

package com.qdone.radmodel;


public class Field
{

	private String fieldName;
	private String propertyName;
	private int dataType;
	private String dateTypeName;
	private boolean primaryKey;
	private Object javaDataType;
	private int nullAble;//是不是可为空  0不可为空，1可为空 ,2不知道
	private String fieldMemo;//字段数据库备注信息
	private int length;//字段长度,针对字符串处理比如:varchar(50)
	

	public Field(String fieldName, String propertyName, boolean primaryKey)
	{
		this.fieldName = fieldName;
		this.primaryKey = primaryKey;
		this.propertyName = propertyName;
	}
	
	public Field(String fieldName, String propertyName, boolean primaryKey,String fieldMemo)
	{
		this.fieldName = fieldName;
		this.primaryKey = primaryKey;
		this.propertyName = propertyName;
		this.fieldMemo=fieldMemo;
	}
	public Field(String fieldName, String propertyName, boolean primaryKey,int nullAble,String fieldMemo)
	{
		this.fieldName = fieldName;
		this.primaryKey = primaryKey;
		this.propertyName = propertyName;
		this.nullAble = nullAble;
	}
	public String getFieldName()
	{
		return fieldName;
	}

	public boolean isPrimaryKey()
	{
		return primaryKey;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public int getDataType()
	{
		return dataType;
	}

	public void setDataType(int dataType)
	{
		this.dataType = dataType;
	}

	public String getDateTypeName() {
		return dateTypeName;
	}

	public void setDateTypeName(String dateTypeName) {
		this.dateTypeName = dateTypeName;
	}

	public Object getJavaDataType() {
		return javaDataType;
	}

	public void setJavaDataType(Object javaDataType) {
		this.javaDataType = javaDataType;
	}

	public int getNullAble() {
		return nullAble;
	}

	public void setNullAble(int nullAble) {
		this.nullAble = nullAble;
	}

	public String getFieldMemo() {
		return fieldMemo;
	}

	public void setFieldMemo(String fieldMemo) {
		this.fieldMemo = fieldMemo;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	
	
}
