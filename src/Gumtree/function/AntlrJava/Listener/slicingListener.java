package Gumtree.function.AntlrJava.Listener;

import Gumtree.function.AntlrJava.JavaLexer;
import Gumtree.function.AntlrJava.JavaParser;
import Gumtree.function.AntlrJava.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class slicingListener extends JavaParserBaseListener {
    boolean inMethod = false; //true when get into method
    public Map<String, List<Integer>> slicingContent = new HashMap<>();
    List<Boolean> statusofTokens;
    List<String> Tokens;
    List<String> methods;
    String tmpmethod;
    Map<String, Integer> tokenPosition;
    Map<String, Integer> tokenDec = new HashMap<>();
    boolean AssignFlag = false;
    int AssignPosi;

    public slicingListener(List<String> addTokens, List<String> methods, Map<String, Integer> tokenPosition){
        this.statusofTokens = new ArrayList<>();
        for(String t:addTokens){
            statusofTokens.add(false);
        }
        this.tokenPosition = tokenPosition;
        this.Tokens = addTokens;
        this.methods = methods;
    }

    private void updateMap(String token, int line){
        if(!slicingContent.containsKey(token)){
            List<Integer> l = new ArrayList<>();
            l.add(line);
            slicingContent.put(token, l);
        }
        else {
            if(!slicingContent.get(token).contains(line))
                slicingContent.get(token).add(line);
        }
    }

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
        if(AssignFlag) {
            if (ctx.getTokens(JavaLexer.IDENTIFIER).size() != 0){
                for(TerminalNode t :ctx.getTokens(JavaLexer.IDENTIFIER)){
                    if(t.getSymbol().getCharPositionInLine() < AssignPosi) {
                        if(!Tokens.contains(t.getText()))
                            continue;
                        if (tokenPosition.get(t.getText()) >= t.getSymbol().getLine()) {
                            slicingContent.put(t.getText(), new ArrayList<>()); // clear slicing result
                        } else if (tokenPosition.get(t.getText()) < t.getSymbol().getLine()) {
                            statusofTokens.set(Tokens.indexOf(t.getText()), true);
                        }
                    }
                }
                AssignFlag = false;
            }
        }
        if(Tokens.contains(ctx.getText()) && inMethod){
            if(tmpmethod.equals(methods.get(Tokens.indexOf(ctx.getText()))) && !statusofTokens.get(Tokens.indexOf(ctx.getText())))
                updateMap(ctx.getText(), ctx.start.getLine());
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
            if(tokenPosition.get(ctx.getText()) >= ctx.start.getLine()){
                slicingContent.put(ctx.getText(),new ArrayList<>()); // clear slicing result
            }
            else if(tokenPosition.get(ctx.getText()) < ctx.start.getLine()){
                statusofTokens.set(Tokens.indexOf(ctx.getText()), true);
            }
            if(tmpmethod.equals(methods.get(Tokens.indexOf(ctx.getText()))) && !statusofTokens.get(Tokens.indexOf(ctx.getText())))
                updateMap(ctx.start.getText(), ctx.start.getLine());
        }
    }

    @Override public void enterExpression(JavaParser.ExpressionContext ctx) {
        if(ctx.getTokens(JavaLexer.ASSIGN).size() == 1){
            AssignFlag = true;
            AssignPosi = ctx.getTokens(JavaLexer.ASSIGN).get(0).getSymbol().getCharPositionInLine();
        }
    }

//    @Override public void enterParExpression(JavaParser.ParExpressionContext ctx) {
//        if(ctx.getTokens(JavaLexer.ASSIGN).size() == 1){
//            System.out.println(ctx.getTokens(JavaLexer.ASSIGN).get(0).getSymbol().getCharPositionInLine());
//        }
//    }
//
//    @Override public void enterVariableModifier(JavaParser.VariableModifierContext ctx) {
//        System.out.println(1);
//    }
//
//    @Override public void enterVariableDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
//        System.out.println(1);
//    }
//
//    @Override public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
//        System.out.println(1);
//    }
////
//
//    @Override public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
//        System.out.println(1);
//    }
//
//    @Override public void enterVariableInitializer(JavaParser.VariableInitializerContext ctx) {
//        if(ctx.start.getLine() == 61)
//            System.out.println(1);
//    }
}
