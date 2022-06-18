package TokenAnalysis.TokenAna.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class otherTools {
    public static int[] strlisttoint(ArrayList<String> list){
        if(list.size() == 0)
            return null;
        ArrayList<Integer> intlist = new ArrayList<>();
        for(String s: list){
            String[] tmp = s.split(",");
            for(String lines: tmp) {
                String[] line = lines.split(",");
                for(String tuple: line) {
                    intlist.add(Integer.valueOf(tuple));
                }
            }
        }
        int[] ret = new int[intlist.size()];
        for(Integer i:intlist){
            ret[intlist.indexOf(i)] = i;
        }
        return ret;
    }

    public static int[] ListtoArray(List<Integer> l){
        int[] res = new int[l.size()];
        for(int i = 0; i < l.size(); i++){
            res[i] = l.get(i);
        }
        return res;
    }

    public static String readFile(String filepath){
        String lineTxt = "";
        try {
            String encoding="GBK";
            File file=new File(filepath);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String tmp;
                while((tmp = bufferedReader.readLine()) != null){
                    lineTxt += (tmp + "\n");
                }
                read.close();
            }else{
                System.out.println("cannot find target files");
            }
        } catch (Exception e) {
            System.out.println("read error");
            e.printStackTrace();
        }
        return lineTxt;
    }
}
