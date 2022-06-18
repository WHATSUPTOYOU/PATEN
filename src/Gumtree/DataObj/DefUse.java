package Gumtree.DataObj;

import com.github.gumtreediff.tree.Tree;

public class DefUse {
    public Tree usePoint;
    public Tree defRoot;
    public int defLine;
    public DefUse(Tree usePoint, Tree defRoot, int defLine){
        this.defLine = defLine;
        this.usePoint = usePoint;
        this.defRoot = defRoot;
    }
}
