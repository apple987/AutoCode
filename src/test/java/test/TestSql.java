package test;

import java.io.Serializable;

public class TestSql implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		String tableName="student";
		String tableSchema="wepull";
		String columnName="sname";
		//获取mysql数据库的字段注释信息
				String mysqlCommont="SELECT COLUMN_NAME , DATA_TYPE , COLUMN_COMMENT  from FROM INFORMATION_SCHEMA.COLUMNS ";
				mysqlCommont+=" WHERE table_name = '"+tableName+"' ";
				mysqlCommont+=" AND table_schema = '"+tableSchema+"' ";
				mysqlCommont+=" AND column_name  = '"+columnName+"' ";
				System.err.println(mysqlCommont);
				
		String s="";
		String id="id";
		s="studentService.view(Integer.parseInt(request.getParameter(\""+id+"\")))";
		System.err.println(s);
		
		String str="asdad<td><input id='sex' name='sex' value='${student.sex}' /></td>dsadsa";
		String a="<td><input id='sex' name='sex' value='${student.sex}' /></td>";
		str=str.replaceAll(a, "");
		System.out.println(str);		
	}
}
