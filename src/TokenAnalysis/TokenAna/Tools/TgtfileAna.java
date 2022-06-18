package TokenAnalysis.TokenAna.Tools;

import TokenAnalysis.AntlrJava.JavaLexer;
import TokenAnalysis.AntlrJava.JavaParser;
import TokenAnalysis.TokenAna.Listeners.noMethodListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class TgtfileAna {
    public static boolean ContainMethod(String tgtfile){
        String content = readFile(tgtfile);
        CharStream inputs = CharStreams.fromString(content);
        JavaLexer lexer = new JavaLexer(inputs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();
        noMethodListener listener = new noMethodListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        return listener.ContainMethod;
    }

    private static String readFile(String filepath){
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

    public static void main(String[] args) {
        System.out.println(TgtfileAna.ContainMethod("/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGBOUNCYCASTLE-474616/uninfluenced_package/cd9832/XMSSUtil.java/bcprov-ext-jdk15on-1.66.jar/tfile.java"));
    }

}
