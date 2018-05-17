package test;

import java.util.ArrayList;
import java.util.List;


public class TestUperNamae {
	public static void main(String[] args) {
//		System.err.println("数据库表字段名转换成bean:"+ ColumnConvert.getJavaBeanPropsNameBy("login_date"));
//		System.err.println("bean的字段名转换成数据库原生字段名:"+ColumnConvert.getJavaBeanNameBy("loginDate"));
		System.out.println(convertBeanToCloumn("loginDatE"));
	}
	/**
	 * 将数据库的表字段loginDate转换成原生的login_date
	 * @param entityName
	 * @return
	 */
	public static String convertBeanToCloumn(String entityName){
		StringBuffer buffer = new StringBuffer(1024);
	    List<Object> arr=new ArrayList<Object>();
	    //先将结果暂时存入list中
		for(int i=0;i<entityName.length();i++){
				if(i>0&&i<entityName.length()-1){
					if(Character.isUpperCase(entityName.charAt(i))){
						arr.add("_");
						arr.add(entityName.charAt(i));
					}else{
						arr.add(entityName.charAt(i));
					}
				}else{
					arr.add(entityName.charAt(i));
				}
		}
		//将list转换成
		for (int i = 0; i < arr.size(); i++) {
			buffer.append(arr.get(i));
		}
		return buffer.toString().toLowerCase();
	}
}
