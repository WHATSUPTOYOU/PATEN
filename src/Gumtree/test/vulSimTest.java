package Gumtree.test;

import com.github.difflib.algorithm.DiffException;
import Gumtree.function.similaritycalc.SimCalcwithSimHash;

import java.io.*;
import java.util.Iterator;
import java.util.List;

public class vulSimTest {
    public static void main(String[] args) throws IOException, DiffException {
        String vfile = "/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGAPACHETOMCAT-1017114/influenced_package/1bbc65/Http2UpgradeHandler.java/vfile.java";
        String pfile = "/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGAPACHETOMCAT-1017114/influenced_package/1bbc65/Http2UpgradeHandler.java/pfile.java";
        String tfile = "/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGAPACHETOMCAT-1017114/influenced_package/1bbc65/Http2UpgradeHandler.java/tomcat-coyote-9.0.12.jar/tfile.java";
//        CharStream inputsDel = CharStreams.fromString(readFile(vfile));
//        JavaLexer lexerDel = new JavaLexer(inputsDel);
//        CommonTokenStream tokensDel = new CommonTokenStream(lexerDel);
//        JavaParser parserDel = new JavaParser(tokensDel);
//        ParseTree treeDel = parserDel.compilationUnit();
        SimCalcwithSimHash s = new SimCalcwithSimHash(vfile, pfile, tfile);
        System.out.println(s.res);
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

    private static String getContent_withline(String file, List<Integer> addlines) throws IOException { //Input: revision lines Out: Content of lines
        String content = "";
        Iterator<Integer> lines_ite = addlines.iterator();
        BufferedReader bf = new BufferedReader(new StringReader(file));
        String s;
        int lineNO = 0;
        int targetline = lines_ite.next();
        while((s = bf.readLine()) != null){
            lineNO ++;
            if(lineNO == targetline){
                content += s;
                if (!lines_ite.hasNext())
                    break;
                targetline = lines_ite.next();
            }
        }
        return content;
    }
}
