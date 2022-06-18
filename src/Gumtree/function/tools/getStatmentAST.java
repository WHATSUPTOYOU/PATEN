package Gumtree.function.tools;

import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.Node;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.TreeMapfromJDT;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;

import java.io.IOException;
import java.util.*;

public class getStatmentAST {
    List<String> ori;
    List<String> tgt;
    Map<String , Integer> tokenWeight;
    int total = 0;
    int dis = 0;
    public double disNorm;

    public static void main(String[] args) throws IOException {
        List<String> ori = new ArrayList<>();
        ori.add("int index=qualifiedName.indexOf(':');");
        ori.add("if(index<0){");
        ori.add("String name=qualifiedName.substring(index+1);");
        List<String> tgt = new ArrayList<>();
        tgt.add("int index=qualifiedName.indexOf(58);");
        tgt.add("if(index<0){");
        tgt.add("String name=qualifiedName.substring(index+1);");
        Map<String , Integer> map = new HashMap<>();
        map.put("index",3);
        getStatmentAST g = new getStatmentAST(ori, tgt, map);
//        ASTParser astParserOri = ASTParser.newParser(AST.JLS14);
//        astParserOri.setSource(ori.get(0).toCharArray());
//        astParserOri.setKind(ASTParser.K_STATEMENTS);
//        ASTParser astParserTgt = ASTParser.newParser(AST.JLS14);
//        astParserTgt.setSource(tgt.get(0).toCharArray());
//        astParserTgt.setKind(ASTParser.K_STATEMENTS);
////        CompilationUnit s = (CompilationUnit) astParser.createAST(null);
//        Block block = (Block) astParserOri.createAST(null);
//        Visitor visitorOri = new Visitor(map);
//        block.accept(visitorOri);
//        block = (Block) astParserTgt.createAST(null);
//        Visitor visitorTgt = new Visitor(map);
//        block.accept(visitorTgt);
//        int a = TreeMapfromJDT.ZhangShasha(visitorOri.treeMap, visitorTgt.treeMap);
//
//        System.out.println(a);
    }

    private void calcDis() throws IOException {
        for(int i = 0; i< ori.size();i++) {
            String origStr = ori.get(i);
            String tgtStr = tgt.get(i);
            if(origStr.equals(""))
                continue;
            if(origStr.charAt(origStr.length()-1) == '{')
                origStr = origStr.substring(0,origStr.length()-1);
            if(origStr.charAt(origStr.length()-1) != ';'){
                origStr += ';';
            }
            ASTParser astParserOri = ASTParser.newParser(AST.JLS14);
            astParserOri.setSource(origStr.toCharArray());
            astParserOri.setKind(ASTParser.K_STATEMENTS);
//        CompilationUnit s = (CompilationUnit) astParser.createAST(null);
            Block block = (Block) astParserOri.createAST(null);
            Visitor visitorOri = new Visitor(tokenWeight);
            block.accept(visitorOri);
            char c;
            try {
                c = tgtStr.charAt(tgtStr.length()-1);
            }
            catch (Exception e){
                int cnt = calcTotal(visitorOri.treeMap.root);
                dis += cnt;
                total += cnt;
                continue;
            }
            if(c == '{')
                tgtStr = tgtStr.substring(0,tgtStr.length()-1);
            if(c != ';'){
                tgtStr += ';';
            }
            ASTParser astParserTgt = ASTParser.newParser(AST.JLS14);
            astParserTgt.setSource(tgtStr.toCharArray());
            astParserTgt.setKind(ASTParser.K_STATEMENTS);
            block = (Block) astParserTgt.createAST(null);
            Visitor visitorTgt = new Visitor(tokenWeight);
            block.accept(visitorTgt);
            int a = TreeMapfromJDT.ZhangShasha(visitorOri.treeMap, visitorTgt.treeMap);
            total += calcTotal(visitorOri.treeMap.root);
            total += calcTotal(visitorTgt.treeMap.root);
            dis += a;
        }
    }

    private int calcTotal(Node root){
        int count = 0;
        Stack<Node> nodes = new Stack<>();
        nodes.add(root);
        while(nodes.size() != 0){
            Node s = nodes.pop();
            count += Math.abs(tokenWeight.getOrDefault(s.label.split("/")[0],1));
            if(s.children.size() != 0){
                nodes.addAll(s.children);
            }
        }
        return count;
    }

    public getStatmentAST(List<String> ori, List<String> tgt, Map<String , Integer> tokenWeight) throws IOException {
        this.ori = ori;
        this.tgt = tgt;
        this.tokenWeight = tokenWeight;
        calcDis();
        if(total == 0)
            disNorm = 1.0;
        else
            disNorm = (double)dis/(double) total;
//        System.out.println(disNorm);
    }
}
