package Gumtree.function.tools;

import Configure.RuntimeConfig;
import Gumtree.DataObj.DefUse;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.Node;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.TreeMap;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.utils.Pair;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.util.*;

public class SlicingGraph {
    CompilationUnit unitadd;
    CompilationUnit unittgt;
    MappingStore mappingsPT;
    List<Integer> slicedLine;
//    Map<String, Integer> defs;
    Map<Integer, Integer> UseMap = new HashMap<>();  //line map from add to tgt
    List<Pair<Integer, Integer>> connectionAdd = new ArrayList<>();
    Map<Integer,List<DefUse>> connectionPoistion = new HashMap<>();
//    List<Pair<Tree, Tree>> connectionPoistion = new ArrayList<>();
    List<Integer> tgtLine = new ArrayList<>();
    Map<String, Map<Integer, List<Integer>>> def_use;
    Map<String, Integer> tokenWeight;


    Map<Integer, Tree> originCtxTrees = new HashMap<>();
    Map<Integer, Tree> targetCtxTrees = new HashMap<>();
    public double NormDistance;
    double th;


    public SlicingGraph(List<Integer> slicedLine, Map<String, Map<Integer, List<Integer>>> def_use, Tree oritree,
                        Tree ttree, Map<String, Integer> tokenChange) throws IOException {
        this.slicedLine = slicedLine;
        this.def_use = def_use;
        this.tokenWeight = tokenChange;
        Run.initGenerators();
        Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
        mappingsPT = defaultMatcher.match(oritree, ttree);
        generateCom();
        travelMap();
//        UseMapFilter();
        DefUseFusion();

//        List<Tree> treeListOri = new ArrayList<>();
//        for(int line:originCtxTrees.keySet())
//            treeListOri.add(originCtxTrees.get(line));
        TreeMap treeMapOri = new TreeMap(originCtxTrees, unitadd, tokenWeight, connectionPoistion);
//        List<Tree> treeListTgt = new ArrayList<>();
//        for(int line:targetCtxTrees.keySet())
//            treeListTgt.add(targetCtxTrees.get(line));
        TreeMap treeMapTgt = new TreeMap(targetCtxTrees, unittgt, tokenWeight);
        if(originCtxTrees.size() == 0 || targetCtxTrees.size() == 0) {
            NormDistance = 1.0;
            return;
        }
        int distance = TreeMap.ZhangShasha(treeMapOri, treeMapTgt);
        int total;
        total = calcTotal(treeMapOri.root) + calcTotal(treeMapTgt.root);
        NormDistance = (double)distance/(double)total;
    }

    private void UseMapFilter() throws IOException {
//        double th = 0.1;
        int distanceTotal = 0;
        int totalTotal = 0;
        for(int oriLine:UseMap.keySet()){
            TreeMap treeMapOri = new TreeMap(originCtxTrees.get(oriLine),tokenWeight);
            TreeMap treeMapTgt = new TreeMap(targetCtxTrees.get(UseMap.get(oriLine)), tokenWeight);
            int distance = TreeMap.ZhangShasha(treeMapOri, treeMapTgt);
            int total = calcTotal(treeMapOri.root) + calcTotal(treeMapTgt.root);
            double NorDistance = (double)distance/(double)total;
            if(NorDistance <= th) {
                distanceTotal += distance;
                totalTotal += total;
            }
        }
        if(totalTotal == 0)
            NormDistance = 1.0;
        else
            NormDistance = (double)distanceTotal/(double)totalTotal;
    }

    private void DefUseFusion(){
        for(Pair<Integer, Integer> pair:connectionAdd){
            getFusionPoint(originCtxTrees.get(pair.first), originCtxTrees.get(pair.second), pair.first, pair.second);
        }
    }

