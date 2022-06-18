package TokenAnalysis.TokenAna.Listeners;

import TokenAnalysis.AntlrJava.JavaParser;
import TokenAnalysis.AntlrJava.JavaParserBaseListener;

public class noMethodListener extends JavaParserBaseListener {
    public boolean ContainMethod = false;
    @Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        ContainMethod = true;
    }

    @Override public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) { ContainMethod = true; }
}
