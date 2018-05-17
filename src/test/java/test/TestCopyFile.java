package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestCopyFile {
    public static void main(String[] args){
        try {
        	//获得源文件
        	String inName="D:\\data.txt";
        	File inFile=new File(inName);
        	if(!inFile.exists()){
        		inFile.mkdirs();
        	}
			FileInputStream fis = new FileInputStream(inFile.getAbsolutePath());
			System.out.println("源文件:"+inFile.getAbsolutePath());
			//获得目标文件
			String outName="D:\\json.txt";
			File outFile=new File(outName);
			if(!outFile.exists()){
				outFile.mkdirs();
			}else{
				outFile.delete();
				outFile.mkdirs();
			}
			System.out.println("目标文件");
			FileOutputStream fos = new FileOutputStream(outFile.getAbsolutePath());
			byte[] b = new byte[1024000];
			int n;
			while ((n = fis.read(b)) != -1) {
			    fos.write(b, 0, n);
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println("文件没有找到:"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO流操作异常:"+e.getMessage());
			e.printStackTrace();
		}
    }
}
