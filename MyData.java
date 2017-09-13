package com.example.chen.sesor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;

/**
 * Created by chen on 2017/4/17.
 */

public class MyData  {

    public MyData(){
        super();

    }
    public void create_file(String name){
        File file=new File(name);
        if(!file.exists()){
           try{
               file.createNewFile();
               System.out.println("文件已经创建了");
           }catch (IOException e){
               e.printStackTrace();
           }
        }  else
        {
            System.out.println("文件已经存在");
            System.out.println("文件名："+file.getName());
            System.out.println("文件绝对路径为："+file.getAbsolutePath());
            //是存在工程目录下，所以
            System.out.println("文件相对路径为："+file.getPath());

            System.out.println("文件大小为："+file.length()+"字节");
            System.out.println("文件是否可读："+file.canRead());
            System.out.println("文件是否可写："+file.canWrite());
            System.out.println("我呢间是否隐藏："+file.isHidden());
        }
    }
    public void delete_file(String name){
        File file=new File(name);
        if(file.exists())
        {
            file.delete();
            System.out.println("文件已经被删除了");
        }
    }
    public void append(String name,String content){
        try {
            FileWriter fileWriter=new FileWriter(name,true);
            fileWriter.write(content);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void create_folder(String path,String name){
        File file=new File(path+name);
        if(!file.exists()){
            file.mkdir();
        }
    }
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，rn这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉r，或者屏蔽n。否则，将会多出很多空行。
                if (((char) tempchar) != 'r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != 'r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == 'r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
