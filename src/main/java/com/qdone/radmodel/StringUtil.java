// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   StringUtil.java

package com.qdone.radmodel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil
{

	public StringUtil()
	{
	}

	public static String getStringHexLenWithinByte(String strValue)
	{
		int len = strValue.length() / 2;
		String result = Integer.toHexString(len);
		if (result.length() == 1)
			result = (new StringBuilder("0")).append(result).toString();
		return result;
	}

	public static String trimStringBlank(String strValue)
	{
		String result = strValue.trim();
		String space = " ";
		String tabCh = Character.toString('\t');
		result = result.replaceAll(space, "");
		result = result.replaceAll(tabCh, "");
		return result;
	}

	public static int getHexDataLen(String data)
	{
		String strTemp = data.replaceAll(" ", "");
		return strTemp.length();
	}

	public static String formatHexDataWithSpace(String data)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < data.length(); i++)
		{
			if (i > 0 && i % 2 == 0)
				buffer.append(" ");
			buffer.append(data.charAt(i));
		}

		return buffer.toString();
	}

	public static String asciiToHex(String asciiString)
	{
		StringBuffer hexString = new StringBuffer();
		byte buff[] = asciiString.getBytes();
		for (int i = 0; i < buff.length; i++)
		{
			int iValue = buff[i];
			if (iValue < 0)
				iValue += 256;
			String hex = Integer.toString(iValue, 16);
			if (hex.length() == 1)
				hexString.append((new StringBuilder("0")).append(hex).toString());
			else
				hexString.append(hex);
		}

		return hexString.toString().toUpperCase();
	}

	public static String byteToHex(byte buffer[], int offset, int len)
	{
		StringBuffer hexString = new StringBuffer();
		for (int i = offset; i < offset + len; i++)
		{
			int iValue = buffer[i];
			if (iValue < 0)
				iValue += 256;
			String hex = Integer.toString(iValue, 16);
			if (hex.length() == 1)
				hexString.append((new StringBuilder("0")).append(hex).toString());
			else
				hexString.append(hex);
		}

		return hexString.toString().toUpperCase();
	}

	public static String byteToHex(byte buffer[])
	{
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < buffer.length; i++)
		{
			int iValue = buffer[i];
			if (iValue < 0)
				iValue += 256;
			String hex = Integer.toString(iValue, 16);
			if (hex.length() == 1)
				hexString.append((new StringBuilder("0")).append(hex).toString());
			else
				hexString.append(hex);
		}

		return hexString.toString().toUpperCase();
	}

	public static String asciiToHex(byte hexBuffer[], int iOffset, int iLen)
	{
		StringBuffer hexString = new StringBuffer();
		for (int i = iOffset; i < iOffset + iLen; i++)
		{
			int byteValue = hexBuffer[i];
			if (byteValue < 0)
				byteValue += 256;
			String hex = Integer.toString(byteValue, 16);
			if (hex.length() == 1)
				hexString.append((new StringBuilder("0")).append(hex).toString());
			else
				hexString.append(hex);
		}

		return hexString.toString().toUpperCase();
	}

	public static String intToHex(int value)
	{
		String hex = Integer.toString(value, 16);
		if (hex.length() % 2 != 0)
			hex = (new StringBuilder("0")).append(hex).toString();
		return hex.toUpperCase();
	}

	public static String hexToAscii(String hex)
	{
		byte buffer[] = new byte[hex.length() / 2];
		for (int i = 0; i < buffer.length; i++)
		{
			String strByte = hex.substring(i * 2, i * 2 + 2);
			buffer[i] = (byte)Integer.parseInt(strByte, 16);
		}

		return new String(buffer);
	}

	public static byte[] hexToBytes(String hex)
	{
		byte buffer[] = new byte[hex.length() / 2];
		for (int i = 0; i < buffer.length; i++)
		{
			String strByte = hex.substring(i * 2, i * 2 + 2);
			buffer[i] = (byte)Integer.parseInt(strByte, 16);
		}

		return buffer;
	}

	public static boolean hasChineseChar(String strValue)
	{
		boolean bResult = false;
		byte temp[] = strValue.getBytes();
		for (int i = 0; i < strValue.length(); i++)
		{
			if (temp[i] >= 0)
				continue;
			bResult = true;
			break;
		}

		return bResult;
	}

	public static boolean isNull(String value)
	{
		boolean bIsNull = false;
		if (value == null)
		{
			bIsNull = true;
			return bIsNull;
		}
		if (value.trim().length() == 0)
			bIsNull = true;
		return bIsNull;
	}

	public static String formatWithSpace(String value)
	{
		String formatedValue = value;
		formatedValue = trimStringBlank(formatedValue);
		if (formatedValue.length() % 2 != 0)
			formatedValue = (new StringBuilder(String.valueOf(formatedValue))).append("0").toString();
		String retValue = "";
		for (int i = 0; i < formatedValue.length(); i += 2)
			retValue = (new StringBuilder(String.valueOf(retValue))).append(formatedValue.substring(i, i + 2)).append(" ").toString();

		return retValue;
	}

	public static String formatDateToString(Date date)
	{
		if (date == null)
		{
			return "";
		} else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			return sdf.format(date);
		}
	}

	public static String formatDateToEngString(Date date)
	{
		if (date == null)
		{
			return "";
		} else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		}
	}

	public static String splitAndFilterString(String input, int length)
	{
		if (input == null || input.trim().equals(""))
			return "";
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
		str = str.replaceAll("[(/>)<]", "");
		int len = str.length();
		if (len <= length)
		{
			return str;
		} else
		{
			str = str.substring(0, length);
			str = (new StringBuilder(String.valueOf(str))).append("......").toString();
			return str;
		}
	}

	public static String formatStringByLen(String data, int len, String fill)
	{
		String strData = trimStringBlank(data);
		int iFilledLen = strData.length() % len;
		if (iFilledLen > 0)
		{
			for (int i = 0; i < 8 - iFilledLen; i++)
				strData = (new StringBuilder(String.valueOf(strData))).append(fill).toString();

		}
		return strData;
	}

	public static byte[] bcdToBytes(String data)
	{
		String strData = trimStringBlank(data).toUpperCase();
		int j = 0;
		byte bData[] = new byte[strData.length() / 2];
		for (int i = 0; i < strData.length(); i++)
		{
			int value = Character.digit(strData.charAt(i), 16);
			bData[j] = (byte)(value << 4 & 0xf0);
			value = Character.digit(strData.charAt(i + 1), 16);
			bData[j] |= (byte)(value & 0xf);
			i++;
			j++;
		}

		return bData;
	}

	public static String getSeperatedPathFromPackage(String rootPackage)
	{
		String path = rootPackage.replaceAll("\\.", (new StringBuilder("\\")).append(File.separator).toString());
		if (!path.endsWith(File.separator))
			path = (new StringBuilder(String.valueOf(path))).append(File.separator).toString();
		return path;
	}
}
