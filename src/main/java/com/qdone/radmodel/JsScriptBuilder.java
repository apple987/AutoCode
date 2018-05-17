// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   JsScriptBuilder.java

package com.qdone.radmodel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

// Referenced classes of package com.qdone.radmodel:
//			CodeBuilder, Project, Entity, Field

public class JsScriptBuilder
	implements CodeBuilder
{

	private Project project;
	private Entity entity;

	public JsScriptBuilder(Project project)
	{
		this.project = project;
		entity = this.project.getEntity();
	}

	private void saveToFile(StringBuffer codeBuffer)
	{
		String dir = project.getJsRootPath();
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
		String fileName = (new StringBuilder(String.valueOf(project.getJsRootPath()))).append(entity.getEntityBeanName()).append(File.separator).append(entity.getEntityBeanName()).append(".js").toString();
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
		String varSpaceName = entity.getEntityBeanName();
		String newAction = (new StringBuilder(String.valueOf(varSpaceName))).append(".add").append(entity.getEntityName()).append("Action").toString();
		String modifyAction = (new StringBuilder(String.valueOf(varSpaceName))).append(".modify").append(entity.getEntityName()).append("Action").toString();
		String deleteAction = (new StringBuilder(String.valueOf(varSpaceName))).append(".delete").append(entity.getEntityName()).append("Action").toString();
		String priFieldName = entity.getPrimaryKeyField().getPropertyName();
		codeBuffer.append((new StringBuilder("var ")).append(varSpaceName).append(" = {};// 命名空间\r\n\r\n").toString());
		codeBuffer.append("Ext.onReady(function(){\r\n");
		codeBuffer.append("    Ext.QuickTips.init();\r\n");
		codeBuffer.append("    Ext.form.Field.prototype.msgTarget = 'side';\r\n\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".").append(varSpaceName).append("DataStore = new Ext.data.Store({\r\n").toString());
		codeBuffer.append((new StringBuilder("        url : load")).append(entity.getEntityName()).append("Url,// 加载所有的数据\r\n").toString());
		codeBuffer.append("        autoLoad : true,// 不需要调用就加载\r\n");
		codeBuffer.append("        reader : new Ext.data.JsonReader(\r\n");
		codeBuffer.append("        {\r\n");
		codeBuffer.append("            totalProperty : 'results',// 总共多少条记录\r\n");
		codeBuffer.append("            root : 'rows',// JSON 格式的数据源\r\n");
		codeBuffer.append((new StringBuilder("            id : '")).append(entity.getPrimaryKeyField().getPropertyName()).append("'\r\n").toString());
		codeBuffer.append("        },\r\n");
		codeBuffer.append("        // 对root 里面的数据怎么解析\r\n");
		codeBuffer.append("        [\r\n");
		String jsonKeyValue = (new StringBuilder("    {name:'")).append(priFieldName).append("'},\r\n").toString();
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			Field field = (Field)entity.getFields().get(i);
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("            {name:'").append(field.getPropertyName()).append("'},\r\n").toString();
		}

		jsonKeyValue = jsonKeyValue.substring(0, jsonKeyValue.length() - 3);
		codeBuffer.append((new StringBuilder("        ")).append(jsonKeyValue).append("\r\n").toString());
		codeBuffer.append("        ])\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".gridSM = new Ext.grid.CheckboxSelectionModel({\r\n").toString());
		codeBuffer.append("        singleSelect : true,// 定义选择模型是单选框\r\n");
		codeBuffer.append("        listeners : {\r\n");
		codeBuffer.append("            'rowselect' : function(selectionModel, rowIndex, record) {\r\n");
		codeBuffer.append("                // 有选中的话就让修改，删除成为高亮\r\n");
		codeBuffer.append((new StringBuilder("                ")).append(varSpaceName).append(".selectedRecord = record;\r\n\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(modifyAction).append(".enable();\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(deleteAction).append(".enable();\r\n").toString());
		codeBuffer.append("            }\r\n");
		codeBuffer.append("        }\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    // 定义表单的列模型\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".gridColumModel = new Ext.grid.ColumnModel({\r\n").toString());
		codeBuffer.append((new StringBuilder("        columns : [")).append(varSpaceName).append(".gridSM, \r\n").toString());
		codeBuffer.append("            {\r\n");
		codeBuffer.append((new StringBuilder("                header:'")).append(priFieldName).append("',\r\n").toString());
		codeBuffer.append("                width:100,\r\n");
		codeBuffer.append((new StringBuilder("                dataIndex : '")).append(priFieldName).append("'\r\n").toString());
		codeBuffer.append("            },\r\n");
		jsonKeyValue = "";
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			Field field = (Field)entity.getFields().get(i);
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("            {\r\n").toString();
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("                header:'").append(field.getPropertyName()).append("',\r\n").toString();
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("                width:100,\r\n").toString();
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("                dataIndex : '").append(field.getPropertyName()).append("'\r\n").toString();
			jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue))).append("            },\r\n").toString();
		}

		jsonKeyValue = (new StringBuilder(String.valueOf(jsonKeyValue.substring(0, jsonKeyValue.length() - 3)))).append("\r\n").toString();
		codeBuffer.append(jsonKeyValue);
		codeBuffer.append("        ]\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //定义 New Action\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(newAction).append(" = new Ext.Action({\r\n").toString());
		codeBuffer.append((new StringBuilder("        text : '添加")).append(entity.getEntityName()).append("',// 设置名称\r\n").toString());
		codeBuffer.append((new StringBuilder("        iconCls : 'add")).append(entity.getEntityName()).append("',// CSS 样式\r\n").toString());
		codeBuffer.append("        handler : function() {\r\n");
		codeBuffer.append((new StringBuilder("            var el = ")).append(varSpaceName).append(".dataForm.getForm().getEl();\r\n").toString());
		codeBuffer.append("            if (el){\r\n");
		codeBuffer.append("                el.dom.reset();\r\n");
		codeBuffer.append("            }\r\n\r\n");
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataForm.getForm().reset();// 清空表单里面的元素的值\r\n").toString());
		codeBuffer.append((new StringBuilder("            var ")).append(priFieldName).append(" = Ext.getCmp('").append(priFieldName).append("');\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(priFieldName).append(".setRawValue('');\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(priFieldName).append(".setReadOnly(false);\r\n\r\n").toString());
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			Field field = (Field)entity.getFields().get(i);
			codeBuffer.append((new StringBuilder("            var ")).append(field.getPropertyName()).append(" = Ext.getCmp('").append(field.getPropertyName()).append("');\r\n").toString());
			codeBuffer.append((new StringBuilder("            ")).append(field.getPropertyName()).append(".setRawValue('');\r\n\r\n").toString());
		}

		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.iconCls = 'add").append(entity.getEntityName()).append("';// 设置窗口的样式\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.setTitle('添加").append(entity.getEntityName()).append("');// 设置窗口的名称\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.show();// 显示窗口\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.center();// 让窗口所属的页面居中显示\r\n").toString());
		codeBuffer.append("        }\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //定义Modify Action\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(modifyAction).append(" = new Ext.Action({\r\n").toString());
		codeBuffer.append((new StringBuilder("        text : '修改")).append(entity.getEntityName()).append("',// 设置名称\r\n").toString());
		codeBuffer.append((new StringBuilder("        iconCls : 'modify")).append(entity.getEntityName()).append("',// CSS 样式\r\n").toString());
		codeBuffer.append("        disabled : true,\r\n");
		codeBuffer.append("        handler : function() {\r\n");
		codeBuffer.append((new StringBuilder("            var el = ")).append(varSpaceName).append(".dataForm.getForm().getEl();\r\n").toString());
		codeBuffer.append("            if (el){\r\n");
		codeBuffer.append("                el.dom.reset();\r\n");
		codeBuffer.append("            }\r\n\r\n");
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataForm.getForm().reset();// 清空表单里面的元素的值\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.iconCls = 'modify").append(entity.getEntityName()).append("';// 设置窗口的样式\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.setTitle('修改").append(entity.getEntityName()).append("');// 设置窗口的名称\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataForm.getForm().loadRecord(").append(varSpaceName).append(".selectedRecord);\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.show();// 显示窗口\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(varSpaceName).append(".dataWin.center();// 让窗口所属的页面居中显示\r\n").toString());
		codeBuffer.append("        }\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //定义Delete Action\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(deleteAction).append(" = new Ext.Action({\r\n").toString());
		codeBuffer.append((new StringBuilder("        text : '删除")).append(entity.getEntityName()).append("',// 设置名称\r\n").toString());
		codeBuffer.append((new StringBuilder("        iconCls : 'delete")).append(entity.getEntityName()).append("',// CSS 样式\r\n").toString());
		codeBuffer.append("        disabled : true,\r\n");
		codeBuffer.append("        handler : function() {\r\n");
		codeBuffer.append((new StringBuilder("            Ext.Msg.confirm('删除")).append(entity.getEntityName()).append("', '确认要删除").append(entity.getEntityName()).append("吗？', function(btn, text) {\r\n").toString());
		codeBuffer.append("                if (btn == 'yes') {\r\n");
		codeBuffer.append("                    Ext.Ajax.request({\r\n");
		codeBuffer.append((new StringBuilder("                        url : delete")).append(entity.getEntityName()).append("Url,\r\n").toString());
		codeBuffer.append("                        params : {\r\n");
		codeBuffer.append((new StringBuilder("                            '")).append(entity.getEntityBeanName()).append(".").append(priFieldName).append("' : ").append(varSpaceName).append(".selectedRecord.data.").append(priFieldName).append("\r\n").toString());
		codeBuffer.append("                        },\r\n");
		codeBuffer.append("                        success : function(resp, opts) {\r\n");
		codeBuffer.append("                            var respObj = Ext.util.JSON.decode(resp.responseText);\r\n");
		codeBuffer.append("                            if (respObj.success) {\r\n");
		codeBuffer.append("                                // 重新加载角色表格\r\n");
		codeBuffer.append((new StringBuilder("                                ")).append(varSpaceName).append(".").append(varSpaceName).append("DataStore.reload();\r\n").toString());
		codeBuffer.append((new StringBuilder("                                ")).append(modifyAction).append(".disable();\r\n").toString());
		codeBuffer.append((new StringBuilder("                                ")).append(deleteAction).append(".disable();\r\n").toString());
		codeBuffer.append("                            } else {\r\n");
		codeBuffer.append((new StringBuilder("                                Ext.Msg.alert('删除")).append(entity.getEntityBeanName()).append("', respObj.msg);\r\n").toString());
		codeBuffer.append("                            }\r\n");
		codeBuffer.append("                        },\r\n");
		codeBuffer.append("                        failure : dealErrReps\r\n");
		codeBuffer.append("                    })\r\n");
		codeBuffer.append("                }\r\n");
		codeBuffer.append("            })\r\n");
		codeBuffer.append("        }\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //定义form\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".dataForm = new Ext.form.FormPanel({\r\n").toString());
		codeBuffer.append("        labelAlign : 'right',\r\n");
		codeBuffer.append("        labelWidth : 90,\r\n");
		codeBuffer.append("        frame : true,\r\n");
		codeBuffer.append("        region : 'center',\r\n");
		codeBuffer.append("        buttonAlign : 'left',\r\n");
		codeBuffer.append("        defaultType : 'textfield',\r\n");
		codeBuffer.append("        items : [\r\n");
		codeBuffer.append("            {\r\n");
		codeBuffer.append("                xtype : 'hidden',\r\n");
		codeBuffer.append((new StringBuilder("                name : '")).append(varSpaceName).append(".").append(priFieldName).append("',\r\n").toString());
		codeBuffer.append((new StringBuilder("                id : '")).append(priFieldName).append("'\r\n").toString());
		codeBuffer.append("            },\r\n");
		for (int i = 0; i < entity.getFields().size(); i++)
		{
			Field field = (Field)entity.getFields().get(i);
			codeBuffer.append("            {\r\n");
			codeBuffer.append((new StringBuilder("                fieldLabel : '")).append(field.getFieldName()).append("',\r\n").toString());
			codeBuffer.append((new StringBuilder("                name : '")).append(varSpaceName).append(".").append(field.getPropertyName()).append("',\r\n").toString());
			codeBuffer.append((new StringBuilder("                id : '")).append(field.getPropertyName()).append("',\r\n").toString());
			codeBuffer.append("                maxLength : 50,\r\n");
			codeBuffer.append((new StringBuilder("                maxLengthText : '")).append(field.getFieldName()).append("最大长度50',\r\n").toString());
			codeBuffer.append("                allowBlank : false,\r\n");
			codeBuffer.append((new StringBuilder("                blankText : '")).append(field.getFieldName()).append("不能为空',\r\n").toString());
			codeBuffer.append("                width : 250\r\n");
			codeBuffer.append("            }");
			if (i != entity.getFields().size() - 1)
				codeBuffer.append(",\r\n");
			else
				codeBuffer.append("\r\n");
		}

		codeBuffer.append("        ]\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //windown\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".dataWin = new Ext.Window({\r\n").toString());
		codeBuffer.append((new StringBuilder("        title : '")).append(entity.getEntityName()).append("',\r\n").toString());
		codeBuffer.append("        width : 400,\r\n");
		codeBuffer.append("        layout : 'border',\r\n");
		codeBuffer.append((new StringBuilder("        height : ")).append(Integer.toString((entity.getFields().size() + 1) * 30)).append(",\r\n").toString());
		codeBuffer.append("        closable : true,\r\n");
		codeBuffer.append("        closeAction : 'hide',\r\n");
		codeBuffer.append("        collapsed : true,\r\n");
		codeBuffer.append("        modal : true,\r\n");
		codeBuffer.append((new StringBuilder("        items : [")).append(varSpaceName).append(".dataForm],\r\n").toString());
		codeBuffer.append("        buttons : [{\r\n");
		codeBuffer.append("            text : '保存',\r\n");
		codeBuffer.append("            handler : function() {\r\n");
		codeBuffer.append((new StringBuilder("                if (")).append(varSpaceName).append(".dataForm.getForm().isValid())\r\n").toString());
		codeBuffer.append("                {\r\n");
		codeBuffer.append("                    submit();\r\n");
		codeBuffer.append("                }\r\n");
		codeBuffer.append("            }\r\n");
		codeBuffer.append("        },\r\n");
		codeBuffer.append("        {\r\n");
		codeBuffer.append("            text:'取消',\r\n");
		codeBuffer.append("            handler:function()\r\n");
		codeBuffer.append("            {\r\n");
		codeBuffer.append((new StringBuilder("                ")).append(varSpaceName).append(".dataForm.getForm().reset();\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(varSpaceName).append(".dataWin.hide();\r\n").toString());
		codeBuffer.append("            }\r\n");
		codeBuffer.append("        }]\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("    //定义form submit\r\n");
		codeBuffer.append("    function submit()\r\n");
		codeBuffer.append("    {\r\n");
		codeBuffer.append((new StringBuilder("        ")).append(varSpaceName).append(".dataForm.getForm().submit(\r\n").toString());
		codeBuffer.append("        {\r\n");
		codeBuffer.append("            clientValidation: false,\r\n");
		codeBuffer.append((new StringBuilder("            url:save")).append(entity.getEntityName()).append("Url,\r\n").toString());
		codeBuffer.append("            success: function(form, action) {\r\n");
		codeBuffer.append("                // 重新加载角色表格\r\n");
		codeBuffer.append((new StringBuilder("                ")).append(varSpaceName).append(".").append(varSpaceName).append("DataStore.reload();\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(modifyAction).append(".disable();\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(deleteAction).append(".disable();\r\n").toString());
		codeBuffer.append((new StringBuilder("                ")).append(varSpaceName).append(".dataWin.hide();\r\n").toString());
		codeBuffer.append("            },\r\n");
		codeBuffer.append("            failure: function(form, action){\r\n");
		codeBuffer.append("                switch (action.failureType) {\r\n");
		codeBuffer.append("                    case Ext.form.Action.CLIENT_INVALID:\r\n");
		codeBuffer.append("                        Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');\r\n");
		codeBuffer.append("                        break;\r\n");
		codeBuffer.append("                    case Ext.form.Action.CONNECT_FAILURE:\r\n");
		codeBuffer.append("                        Ext.Msg.alert('Failure', 'Ajax communication failed');\r\n");
		codeBuffer.append("                        break;\r\n");
		codeBuffer.append("                    case Ext.form.Action.SERVER_INVALID:\r\n");
		codeBuffer.append((new StringBuilder("                        Ext.Msg.alert('保存")).append(entity.getEntityName()).append("失败', action.result.msg);\r\n").toString());
		codeBuffer.append("                        break;\r\n");
		codeBuffer.append("                }\r\n");
		codeBuffer.append("            }\r\n");
		codeBuffer.append("        })\r\n");
		codeBuffer.append("    }\r\n\r\n");
		codeBuffer.append("    //定义数据表格\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".").append(varSpaceName).append("Grid = new Ext.grid.GridPanel({\r\n").toString());
		codeBuffer.append("        region : 'center',\r\n");
		codeBuffer.append("        margins : '0 2 0 0',\r\n");
		codeBuffer.append("        buttonAlign : 'left',\r\n");
		codeBuffer.append("        tbar : [\r\n");
		codeBuffer.append((new StringBuilder("            ")).append(newAction).append(",\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(modifyAction).append(",\r\n").toString());
		codeBuffer.append((new StringBuilder("            ")).append(deleteAction).append("\r\n").toString());
		codeBuffer.append("        ],\r\n");
		codeBuffer.append((new StringBuilder("        store : ")).append(varSpaceName).append(".").append(varSpaceName).append("DataStore,\r\n").toString());
		codeBuffer.append((new StringBuilder("        sm : ")).append(varSpaceName).append(".gridSM,\r\n").toString());
		codeBuffer.append((new StringBuilder("        cm : ")).append(varSpaceName).append(".gridColumModel,\r\n").toString());
		codeBuffer.append("        listeners : {\r\n");
		codeBuffer.append("            'cellclick' : function(obj, rowIndex, columnIndex, e) {\r\n");
		codeBuffer.append((new StringBuilder("                if (")).append(varSpaceName).append(".gridSM.getCount() == 0) {\r\n").toString());
		codeBuffer.append((new StringBuilder("                    ")).append(modifyAction).append(".disable();\r\n").toString());
		codeBuffer.append((new StringBuilder("                    ")).append(deleteAction).append(".disable();\r\n").toString());
		codeBuffer.append("                }\r\n");
		codeBuffer.append("            }\r\n");
		codeBuffer.append("        }\r\n");
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append((new StringBuilder("    ")).append(varSpaceName).append(".dataView = new Ext.Viewport({\r\n").toString());
		codeBuffer.append("        layout : 'border',\r\n");
		codeBuffer.append((new StringBuilder("        items : [")).append(varSpaceName).append(".").append(varSpaceName).append("Grid]\r\n").toString());
		codeBuffer.append("    });\r\n\r\n");
		codeBuffer.append("});\r\n");
		saveToFile(codeBuffer);
	}
}
