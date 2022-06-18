package Gumtree.function.AntlrJava.Listener;

import Gumtree.DataObj.DefUse;
import Gumtree.function.AntlrJava.JavaLexer;
import Gumtree.function.AntlrJava.JavaParser;
import Gumtree.function.AntlrJava.JavaParserBaseListener;
import com.github.gumtreediff.utils.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class slicingListenerDefUse extends JavaParserBaseListener {   // Def-Use part need improve
    boolean inMethod = false; //true when get into method

    List<Boolean> statusofTokens;
    List<String> Tokens;
    List<String> methods;
    String tmpmethod;
    Map<String, Integer> tokenPosition;
    boolean AssignFlag = false;
    int AssignPosi;

    Map<String,Map<Integer, Integer>> brachesDef = new HashMap<>(); // record branches which has definition: <Token, Map<branch, Def>>
    public Map<String, Map<Integer, List<Integer>>> def_use = new HashMap<>();
//    Map<String, List<Integer>> crtDef = new HashMap<>();
    public Map<String, List<Integer>> slicedLineSig = new HashMap<>();  // not consider about branch yet

    int tmpbranch = 1;

    public slicingListenerDefUse(List<String> Tokens, List<String> methods, Map<String, Integer> tokenPosition){
        this.statusofTokens = new ArrayList<>();
        for(String t:Tokens){  // initialize lists
            statusofTokens.add(false);
            brachesDef.put(t, new HashMap<>());
            def_use.put(t, new HashMap<>());
//            crtDef.put(t,new ArrayList<>());
            slicedLineSig.put(t, new ArrayList<>());
        }
        this.tokenPosition = tokenPosition;
        this.Tokens = Tokens;
        this.methods = methods;
    }

//    private void updateDefMap(TerminalNode t){
//        if(defs.containsKey(t.getText())){
//            defs.put(t.getText(),t.getSymbol().getLine());
//        }
//    }


    @Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        inMethod = true;
        for(ParseTree p:ctx.children){
            if(p instanceof TerminalNodeImpl && ((TerminalNodeImpl) p).getSymbol().getType() == JavaLexer.IDENTIFIER){
                tmpmethod = p.getText();
            }
        }
    }

    @Override public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) { inMethod = false; }

    @Override public void enterPrimary(JavaParser.PrimaryContext ctx) {
        if(AssignFlag) { // If is a assignment expression
            if (ctx.getTokens(JavaLexer.IDENTIFIER).size() != 0){
                for(TerminalNode t :ctx.getTokens(JavaLexer.IDENTIFIER)){
                    if(t.getSymbol().getCharPositionInLine() < AssignPosi) {
                        if(!Tokens.contains(t.getText()) || !inMethod)
                            continue;
                        if (tokenPosition.get(t.getText()) >= t.getSymbol().getLine()) {
                            if (brachesDef.get(t.getText()).containsKey(tmpbranch)){
                                slicedLineSig.put(t.getText(), new ArrayList<>());
                                def_use.get(t.getText()).put(brachesDef.get(t.getText()).get(tmpbranch),new ArrayList<>());
                            }
                            slicedLineSig.get(t.getText()).add(t.getSymbol().getLine());
//                            slicedLine.add(t.getSymbol().getLine());
//                            if(!brachesDef.get(t.getText()).containsKey(tmpbranch)) {
//                                crtDef.get(t.getText()).add(t.getSymbol().getLine());
                            brachesDef.get(t.getText()).put(tmpbranch, t.getSymbol().getLine());
//                            }
                            def_use.get(t.getText()).put(t.getSymbol().getLine(),new ArrayList<>());
                            return;
                        } else if (tokenPosition.get(t.getText()) < t.getSymbol().getLine()) {
                            statusofTokens.set(Tokens.indexOf(t.getText()), true);
                        }
                    }
                }
                AssignFlag = false;
            }
        }
        if(Tokens.contains(ctx.getText()) && inMethod){
            if(tmpmethod.equals(methods.get(Tokens.indexOf(ctx.getText()))) &&
                    !statusofTokens.get(Tokens.indexOf(ctx.getText())) ){
//                if(!slicedLine.contains(ctx.start.getLine()))
//                    slicedLine.add(ctx.start.getLine());
//                if(!slicedLineSig.containsKey(ctx.getText())){
//                    slicedLineSig.put(ctx.getText(),new ArrayList<>());
//                }
//                if(slicedLineSig.get(ctx.getText()).contains(ctx.start.getLine()))
                if(tokenPosition.get(ctx.getText()) <= ctx.start.getLine())
                    slicedLineSig.get(ctx.getText()).add(ctx.start.getLine());
                for(int brach:brachesDef.get(ctx.getText()).keySet()){ // for all defines
                    def_use.get(ctx.getText()).get(brachesDef.get(ctx.getText()).get(brach)).add(ctx.start.getLine());
                }
            }
        }
    }

//    @Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
//        for(ParseTree tree: ctx.LBRACK()){
//            if(tree instanceof TerminalNode){
//                if(Tokens.contains(((TerminalNode) tree).getSymbol().getText()))
//                    System.out.println(1);
//            }
//        }
//    }

    @Override public void enterVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        if(Tokens.contains(ctx.start.getText()) && inMethod){
            // check if this dec is closer than target line
            if(tokenPosition.get(ctx.start.getText()) >= ctx.start.getLine()){
//                slicedLine.add(ctx.start.getLine());
                if(brachesDef.get(ctx.start.getText()).containsKey(tmpbranch)) {
                    slicedLineSig.put(ctx.start.getText(), new ArrayList<>());
                    def_use.get(ctx.start.getText()).put(brachesDef.get(ctx.start.getText()).get(tmpbranch),new ArrayList<>());
                }
                slicedLineSig.get(ctx.start.getText()).add(ctx.start.getLine());
//                crtDef.put(ctx.start.getText(), ctx.start.getLine()); // clear slicing result
                brachesDef.get(ctx.start.getText()).put(tmpbranch, ctx.start.getLine());
                def_use.get(ctx.start.getText()).put(ctx.start.getLine(),new ArrayList<>());
            }
            else if(tokenPosition.get(ctx.start.getText()) < ctx.start.getLine()){
                statusofTokens.set(Tokens.indexOf(ctx.start.getText()), true);
            }
//            if(tmpmethod.equals(methods.get(Tokens.indexOf(ctx.getText()))) && !statusofTokens.get(Tokens.indexOf(ctx.getText()))){
//                updateDefMap(t,);
//            }
//                updateMap(ctx.start.getText(), ctx.start.getLine());
        }
    }

    @Override public void enterExpression(JavaParser.ExpressionContext ctx) {
        if(ctx.getTokens(JavaLexer.ASSIGN).size() == 1){
            AssignFlag = true;
            AssignPosi = ctx.getTokens(JavaLexer.ASSIGN).get(0).getSymbol().getCharPositionInLine();
        }
    }

    @Override public void enterForControl(JavaParser.ForControlContext ctx) {
        tmpbranch ++;
    }

    @Override public void exitForControl(JavaParser.ForControlContext ctx) {
        tmpbranch --;
    }

    @Override public void enterSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        tmpbranch ++;
    }

    @Override public void exitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        tmpbranch --;
    }


    @Override public void enterStatement(JavaParser.StatementContext ctx) {
        if(ctx.getParent().getTokens(JavaLexer.IF).size() != 0){
            tmpbranch ++;
        }
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation does nothing.</p>
     */
    @Override public void exitStatement(JavaParser.StatementContext ctx) {
        if(ctx.getParent().getTokens(JavaLexer.IF).size() != 0){
            tmpbranch --;
        }
    }
}
