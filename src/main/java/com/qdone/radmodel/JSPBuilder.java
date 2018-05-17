// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JSPBuilder.java

package com.qdone.radmodel;

import java.io.*;
import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity

public class JSPBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public JSPBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	public void saveToFile()
	{
		DataInputStream dis = new DataInputStream(getClass().getResourceAsStream("JSPTemplate.txt"));
		try
		{
			byte buffer[] = new byte[dis.available()];
			dis.readFully(buffer);
			String codeBuffer = new String(buffer, "utf-8");
			codeBuffer = codeBuffer.replaceAll("_entity_", entity.getEntityName());
			codeBuffer = codeBuffer.replaceAll("_entityBean_", entity.getEntityBeanName());
			saveToFile(codeBuffer);
			dis.close();
			buffer = (byte[])null;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void saveToFile(String codeBuffer)
	{
		String dir = (new StringBuilder(String.valueOf(project.getJspRootPath()))).append(entity.getEntityBeanName()).append(File.separator).toString();
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
		String fileName = (new StringBuilder(String.valueOf(dir))).append(entity.getEntityBeanName()).append(".jsp").toString();
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
