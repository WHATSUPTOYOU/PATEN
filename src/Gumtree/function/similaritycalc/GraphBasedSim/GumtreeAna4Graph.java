package Gumtree.function.similaritycalc.GraphBasedSim;

import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.EditScriptGenerator;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.*;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import com.sun.tools.javac.util.Pair;
import Gumtree.DataObj.List_Structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GumtreeAna4Graph {
    private final MappingStore mappings;
    private final EditScript actions;
    Pair<String, ArrayList<Tree>>[] revision_list;
//    private List<Move> Move = new ArrayList<>();
    Tree src = null;
    Tree dst = null;
    public TreeContext srcTree = null;
    public TreeContext dstTree = null;

    ArrayList<List_Structure> in_structlist;
    ArrayList<List_Structure> del_structlist;


    final static String CompilationUnit_node = "CompilationUnit";  //root of program
    final static String Methoddec_node = "MethodDeclaration";       // method declaration
    final static String Typedec_node = "TypeDeclaration";           // parent of part between class and method
    final static String Fielddec_node = "FieldDeclaration";
    final static String Block_node = "Block";

    public GumtreeAna4Graph(String vulfile, String targetfile) throws IOException {
        Run.initGenerators();
        srcTree = TreeGenerators.getInstance().getTree(vulfile);
        dstTree = TreeGenerators.getInstance().getTree(targetfile);
        this.src = srcTree.getRoot(); // retrieves and applies the default parser for the file
        this.dst = dstTree.getRoot(); // retrieves and applies the default parser for the file
//        System.out.println(dst.getParent());
//        Tree tg = TreeGenerators.getInstance().getTree("./target.java").getRoot();
        Matcher defaultMatcher = Matchers.getInstance().getMatcher(); // retrieves the default matcher
        this.mappings = defaultMatcher.match(src, dst); // computes the mappings between the trees

        EditScriptGenerator editScriptGenerator = new SimplifiedChawatheScriptGenerator(); // instantiates the simplified Chawathe script generator
//        System.out.println(dst.getParent());
        this.actions = editScriptGenerator.computeActions(mappings); // computes the edit script


        handle_actions();
        set_list();
    }


    public ArrayList<List_Structure> get_insert_list(){
        return this.in_structlist;
    }

    public ArrayList<List_Structure> get_delete_list(){return this.del_structlist;}


    public Tree getSrc(){return this.src;}

    public Tree getDst(){ return this.dst;}

    private void handle_actions(){
        this.revision_list = new Pair[5];
//        revision_list[0] = new Pair("MOVE", new ArrayList<Tree>());
        revision_list[1] = new Pair("INSERT", new ArrayList<Tree>());
        revision_list[2] = new Pair("INSERT_TREE", new ArrayList<Tree>());
        revision_list[3] = new Pair("DELETE", new ArrayList<Tree>());
        revision_list[4] = new Pair("DELETE_TREE", new ArrayList<Tree>());
        for(Action action:actions){
            if(action instanceof Update){
                Update update = (Update)action;
                Tree target = find_update_target(update, mappings);  //get target node
                if(target != null && filter_parentnode(update.getNode())){
                    revision_list[4].snd.add(update.getNode());
                    revision_list[2].snd.add(target);
                }
            }
            else if(action instanceof TreeDelete){
                TreeDelete treeDelete = (TreeDelete)action;
                revision_list[4].snd.add(treeDelete.getNode());
            }
            else if(action instanceof TreeInsert){
                TreeInsert treeInsert = (TreeInsert)action;
                revision_list[2].snd.add(treeInsert.getNode());
            }
            else if(action instanceof Insert){
                Insert insert = (Insert)action;
                revision_list[1].snd.add(insert.getNode());
            }
            else if(action instanceof Delete){
                Delete delete = (Delete)action;
                revision_list[3].snd.add(delete.getNode());
            }
//            else if(action instanceof Move){
//                Move move = (Move)action;
//                Move.add(move);
//            }
        }
    }
//
    private static Tree find_update_target(Update update,MappingStore mappings){
        for(Mapping map:mappings){
            if(map.first.equals(update.getNode().getParent())){
                for(Tree tree: map.second.getDescendants()){
                    if (tree.getLabel().equals(update.getValue())) {
                        return tree;
                    }
                }
            }
        }
        return null;
    }

    private void set_list(){
        in_structlist = new ArrayList<>();
        del_structlist = new ArrayList<>();

        for(Tree tree:revision_list[1].snd){
            if(filter_parentnode(tree) && !tree.getType().name.equals("TextElement")) {
                String position = getPositionSeq(tree);
                in_structlist.add(new List_Structure(position, "Node", "Insert"));
            }
        }

        for(Tree tree:revision_list[2].snd){
            if((filter_parentnode(tree) || filter_childnode(tree)) && !tree.getType().name.equals("MarkerAnnotation")) {
                String position = getPositionSeq(tree);
                in_structlist.add(new List_Structure(position, "Tree","Insert"));
            }
        }

        for(Tree tree:revision_list[3].snd){
            if(filter_parentnode(tree) && !tree.getType().name.equals("TextElement")) {
                String position = getPositionSeq(tree);
                del_structlist.add(new List_Structure(position, "Node", "Delete"));
            }
        }


        for(Tree tree:revision_list[4].snd){
            if((filter_parentnode(tree) || filter_childnode(tree)) && !tree.getType().name.equals("MarkerAnnotation")) {
                String position = getPositionSeq(tree);
                del_structlist.add(new List_Structure(position, "Tree", "Delete"));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GumtreeAna4Graph g= new GumtreeAna4Graph("./Gumtree.test.java","./test2.java");
    }

    private boolean filter_parentnode(Tree tree){
//        return (!tree.getParent().getType().name.equals(CompilationUnit_node)
//                && !tree.getParent().getType().name.equals(Typedec_node) && !tree.getParent().getType().name.equals(Block_node)
//                && !tree.getParent().getType().name.equals(Methoddec_node) && !tree.getParent().getType().name.equals(Fielddec_node));
        tree = tree.getParent();
        while (!tree.getType().name.equals(CompilationUnit_node)){
            if(tree.getType().name.equals(Methoddec_node))
                return true;
            tree = tree.getParent();
        }
        return false;
    }
//
    private boolean filter_childnode(Tree tree){
//        return (!tree.getParent().getType().name.equals(CompilationUnit_node)
//                && !tree.getParent().getType().name.equals(Typedec_node) && !tree.getParent().getType().name.equals(Block_node)
//                && !tree.getParent().getType().name.equals(Methoddec_node) && !tree.getParent().getType().name.equals(Fielddec_node));
        if(tree.getType().name.equals(Methoddec_node))
            return true;
        List<Tree> treels = tree.getDescendants();
        for(Tree t: treels){
            if(t.getType().name.equals(Methoddec_node))
                return true;
        }
        return false;
    }

    private String getPositionSeq(Tree t){
        String seq = "";
        while(t.getParent() != null && !t.getParent().getType().name.equals("")){
            seq += String.valueOf(t.positionInParent());
            seq += ",";
            t = t.getParent();
        }
        return seq;
    }
}
