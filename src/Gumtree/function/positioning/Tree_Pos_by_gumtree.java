package Gumtree.function.positioning;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public class Tree_Pos_by_gumtree implements TreePosition{
    Tree Correspond_tree_in_target = null;
    boolean parentMatch = false;

//    Map<Tree, Tree> InsertMap = new HashMap<>();
//    Map<Tree, Tree> DeleteMap = new HashMap<>();

    public Tree_Pos_by_gumtree(Tree ori, MappingStore mappings){
        init(mappings, ori);
    }


    private void init(MappingStore mappings, Tree ori){
        for(Mapping map: mappings){
            if(map.first.equals(ori)){
                this.Correspond_tree_in_target = map.second;
                return;
            }
        }
        if(Correspond_tree_in_target == null){  // if can't find target mapping, continue searching parent mapping
            for(Mapping map: mappings){
                if(map.first.equals(ori.getParent())){
                    this.parentMatch = true;
                    return;
                }
            }
        }
    }


    @Override
    public Tree get_correspond_target() {
        return Correspond_tree_in_target;
    }

    public boolean get_parentMatch(){return parentMatch;}

}
