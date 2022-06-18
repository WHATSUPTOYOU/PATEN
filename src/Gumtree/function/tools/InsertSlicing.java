package Gumtree.function.tools;

import com.github.difflib.algorithm.DiffException;
import Gumtree.function.AntlrJava.JavaLexer;
import Gumtree.function.AntlrJava.JavaParser;
import Gumtree.function.AntlrJava.Listener.*;
import Gumtree.function.tools.VfileSimCalc;
import Gumtree.function.tools.getDiffLines;
import Gumtree.function.tools.simHash;
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

public class InsertSlicing{
    String vfile;
    String pfile;
    String tfile;
    ParseTree treeDel;
    ParseTree treeAdd;
    JavaLexer lexerDel;
    JavaLexer lexerAdd;
    public Map<String, List<Integer>> res = null;
    public Map<String, List<String>> slicingContentTotal = new HashMap<>();
    public List<String> oriStatements = new ArrayList<>();


    boolean match;


    public InsertSlicing(String vfile, String pfile, String tfile) throws IOException, DiffException {
        this.vfile = vfile;
        this.pfile = pfile;
        this.tfile = tfile;
        calcSim();
    }

    private void calcSim() throws IOException, DiffException {
        initParseTrees();
        List<List<Integer>> res = getDiffLines.compare(vfile, pfile);
        List<Integer> addLines = res.get(0);
//        List<Integer> delLines = res.get(1);

        variableUsageListener tokenAddListener = getRelatedTokensSyn(pfile, treeAdd, addLines);
//        List<String> delTokens = getRelatedTokens(treeDel, delLines);
//        List<String> addTokens = getRelatedTokens(treeAdd, addLines);
        slicingContentTotal = getLinesList(tokenAddListener.tokens, tokenAddListener.start, tokenAddListener.ending, tokenAddListener.methods, tokenAddListener.tokenPosition);

    }


    private Map<String, List<String>> getLinesList(List<String> addTokens, List<Integer> addStartLines, List<Integer> addEndLines, List<String> methodsAdd, Map<String, Integer> tokenPositionAdd) throws IOException {
        if(addTokens.size() != 0) {
            slicingListener s_add = new slicingListener(addTokens, methodsAdd, tokenPositionAdd);
            ParseTreeWalker.DEFAULT.walk(s_add, treeAdd);
            Map<String, List<Integer>> slicingContentAdd = s_add.slicingContent;
            for (String token : slicingContentAdd.keySet()) {
                int startLine = addStartLines.get(addTokens.indexOf(token));
                int endLine = addEndLines.get(addTokens.indexOf(token));
//                slicingContentAdd.replace(token, filterListAdd(slicingContentAdd.get(token), startLine, endLine));
                List<String> stlines = getContent_withline(pfile, filterListAdd(slicingContentAdd.get(token), startLine, endLine));
                oriStatements.addAll(stlines);
                slicingContentTotal.put(token, stlines);
//                System.out.println(slicingContentAdd.get(token));
            }
        }
        return slicingContentTotal;
    }


    private List<Integer> filterListAdd(List<Integer> addList, int startline, int endline){
        List<Integer> finalList = new ArrayList<>();
        int startindex = addList.indexOf(startline);
        int endindex = addList.indexOf(endline);
        int head = 0;
        int tail = 0;
        if(addList.size() - (endindex-startindex+1) > 5){
            int pre = 2;
            int lat = 3;
            if(startindex - pre < 0){
                head = 0;
                tail = endindex + (5 - startindex);
            }
            else if(endindex + lat > addList.size() - 1){
                tail = addList.size() - 1;
                head = addList.size() - 4 - (endindex - startindex + 1);
            }
            else {
                head = startindex - 2;
                tail = endindex + 3;
            }
        }
        else {
            head = 0;
            tail = addList.size() - 1;
        }
        for(int i=head;i<=tail;i++){
            if(i<startindex||i>endindex)
                finalList.add(addList.get(i));
        }
        return finalList;

    }


    private void initParseTrees(){
        CharStream inputsDel = CharStreams.fromString(readFile(vfile));
        lexerDel = new JavaLexer(inputsDel);
        CommonTokenStream tokensDel = new CommonTokenStream(lexerDel);
        JavaParser parserDel = new JavaParser(tokensDel);
        this.treeDel = parserDel.compilationUnit();

        CharStream inputsAdd = CharStreams.fromString(readFile(pfile));
        lexerAdd = new JavaLexer(inputsAdd);
        CommonTokenStream tokensAdd = new CommonTokenStream(lexerAdd);
        JavaParser parserAdd = new JavaParser(tokensAdd);
        this.treeAdd = parserAdd.compilationUnit();
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
