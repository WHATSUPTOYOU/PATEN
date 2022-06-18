package Gumtree.function.similaritycalc.GraphBasedSim;

import Gumtree.DataObj.*;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.utils.Pair;
import Gumtree.function.positioning.Tree_Pos_by_gumtree;

import java.util.*;

public class getGraph {
    public List<EditResObj> InsDelRes = new ArrayList<>();
    //    public List<EditResObj> deleteRes = new ArrayList<>();
    public List<EditResObj> updateRes = new ArrayList<>();
    public int in_match = 0;
    public int del_match = 0;
    public int in_total = 0;
    public int del_total = 0;
    Tree targettree;
    Tree vtree;
    Tree ptree;
    private final String NodeType = "Node";
    private final String TreeType = "Tree";
    public boolean no_target_flag = true;  // Mark if all trees cannot find the target tree in target file
    public Map<String, List<Tree>> VGraph = null;
    public Map<String, List<Tree>> PGraph = null;
    public Map<String, List<Tree>> TGraph = null;
    public List<Pair<Tree, Tree>> VTmap = null;
    public List<Pair<Tree, Tree>> PTmap = null;

    public getGraph(List<List_Structure> in_list, List<List_Structure> del_list, Tree targetTree,
                             Tree src, Tree dst) throws Exception {
        this.targettree = targetTree;
        this.vtree = src;
        this.ptree = dst;
        init();
        setGraph(in_list, del_list);
    }

    private void init(){
        VTmap = new ArrayList<>();
        PTmap = new ArrayList<>();
        VGraph = new HashMap<>();
        PGraph = new HashMap<>();
        TGraph = new HashMap<>();
    }

    private void setGraph(List<List_Structure> in_list, List<List_Structure> del_list){
        Run.initGenerators();
        Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
        if(in_list.size()!=0) {
            MappingStore mappings;
            mappings = defaultMatcher.match(ptree, targettree); // computes the mappings between the trees
            GraphGenerator(in_list, PGraph, TGraph, PTmap, mappings);
        }

        if(del_list.size()!=0){
            MappingStore mappings;
            mappings = defaultMatcher.match(vtree, targettree); // computes the mappings between the trees
            Map<Tree,Tree> map = new HashMap<>();
            for(Mapping mapping:mappings){
                map.put(mapping.first, mapping.second);
            }
            GraphGenerator(del_list, VGraph, TGraph, VTmap, mappings);
        }


    }

    private void GraphGenerator(List<List_Structure> list, Map<String, List<Tree>> OriGraph, Map<String, List<Tree>> TGraph, List<Pair<Tree, Tree>> map, MappingStore mappings){
        Iterator<List_Structure> ite = list.iterator();
        while(ite.hasNext()){
            List_Structure tuple = ite.next();
            if(tuple.type.equals(NodeType)){  // node type only has one node
                if(!OriGraph.containsKey(NodeType)){
                    OriGraph.put(NodeType, new ArrayList<>());
                }
                OriGraph.get(NodeType).add(tuple.rev_tree);
                Tree_Pos_by_gumtree tree_pos_by_gumtree = new Tree_Pos_by_gumtree(tuple.rev_tree, mappings);
                Tree target = tree_pos_by_gumtree.get_correspond_target();
                if(target != null){
                    if (target.getType().toString().equals("ThisExpression")) {
                        ite.remove();
                        continue;
                    }
                    if(!TGraph.containsKey(NodeType))
                        TGraph.put(NodeType, new ArrayList<>());
                    TGraph.get(NodeType).add(target);
                    map.add(new Pair<>(tuple.rev_tree, target));
                    no_target_flag = false;
                }
                else{
                    if(tree_pos_by_gumtree.get_parentMatch())
                        no_target_flag = false;
                }
            }
            else {
                if(!OriGraph.containsKey(TreeType)){
                    OriGraph.put(TreeType, new ArrayList<>());
                }
                OriGraph.get(TreeType).add(tuple.rev_tree);
                Tree_Pos_by_gumtree tree_pos_by_gumtree = new Tree_Pos_by_gumtree(tuple.rev_tree, mappings);
                Tree target = tree_pos_by_gumtree.get_correspond_target();
                if(target != null){
                    if(!TGraph.containsKey(TreeType))
                        TGraph.put(TreeType, new ArrayList<>());
                    TGraph.get(TreeType).add(target);
                    map.add(new Pair<>(tuple.rev_tree, target));
                    no_target_flag = false;
                }
                else{
                    if(tree_pos_by_gumtree.get_parentMatch())
                        no_target_flag = false;
                }
            }
        }
    }
}
