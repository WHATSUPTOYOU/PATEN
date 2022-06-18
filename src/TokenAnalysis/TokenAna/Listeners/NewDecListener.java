package TokenAnalysis.TokenAna.Listeners;

import TokenAnalysis.AntlrJava.JavaParser;
import TokenAnalysis.AntlrJava.JavaParserBaseListener;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewDecListener extends JavaParserBaseListener {

    boolean triggerForinit = false;
    boolean triggerForParam = false;
    boolean triggerForLamdaParam = false;

    public List<String> tokenList = new ArrayList<>();  // new declaration
    int[] lines;

    public NewDecListener(int[] lines){
        this.lines = lines;
    }

    @Override public void enterForInit(JavaParser.ForInitContext ctx) { triggerForinit = true;}

    @Override public void exitForInit(JavaParser.ForInitContext ctx) { triggerForinit = false;}

    @Override public void enterFormalParameters(JavaParser.FormalParametersContext ctx) { triggerForParam = true;}

    @Override public void exitFormalParameters(JavaParser.FormalParametersContext ctx) { triggerForParam = false;}

    @Override public void enterLambdaParameters(JavaParser.LambdaParametersContext ctx) { triggerForLamdaParam = true;}

    @Override public void exitLambdaParameters(JavaParser.LambdaParametersContext ctx) { triggerForLamdaParam = false;}

    @Override public void enterVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        if(!triggerForinit && !triggerForLamdaParam && !triggerForParam){
            List<ParseTree> ls = ctx.children;
            Iterator<ParseTree> ite = ls.iterator();
            while(ite.hasNext()){
                ParseTree tmp = ite.next();
                if(tmp instanceof TerminalNode)
                    if(ContainingLine(((TerminalNode) tmp).getSymbol().getLine()) && !tokenList.contains(((TerminalNode) tmp).getSymbol().getText())) {
                        String token = ((TerminalNode) tmp).getSymbol().getText();
                        if (!token.equals("[")&&!token.equals("]"))
                            tokenList.add(token);
                    }
            }
        }
    }

    private boolean ContainingLine(int line){
        for(int l:lines){
            if (l == line)
                return true;
        }
        return false;
    }

}
