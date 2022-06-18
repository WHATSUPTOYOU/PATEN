package TokenAnalysis.AntlrJava;

public class MyVisitor<T> extends JavaParserBaseVisitor<T>{

    @Override public T visitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        System.out.println(111);
        return visitChildren(ctx); }

    @Override public T visitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        System.out.println(111);
        return visitChildren(ctx); }

}
