package Gumtree.function.tools;

import java.io.*;
import java.util.ArrayList;

public class FileReaders {
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    public static void writetoFile(String filecontent, String path) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write(filecontent);
        bw.close();
    }

    public static ArrayList<String> readFile(String filepath){
        ArrayList<String> lineTxt = new ArrayList<>();
        try {
//            String encoding="GBK";
            File file=new File(filepath);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String tmp;
                while((tmp = bufferedReader.readLine()) != null){
                    lineTxt.add(tmp);
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return lineTxt;
    }
}
