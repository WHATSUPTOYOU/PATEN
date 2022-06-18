// find newly added/deleted declaration tokens represent patch/vulnerable pattern
package TokenAnalysis.TokenAna;

import TokenAnalysis.AntlrJava.JavaLexer;
import TokenAnalysis.AntlrJava.JavaParser;
import TokenAnalysis.TokenAna.Listeners.NewDecListener;
import TokenAnalysis.TokenAna.Tools.getDiffLines;
import TokenAnalysis.TokenAna.Tools.otherTools;
import com.github.difflib.algorithm.DiffException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class newDeclToken {
    public List<String> newAddtk = new ArrayList<>();
    public List<String> newDeltk = new ArrayList<>();

    public static void main(String[] args) throws IOException, DiffException {
        String vfile = "/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGSPRINGFRAMEWORK-30165/influenced_package/a95c3d/ContentNegotiationManagerFactoryBean.java/vfile.java.java";
        String pfile = "/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGSPRINGFRAMEWORK-30165/influenced_package/a95c3d/ContentNegotiationManagerFactoryBean.java/pfile.java";
        String del = readFile(vfile);
        String add = readFile(pfile);
        List<List<Integer>> res= getDiffLines.compare(vfile, pfile);
        newDeclToken newDeclToken = new newDeclToken(add, del,otherTools.ListtoArray(res.get(0)),otherTools.ListtoArray(res.get(1)));
        System.out.println(newDeclToken.newAddtk);
//        FileTokenAna tgtana = new FileTokenAna("/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-COMFASTERXMLJACKSONCORE-32111/influenced_package/6799f8/SubTypeValidator.java/jackson-databind-2.7.9.3.jar/tfile.java");
//        List<String> tokenTotal = tgtana.TotalTokenList;
//        System.out.println(tokenTotal);
    }

    public newDeclToken(String AddContent, String DelContent, int[] addLines, int[] delLines) throws IOException {
        setDeclToken(AddContent,DelContent,addLines,delLines);
    }

    private void setDeclToken(String AddContent, String DelContent, int[] addLines, int[] delLines) throws IOException {
        if(addLines != null) {
            // walking pfile tree and get new declarations
            CharStream inputsAdd = CharStreams.fromString(AddContent);
            JavaLexer lexerAdd = new JavaLexer(inputsAdd);
            CommonTokenStream tokensAdd = new CommonTokenStream(lexerAdd);
            JavaParser parserAdd = new JavaParser(tokensAdd);
            ParseTree treeAdd = parserAdd.compilationUnit();
            NewDecListener listenerAdd = new NewDecListener(addLines);
            ParseTreeWalker.DEFAULT.walk(listenerAdd, treeAdd);
            newAddtk = listenerAdd.tokenList;
        }

        if(delLines != null) {
            // vfile.java
            CharStream inputsDel = CharStreams.fromString(DelContent);
            JavaLexer lexerDel = new JavaLexer(inputsDel);
            CommonTokenStream tokensDel = new CommonTokenStream(lexerDel);
            JavaParser parserDel = new JavaParser(tokensDel);
            ParseTree treeDel = parserDel.compilationUnit();
            NewDecListener listenerDel = new NewDecListener(delLines);
            ParseTreeWalker.DEFAULT.walk(listenerDel, treeDel);
            newDeltk = listenerDel.tokenList;
        }

        removeDupl(newAddtk, newDeltk);


    }

    private void removeDupl(List AddTokens, List DelTokens){
        if(AddTokens.size() == 0|| DelTokens.size() ==0)
            return;
        // remove duplicate tokens in addlist and dellist
        Iterator<String> i = AddTokens.iterator();
        while(i.hasNext()){
            String tmp = i.next();
            if(DelTokens.contains(tmp)){
                i.remove();
//                AddTokens.remove(tmp);
                DelTokens.remove(tmp);
            }
        }
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
}