    private void getFusionPoint(Tree def, Tree use, int defLine, int useLine){
        String token = null;
        for(Tree t:def.getDescendants()){
            if((t.getType().name.equals("Assignment") && t.getChild(0).getType().name.equals("SimpleName"))||
                    (t.getType().name.equals("VariableDeclarationFragment") && t.getChild(0).getType().name.equals("SimpleName")))
                token = t.getChild(0).getLabel();
        }
        if(token != null)
            for(Tree t: use.getDescendants()){
                if(t.getLabel().equals(token)) {
                    connectionPoistion.computeIfAbsent(useLine, k -> new ArrayList<>());
                    connectionPoistion.get(useLine).add(new DefUse(t, def, defLine));
                    return;
                }
            }
//        connectionPoistion.add(new Pair<>(null, null));
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

    private void getSubtree(CompilationUnit cu, Map<Integer, Tree> CtxTrees, Tree startpoint, int line){
        // get subtrees in origin file
        while(cu.getLineNumber(startpoint.getParent().getPos()) == line)
            startpoint = startpoint.getParent();
        CtxTrees.put(line, startpoint);
    }

    private void travelMap(){
        for(Mapping map:mappingsPT){
            int tgtLine = unittgt.getLineNumber(map.second.getPos());
            int oriLine = unitadd.getLineNumber(map.first.getPos());
            if(slicedLine.contains(oriLine)){
                if(!UseMap.containsKey(oriLine))
                    UseMap.put(oriLine, tgtLine);
                if(!originCtxTrees.containsKey(oriLine)){
                    getSubtree(unitadd, originCtxTrees, map.first, oriLine);
                }
                if(!targetCtxTrees.containsKey(tgtLine)){
                    getSubtree(unittgt, targetCtxTrees, map.second, tgtLine);
                }
//                if(DefMap.containsValue(tgtLine)) {
//                for(int orilineDef:DefMap.keySet())
//                    if (DefMap.get(orilineDef) == tgtLine && orilineDef != oriLine)
//                        connectionAdd.put(DefMap.get(orilineDef), oriLine);
            }
//            if(defs.containsValue(oriLine)){
//                if(!DefMap.containsKey(oriLine))
//                    DefMap.put(oriLine, tgtLine);
//                for(int orilineUse:UseMap.keySet())
//                    if (UseMap.get(orilineUse) == tgtLine && orilineUse != oriLine)
//                        connectionAdd.put(oriLine, UseMap.get(orilineUse));
//            }
        }
//        UseMap.put(145,55);
        for(int oriLine:UseMap.keySet()){
            if(!tgtLine.contains(UseMap.get(oriLine)))
                tgtLine.add(UseMap.get(oriLine));
            else{
                for(int ori:UseMap.keySet()){
                    if(UseMap.get(ori) == UseMap.get(oriLine) && ori != oriLine){
                        connectionAdd.add(oriLine>ori?new Pair<>(ori,oriLine):new Pair<>(oriLine,ori));
                    }
                }
            }
        }
        for(int i = 0; i < connectionAdd.size(); i++) {
            Pair<Integer, Integer> con = connectionAdd.get(i);
            boolean isDefUse = false;
            for (String token : def_use.keySet()) {
                for(int defline:def_use.get(token).keySet()) {
                    for(int useline : def_use.get(token).get(defline)){
                        if((con.first == defline && con.second == useline)||(con.second == defline && con.first == useline))
                            isDefUse = true;
                    }
                }
            }
            if(!isDefUse) {
                connectionAdd.remove(con);
                i--;
            }
        }
    }

    private void generateCom() throws IOException {
//        Document document = new Document( Configure.RuntimeFiles.runtimeVfile);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(FileReaders.readFileToString(RuntimeConfig.runtimePfile).toCharArray());
        unitadd = (CompilationUnit)parser.createAST(null);
        parser.setSource(FileReaders.readFileToString(RuntimeConfig.runtimeTfile).toCharArray());
        unittgt = (CompilationUnit)parser.createAST(null);
    }

}
