// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TestRadModel.java

package com.qdone.radmodel;

import java.io.*;
import java.sql.*;
import java.util.Properties;

// Referenced classes of package com.qdone.radmodel:
//			Project, Entity, EntityBuilder, IBatisBuilder, 
//			SpringBuilder, ActionBuilder, StrutsBuilder, ServiceBuilder, 
//			InsertJspBuilder, SelectJspBuilder, ViewJspBuilder, UpdateJspBuilder

public class TestRadModel
{

	private Connection conn;
	private Properties props;
	private String jdbcDriver;
	private String jdbcUrl;
	private String userName;
	private String password;
	private String rootPackage;
	private Project project;
	private String tablesName;

	public TestRadModel()
	{
		try
		{
			String rootdir = System.getProperty("user.dir");
			props = new Properties();
			props.load(new FileInputStream((new StringBuilder(String.valueOf(rootdir))).append(File.separator).append("src//main//resources").append(File.separator).append("config.properties").toString()));
			jdbcDriver = props.getProperty("c3p0.driverClass");
			jdbcUrl = props.getProperty("c3p0.jdbcUrl");
			userName = props.getProperty("c3p0.user");
			password = props.getProperty("c3p0.password");
			rootPackage = props.getProperty("autocoding.package");
			tablesName = props.getProperty("autocoding.table.names");
			tablesName=tablesName.toUpperCase();//表名不区分大小写
			project = new Project(rootdir, rootPackage);
			String prefix = props.getProperty("autocoding.prefix");
			project.setPrefix(prefix);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean connect()
	{
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(jdbcUrl, userName, password);
			System.out.println("---------数据库连接成功--------------");
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("---------数据库连接失败--------------");
		return false;
	}

	public boolean disconnect()
	{
		try
		{
			conn.close();
			System.out.println("---------数据库断开成功--------------");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			System.out.println("---------数据库断开失败--------------");
			return false;
		}
		return true;
	}

	public void run()
	{
		String names[] = tablesName.split(",");
		String as[];
		int j = (as = names).length;
		for (int i = 0; i < j; i++)
		{
			String name = as[i];
			System.out.println((new StringBuilder("---------开始生成表[")).append(name).append("]的代码--------------").toString());
			run(name);
			System.out.println((new StringBuilder("---------完成生成表[")).append(name).append("]的代码--------------").toString());
		}

	}

	public void run(String tableName)
	{
		try
		{
			Entity entity = new Entity(conn, tableName);
			entity.parseEntityInfo();
			project.setEntity(entity);
			//springMVC框架的   代码生成
			System.out.println((new StringBuilder("生成【实体类】")).append(entity.getEntityName()).append(".java -> 开始").toString());
			EntityBuilder entityBuilder = new EntityBuilder(project);
			entityBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【实体类】")).append(entity.getEntityName()).append(".java -> 结束").toString());
			System.out.println((new StringBuilder("生成【mybatis配置文件】")).append(entity.getEntityName()).append(".xml -> 开始").toString());
			IBatisBuilder iBatisBuilder = new IBatisBuilder(project);
			iBatisBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【mybatis配置文件】")).append(entity.getEntityName()).append(".xml -> 结束").toString());
			/*System.out.println((new StringBuilder("生成【dao文件】")).append(entity.getEntityName()).append("Dao.java和").append(entity.getEntityName()).append("DaoImpl.java -> 开始").toString());
			DaoBuilder daoBuilder = new DaoBuilder(project);
			daoBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【dao文件】")).append(entity.getEntityName()).append("Dao.java和").append(entity.getEntityName()).append("DaoImpl.java -> 结束").toString());
			*/
			System.out.println((new StringBuilder("生成【Service文件】")).append(entity.getEntityName()).append("Service.java和").append(entity.getEntityName()).append("ServiceImpl.java -> 开始").toString());
			ServiceBuilder serviceBuilder = new ServiceBuilder(project);
			serviceBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【Service文件】")).append(entity.getEntityName()).append("Service.java和").append(entity.getEntityName()).append("ServiceImpl.java -> 结束").toString());
			System.out.println((new StringBuilder("生成【controller类】")).append(entity.getEntityName()).append("Controller.java -> 开始").toString());
			ControllerBuilder controllerBuilder = new ControllerBuilder(project);
			controllerBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【controller类】")).append(entity.getEntityName()).append("Controller.java -> 结束").toString());
			
			System.out.println("生成【select.jsp文件】select.html -> 开始");
			SelectJspBuilder selectJspBuilder = new SelectJspBuilder(project);
			selectJspBuilder.saveToFile();
			System.out.println("生成【select.jsp文件】select.html -> 结束");
			
			System.out.println("生成【insert.jsp文件】insert.html -> 开始");
			InsertJspBuilder insertJspBuilder = new InsertJspBuilder(project);
			insertJspBuilder.saveToFile();
			System.out.println("生成【insert.jsp文件】insert.html -> 结束");
			
			System.out.println("生成【update.jsp文件】update.html -> 开始");
			UpdateJspBuilder updateJspBuilder = new UpdateJspBuilder(project);
			updateJspBuilder.saveToFile();
			System.out.println("生成【update.jsp文件】update.html -> 结束");
			
			/*System.out.println("生成【view.jsp文件】view.jsp -> 开始");
			ViewJspBuilder viewJspBuilder = new ViewJspBuilder(project);
			viewJspBuilder.saveToFile();
			System.out.println("生成【view.jsp文件】view.jsp -> 结束");*/
			

			/*System.out.println((new StringBuilder("生成【Spring配置文件】sprint_")).append(entity.getEntityName()).append(".xml -> 开始").toString());
			SpringBuilder springBuilder = new SpringBuilder(project);
			springBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【Spring配置文件】sprint_")).append(entity.getEntityName()).append(".xml -> 结束").toString());
			System.out.println((new StringBuilder("生成【action类】")).append(entity.getEntityName()).append("Action.java -> 开始").toString());
			ActionBuilder actionBuilder = new ActionBuilder(project);
			actionBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【action类】")).append(entity.getEntityName()).append("Action.java -> 结束").toString());
			System.out.println((new StringBuilder("生成【Struts配置文件】struts_")).append(entity.getEntityName()).append(".xml -> 开始").toString());
			StrutsBuilder strutsBuilder = new StrutsBuilder(project);
			strutsBuilder.saveToFile();
			System.out.println((new StringBuilder("生成【Struts配置文件】struts_")).append(entity.getEntityName()).append(".xml -> 结束").toString());*/
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		TestRadModel testRadModel = new TestRadModel();
		testRadModel.connect();
		testRadModel.run();
		testRadModel.disconnect();
	}
}
