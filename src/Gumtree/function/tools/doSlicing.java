package Gumtree.function.tools;

import Gumtree.DataObj.DefUse;
import Gumtree.function.AntlrJava.JavaLexer;
import Gumtree.function.AntlrJava.JavaParser;
import Gumtree.function.AntlrJava.Listener.slicingListener;
import Gumtree.function.AntlrJava.Listener.slicingListenerDefUse;
import Gumtree.function.AntlrJava.Listener.variableUsageListener;
import com.github.difflib.algorithm.DiffException;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class doSlicing {

    String vfile;
    String pfile;
    String tfile;
    public boolean addFlag = true;



    public List<Integer> slicedLine = new ArrayList<>();
    public Map<String, Map<Integer, List<Integer>>> def_use = new HashMap<>();


    public doSlicing(String vfile, String pfile, String tfile) throws IOException, DiffException {
        this.vfile = vfile;
        this.pfile = pfile;
        this.tfile = tfile;
        calcSim();
    }

    private void calcSim() throws IOException, DiffException {
//        initParseTrees();
//        CharStream inputsAdd = CharStreams.fromString(readFile(pfile));
//        JavaLexer lexerAdd = new JavaLexer(inputsAdd);
//        CommonTokenStream tokensAdd = new CommonTokenStream(lexerAdd);
//        JavaParser parserAdd = new JavaParser(tokensAdd);
        ParseTree treeAdd = initParseTrees(pfile);

        List<List<Integer>> res = getDiffLines.compare(vfile, pfile);
        List<Integer> addLines = res.get(0);

        variableUsageListener tokenAddListener = getRelatedTokensSyn(pfile, treeAdd, addLines);
        if(tokenAddListener != null)
            if(tokenAddListener.tokens.size() != 0) {
                getLinesList(tokenAddListener.tokens, tokenAddListener.methods, tokenAddListener.tokenPosition, addLines, treeAdd);
                return;
            }
        ParseTree treeDel = initParseTrees(vfile);
        List<Integer> delLines = res.get(1);
        variableUsageListener tokenDelListener = getRelatedTokensSyn(vfile, treeDel, delLines);
        if(tokenDelListener != null)
            if(tokenDelListener.tokens.size() != 0) {
                getLinesList(tokenDelListener.tokens, tokenDelListener.methods, tokenDelListener.tokenPosition, delLines, treeDel);
                addFlag = false;
            }

    }

    private void getLinesList(List<String> addTokens, List<String> methodsAdd, Map<String, Integer> tokenPositionAdd, List<Integer> addLines, ParseTree treeAdd) throws IOException {

        slicingListenerDefUse s_add = new slicingListenerDefUse(addTokens, methodsAdd, tokenPositionAdd);
        ParseTreeWalker.DEFAULT.walk(s_add, treeAdd);
        for(String token: s_add.slicedLineSig.keySet()){
            for(int line:s_add.slicedLineSig.get(token))
                if(!slicedLine.contains(line))
                    slicedLine.add(line);
        }
//                s_add.slicedLine.removeAll(addLines);
//        slicedLine.removeAll(addLines);
        this.def_use = s_add.def_use;
        // remove def in patch
//                for(String token: s_add.defs.keySet()){
//                    if(addLines.contains(s_add.defs.get(token)))
//                        s_add.defs.remove(token);
//                }
    }


    private ParseTree initParseTrees(String file){
        CharStream inputsDel = CharStreams.fromString(readFile(file));
        JavaLexer lexerDel = new JavaLexer(inputsDel);
        CommonTokenStream tokensDel = new CommonTokenStream(lexerDel);
        JavaParser parserDel = new JavaParser(tokensDel);
        ParseTree treeDel = parserDel.compilationUnit();
        return treeDel;
    }

    private variableUsageListener getRelatedTokensSyn(String file, ParseTree tree, List<Integer> Lines) throws IOException {
        if(Lines.size() == 0)
            return null;
        variableUsageListener listener = new variableUsageListener(Lines);
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        List<String> lex = getRelatedTokensLex(file, Lines);
        // select intersection
        for(int i = 0; i<listener.tokens.size(); i++){
            if(!lex.contains(listener.tokens.get(i))){
                listener.tokenPosition.remove(listener.tokens.get(i));
                listener.tokens.remove(i);
                listener.start.remove(i);
                listener.ending.remove(i);
                i--;
            }
        }
        if(listener.tokens.size() == 0)
            return null;
        return listener;
    }

    private List<String> getRelatedTokensLex(String file, List<Integer> Lines) throws IOException {
        List<String> content = getContent_withline(file, Lines);
        List<String> tokens = new ArrayList<>();
        String s = "";
        for(String t: content)
            s = s + t + "\n";
        CharStream inputs = CharStreams.fromString(s);
        JavaLexer lexer = new JavaLexer(inputs);
        for (Token t : lexer.getAllTokens()) {
            if (t.getType() == JavaLexer.IDENTIFIER) {
                tokens.add(t.getText());
            }
        }
        return tokens;
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
                bufferedReader.close();
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

    public static List<String> readFiletoList(String filepath){
        List<String> lineTxt = new ArrayList<>();
        try {
            String encoding="GBK";
            File file=new File(filepath);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String tmp;
                while((tmp = bufferedReader.readLine()) != null){
                    lineTxt.add(tmp);
                }
                bufferedReader.close();
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

    private List<String> getContent_withline(String file, List<Integer> lines) throws IOException { //Input: revision lines Out: Content of lines
        if(lines.size() == 0)
            return new ArrayList<>();
        List<String> content = new ArrayList<>();
        int cnt = 0;
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String s;
        int lineNO = 0;
        int targetline = lines.get(0);
        while((s = bf.readLine()) != null){
            lineNO ++;
            if(lineNO == targetline){
                content.add(s.replace("this.","").replace("final", ""));
                cnt++;
                if (cnt == lines.size())
                    break;
                targetline = lines.get(cnt);
            }
        }
        bf.close();
        return content;
    }
}
