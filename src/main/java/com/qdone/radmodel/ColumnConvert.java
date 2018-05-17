// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ColumnConvert.java

package com.qdone.radmodel;


public class ColumnConvert
{

	public ColumnConvert()
	{
	}

	public static String getJavaBeanNameBy(String entityName)
	{
		String javaBeanName = "";
		boolean bFlag = false;
		entityName = entityName.toUpperCase();
		for (int i = 0; i < entityName.length(); i++)
		{
			char ch = entityName.charAt(i);
			if (!bFlag)
			{
				javaBeanName = (new StringBuilder(String.valueOf(javaBeanName))).append(ch).toString();
				bFlag = true;
			} else
			if (ch != '_')
				javaBeanName = (new StringBuilder(String.valueOf(javaBeanName))).append(Character.toString(entityName.charAt(i)).toLowerCase()).toString();
			else
				bFlag = false;
		}

		return javaBeanName;
	}

	public static String getGetSetterNameByProp(String propName)
	{
		String getSetterName = propName.substring(0, 1).toUpperCase();
		getSetterName = (new StringBuilder(String.valueOf(getSetterName))).append(propName.substring(1)).toString();
		return getSetterName;
	}

	public static String getJavaBeanPropsNameBy(String columnName)
	{
		String javaBeanName = "";
		boolean bFlag = false;
		boolean bFirstFlag = false;
		columnName = columnName.toUpperCase();
		for (int i = 0; i < columnName.length(); i++)
		{
			char ch = columnName.charAt(i);
			if (!bFirstFlag)
			{
				javaBeanName = (new StringBuilder(String.valueOf(javaBeanName))).append(Character.toString(columnName.charAt(i)).toLowerCase()).toString();
				bFirstFlag = true;
				bFlag = true;
			} else
			if (!bFlag)
			{
				javaBeanName = (new StringBuilder(String.valueOf(javaBeanName))).append(ch).toString();
				bFlag = true;
			} else
			if (ch != '_')
				javaBeanName = (new StringBuilder(String.valueOf(javaBeanName))).append(Character.toString(columnName.charAt(i)).toLowerCase()).toString();
			else
				bFlag = false;
		}

		return javaBeanName;
	}
}
