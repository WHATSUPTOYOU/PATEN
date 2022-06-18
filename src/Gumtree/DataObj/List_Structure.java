package Gumtree.DataObj;

import com.github.gumtreediff.tree.Tree;

import java.io.Serializable;
import java.util.List;

public class List_Structure implements Serializable {  //  data structure of revision part, include revised tree, type(node/tree), method signature
    public Tree rev_tree;
    public String treePosition;
    public String type;
    //    public List<Tree> method;
    public String OpType;

    public List_Structure(Tree rev_tree, String type){
//        this.method = method;  //remove method signature
        this.rev_tree = rev_tree;
        this.type = type;
        this.OpType = "Insert/Delete";
    }

    public List_Structure(String pos, String type, String opType){
//        this.method = method;  //remove method signature
        this.treePosition = pos;
        this.type = type;
        this.OpType = opType;
    }

    public List_Structure(Tree t, String type, String opType){
//        this.method = method;  //remove method signature
        this.rev_tree = t;
        this.type = type;
        this.OpType = opType;
    }

}