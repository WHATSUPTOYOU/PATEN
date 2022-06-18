package TokenAnalysis.TokenAna.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class getDiffFromPV {
    public static String[] getDiff(String vul, String pat){
        String[] DelAdd = new String[2];
        ArrayList<String>[] s = new ArrayList[2];
        s[0] = new ArrayList<>();
        s[0] = readFile(vul);
        s[1] = new ArrayList<>();
        s[1] = readFile(pat);
        DelAdd[0] = getDiffString(s[0], s[1]);
        DelAdd[1] = getDiffString(s[1], s[0]);
        return DelAdd;
    }

    private static String getDiffString(ArrayList<String> ori, ArrayList<String> tgt){
        String s = "";
        for(String a: ori){
            if(!tgt.contains(a))
                s += a;
        }
        return s;
    }

    private static ArrayList<String> readFile(String filepath){
        ArrayList<String> lineTxt = new ArrayList<>();
        try {
            String encoding="GBK";
            File file=new File(filepath);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String tmp;
                while((tmp = bufferedReader.readLine()) != null){
                    lineTxt.add(tmp + "\n");
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
