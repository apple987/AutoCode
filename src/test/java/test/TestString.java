package test;

public class TestString {
	public static void main(String[] args) {
		StringBuffer searchAreaParam=new StringBuffer(1024);
		String name="A";
		String sex="B";
		searchAreaParam.append("                            ").append((new StringBuilder("<td><input id='")).append(name).append("'  name='"+sex+"' class=\"required date\" readonly='true' onclick=\"laydate({istime: true, format: 'YYYY-MM-DD hh:mm:ss'})\"    value=\"<#if "+name+"."+sex +"??>${"+name+"."+sex+"?string('yyyy-MM-dd HH:mm:ss')}</#if>\" /></td>\r\n").toString());
	  System.err.println(searchAreaParam);
	}

}
