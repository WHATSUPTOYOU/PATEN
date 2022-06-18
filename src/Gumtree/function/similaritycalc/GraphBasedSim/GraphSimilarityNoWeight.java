package Gumtree.function.similaritycalc.GraphBasedSim;

import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.utils.Pair;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.TreeMap;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.TreeMapNoWeight;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GraphSimilarityNoWeight {

    Map<String, List<Tree>> VGraph;
    Map<String, List<Tree>> PGraph;
    Map<String, List<Tree>> TGraph;
    List<Pair<Tree, Tree>> VTmap;
    List<Pair<Tree, Tree>> PTmap;
    Map<String, Integer> tokenChange;
    private static String NodeType = "Node";
    private static String TreeType = "Tree";
    public int Pdis = 0;
    public int Vdis = 0;
    public int Vtotal = 0;
    public int Ptotal = 0;
    public int Ttotal = 0;
    public double VTsim;
    public double PTsim;

    public GraphSimilarityNoWeight(Map<String, List<Tree>> VGraph, Map<String, List<Tree>> PGraph, Map<String, List<Tree>> TGraph,
                           List<Pair<Tree, Tree>> VTmap, List<Pair<Tree, Tree>> PTmap, Map<String, Integer> tokenChange) throws IOException {
        this.VGraph = VGraph;
        this.PGraph = PGraph;
        this.PTmap = PTmap;
        this.TGraph = TGraph;
        this.VTmap = VTmap;
        this.tokenChange = tokenChange;
        runVTana();
        runPTana();
    }

    private void runVTana() throws IOException {
//        List<Tree> VNode = VGraph.get(NodeType);
//        List<Tree> VTree = VGraph.get(TreeType);
        List<Tree> TNode = TGraph.get(NodeType);
        List<Tree> TTree = TGraph.get(TreeType);
        int[] TVisitVectorNode;
        int[] TVisitVectorTree;
        if(TNode == null)
            TVisitVectorNode = new int[0];
        else
            TVisitVectorNode = new int[TNode.size()];
        if(TTree == null)
            TVisitVectorTree = new int[0];
        else
            TVisitVectorTree = new int[TTree.size()];
//        for(int i=0;i<TVisitVectorNode.length;i++)
//            TVisitVectorNode[i] = 0;
//        for(int i=0;i<TVisitVectorTree.length;i++)
//            TVisitVectorTree[i] = 0;
//        List<Tree> Vmap = new ArrayList<>();
//        for(Pair<Tree, Tree> map:VTmap){
//            Vmap.add(map.first);
//        }
        if(VGraph.get(NodeType) != null) {
            for (Tree Node : VGraph.get(NodeType)) {   // travel vulnerable nodes
                Vtotal += 1;
                Vdis += 1;
                if(VTmap.size() != 0) {
                    for (Pair<Tree, Tree> map : VTmap) {
                        if (map.first.equals(Node) && map.first.hasSameTypeAndLabel(map.second)) {
                            Ttotal += 1;
                            TVisitVectorNode[TNode.indexOf(map.second)] = 1;
                            Vdis -= 1;
                            break;
                        }
                    }
                }
            }
        }
        if(VGraph.get(TreeType) != null){
            for(Tree tree: VGraph.get(TreeType)){  // travel vulnerable trees
                int size = calcTotalwithNoWeight(tree);
                Vtotal += size;
                Vdis += size;
                if(VTmap.size() != 0) {
                    for(Pair<Tree, Tree> map:VTmap){
                        if(map.first.equals(tree)) {
                            Ttotal += calcTotalwithNoWeight(map.second);
                            TVisitVectorTree[TTree.indexOf(map.second)] = 1;
                            TreeMap Vtreemap = new TreeMap(tree, tokenChange);
                            TreeMap Ttreemap = new TreeMap(map.second, tokenChange);
                            int distance = TreeMapNoWeight.ZhangShasha(Vtreemap, Ttreemap);   // calculate edit distance between V and T
                            Vdis = Vdis + distance - size;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < TVisitVectorNode.length; i++) {
            if (TVisitVectorNode[i] == 0) {
                Ttotal += 1;
                Vdis += 1;
            }
        }
        for (int i = 0; i < TVisitVectorTree.length; i++) {
            if (TVisitVectorTree[i] == 0) {
                int size = calcTotalwithNoWeight(TTree.get(i));
                Ttotal += size;
                Vdis += size;
            }
        }

        VTsim = (double)Vdis/(double)(Ttotal+Vtotal);
    }

    private void runPTana() throws IOException {
        List<Tree> TNode = TGraph.get(NodeType);
        List<Tree> TTree = TGraph.get(TreeType);
        int[] TVisitVectorNode;
        int[] TVisitVectorTree;
        if(TNode == null)
            TVisitVectorNode = new int[0];
        else
            TVisitVectorNode = new int[TNode.size()];
        if(TTree == null)
            TVisitVectorTree = new int[0];
        else
            TVisitVectorTree = new int[TTree.size()];
//        List<Tree> Vmap = new ArrayList<>();
//        for(Pair<Tree, Tree> map:VTmap){
//            Vmap.add(map.first);
//        }
        if(PGraph.get(NodeType) != null) {
            for (Tree Node : PGraph.get(NodeType)) {   // travel vulnerable nodes
                Ptotal += 1;
                Pdis += 1;
                if (PTmap.size() != 0) {
                    for (Pair<Tree, Tree> map : PTmap) {
                        if (map.first.equals(Node) && map.first.hasSameTypeAndLabel(map.second)) {
                            if (TNode != null)
                                TVisitVectorNode[TNode.indexOf(map.second)] = 1;
                            Pdis -= 1;
                            break;
                        }
                    }
                }
            }
        }
        if(PGraph.get(TreeType) != null) {
            for (Tree tree : PGraph.get(TreeType)) {  // travel vulnerable trees
                int size = calcTotalwithNoWeight(tree);
                Ptotal += size;
                Pdis += size;
                if (PTmap.size() != 0) {
                    for (Pair<Tree, Tree> map : PTmap) {
                        if (map.first.equals(tree)) {
                            if (TTree != null)
                                TVisitVectorTree[TTree.indexOf(map.second)] = 1;
                            TreeMap Ptreemap = new TreeMap(tree, tokenChange);
                            TreeMap Ttreemap = new TreeMap(map.second, tokenChange);
                            int distance = TreeMap.ZhangShasha(Ptreemap, Ttreemap);   // calculate edit distance between V and T
                            Pdis = Pdis + distance - size;
                            break;
                        }
                    }
                }
            }
        }
        for(int i=0;i<TVisitVectorNode.length;i++){
            if(TVisitVectorNode[i] == 0){
                Pdis += 1;
            }
        }
        for(int i=0;i<TVisitVectorTree.length;i++){
            if(TVisitVectorTree[i] == 0) {
                int size = calcTotalwithNoWeight(TTree.get(i));
                Pdis += size;
            }
        }
        PTsim = (double)Pdis/(double)(Ttotal+Ptotal);
    }

    private int calcTotalwithNoWeight(Tree A){
        return A.getDescendants().size()+1;
    }

}
