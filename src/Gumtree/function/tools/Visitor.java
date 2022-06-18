package Gumtree.function.tools;

import com.github.gumtreediff.utils.Pair;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.Node;
import Gumtree.function.similaritycalc.GraphBasedSim.zhsh.TreeMapfromJDT;
import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Visitor extends ASTVisitor {

    public TreeMapfromJDT treeMap;
    Map<String, Integer> tokenWeight;
    List<Pair<ASTNode, Node>> nodepair = new ArrayList<>();

    public Visitor(Map tokenWeight) throws IOException {
        this.tokenWeight = tokenWeight;
        treeMap = new TreeMapfromJDT(tokenWeight);
    }

    private void setroot(Block block){
        Node root = treeMap.setRoot("Block");
        nodepair.add(new Pair<>(block, root));
    }

    private void settree(String crtcontent, ASTNode prt, ASTNode crtNode){
        for(Pair<ASTNode, Node> p:nodepair){
            if(p.first.equals(prt)){
                Node crt = treeMap.setTree(p.second, crtcontent);
                nodepair.add(new Pair<>(crtNode, crt));
                return;
            }
        }
    }
    @Override
    public boolean visit(SimpleName node) {
        settree(node.getFullyQualifiedName()+"/SimpleName", node.getParent(), node);

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node){
        settree("/VariableDeclarationStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(ArrayAccess node) {
        settree("/ArrayAccess", node.getParent(), node);
        return true;
    }

    public boolean visit(ArrayCreation node) {
        settree("/ArrayCreation", node.getParent(), node);
        return true;
    }

    public boolean visit(ArrayInitializer node) {
        settree("/ArrayInitializer", node.getParent(), node);
        return true;
    }

    public boolean visit(ArrayType node) {
        settree("/ArrayType", node.getParent(), node);
        return true;
    }

    public boolean visit(AssertStatement node) {
        settree("/AssertStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(Assignment node) {
        settree("/Assignment", node.getParent(), node);
        return true;
    }

    public boolean visit(Block node) {
        setroot(node);
        return true;
    }

    public boolean visit(BooleanLiteral node) {
        settree("/BooleanLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(BreakStatement node) {
        settree("/BreakStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(CastExpression node) {
        settree("/CastExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(CatchClause node) {
        settree("/CatchClause", node.getParent(), node);
        return true;
    }

    public boolean visit(CharacterLiteral node) {
        settree(node.getEscapedValue()+"/CharacterLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(ClassInstanceCreation node) {
        settree("/ClassInstanceCreation", node.getParent(), node);
        return true;
    }


    public boolean visit(ConditionalExpression node) {
        settree("/ConditionalExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(ConstructorInvocation node) {
        settree("/ConstructorInvocation", node.getParent(), node);
        return true;
    }

    public boolean visit(ContinueStatement node) {
        settree("/ContinueStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(CreationReference node) {
        settree("/CreationReference", node.getParent(), node);
        return true;
    }

    public boolean visit(Dimension node) {
        settree("/Dimension", node.getParent(), node);
        return true;
    }

    public boolean visit(DoStatement node) {
        settree("/DoStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(EmptyStatement node) {
        settree("/EmptyStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(EnhancedForStatement node) {
        settree("/EnhancedForStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(EnumConstantDeclaration node) {
        settree("/EnumConstantDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(EnumDeclaration node) {
        settree("/EnumDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(ExportsDirective node) {
        settree("/ExportsDirective", node.getParent(), node);
        return true;
    }

    public boolean visit(ExpressionMethodReference node) {
        settree("/ExpressionMethodReference", node.getParent(), node);
        return true;
    }

    public boolean visit(ExpressionStatement node) {
        settree("/ExpressionStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(FieldAccess node) {
        settree("/FieldAccess", node.getParent(), node);
        return true;
    }

    public boolean visit(FieldDeclaration node) {
        settree("/FieldDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(ForStatement node) {
        settree("/ForStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(IfStatement node) {
        settree("/IfStatement", node.getParent(), node);
        return true;
    }


    public boolean visit(InfixExpression node) {
        settree("/InfixExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(Initializer node) {
        settree("/Initializer", node.getParent(), node);
        return true;
    }

    public boolean visit(InstanceofExpression node) {
        settree("/InstanceofExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(IntersectionType node) {
        settree("/IntersectionType", node.getParent(), node);
        return true;
    }

    public boolean visit(LabeledStatement node) {
        settree("/LabeledStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(LambdaExpression node) {
        settree("/LambdaExpression", node.getParent(), node);
        return true;
    }


    public boolean visit(MemberRef node) {
        settree("/MemberRef", node.getParent(), node);
        return true;
    }

    public boolean visit(MemberValuePair node) {
        settree("/MemberValuePair", node.getParent(), node);
        return true;
    }

    public boolean visit(MethodRef node) {
        settree("/MethodRef", node.getParent(), node);
        return true;
    }

    public boolean visit(MethodRefParameter node) {
        settree("/MethodRefParameter", node.getParent(), node);
        return true;
    }

    public boolean visit(MethodDeclaration node) {
        settree("/MethodDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(MethodInvocation node) {
        settree("/MethodInvocation", node.getParent(), node);
        return true;
    }

    public boolean visit(Modifier node) {
        settree("/Modifier", node.getParent(), node);
        return true;
    }

    public boolean visit(ModuleDeclaration node) {
        settree("/ModuleDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(ModuleModifier node) {
        settree("/ModuleModifier", node.getParent(), node);
        return true;
    }

    public boolean visit(NameQualifiedType node) {
        settree("/NameQualifiedType", node.getParent(), node);
        return true;
    }


    public boolean visit(NullLiteral node) {
        settree("/NullLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(NumberLiteral node) {
        settree(node.getToken() + "/NumberLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(OpensDirective node) {
        settree("/OpensDirective", node.getParent(), node);
        return true;
    }

    public boolean visit(PackageDeclaration node) {
        settree("/PackageDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(ParameterizedType node) {
        settree("/ParameterizedType", node.getParent(), node);
        return true;
    }

    public boolean visit(ParenthesizedExpression node) {
        settree("/ParenthesizedExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(PostfixExpression node) {
        settree("/BooleanLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(PrefixExpression node) {
        settree("/PrefixExpression", node.getParent(), node);
        
        return true;
    }

    public boolean visit(ProvidesDirective node) {
        settree("/ProvidesDirective", node.getParent(), node);
        
        return true;
    }

    public boolean visit(PrimitiveType node) {
        settree("/PrimitiveType", node.getParent(), node);
        
        return true;
    }

    public boolean visit(QualifiedName node) {
        settree(node.getFullyQualifiedName()+"/QualifiedName", node.getParent(), node);
        
        return true;
    }

    public boolean visit(QualifiedType node) {
        settree("/QualifiedType", node.getParent(), node);
        
        return true;
    }

    public boolean visit(RequiresDirective node) {
        settree("/RequiresDirective", node.getParent(), node);
        return true;
    }

    public boolean visit(RecordDeclaration node) {
        settree("/RecordDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(ReturnStatement node) {
        settree("/ReturnStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(SimpleType node) {
        settree("/SimpleType", node.getParent(), node);
        return true;
    }

    public boolean visit(SingleVariableDeclaration node) {
        settree("/SingleVariableDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(StringLiteral node) {
        settree(node.getLiteralValue() + "/StringLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(SuperConstructorInvocation node) {
        settree("/SuperConstructorInvocation", node.getParent(), node);
        return true;
    }

    public boolean visit(SuperFieldAccess node) {
        settree("/SuperFieldAccess", node.getParent(), node);
        return true;
    }

    public boolean visit(SuperMethodInvocation node) {
        settree("/SuperMethodInvocation", node.getParent(), node);
        return true;
    }

    public boolean visit(SuperMethodReference node) {
        settree("/SuperMethodReference", node.getParent(), node);
        return true;
    }

    public boolean visit(SwitchCase node) {
        settree("/SwitchCase", node.getParent(), node);
        return true;
    }

    public boolean visit(SwitchExpression node) {
        settree("/SwitchExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(SwitchStatement node) {
        settree("/SwitchStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(SynchronizedStatement node) {
        settree("/SynchronizedStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(TagElement node) {
        settree("/TagElement", node.getParent(), node);
        return true;
    }

    public boolean visit(TextBlock node) {
        settree("/TextBlock", node.getParent(), node);
        return true;
    }

    public boolean visit(TextElement node) {
        settree("/TextElement", node.getParent(), node);
        return true;
    }

    public boolean visit(ThisExpression node) {
        settree("/ThisExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(ThrowStatement node) {
        settree("/ThrowStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(TryStatement node) {
        settree("/TryStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(TypeDeclaration node) {
        settree("/TypeDeclaration", node.getParent(), node);
        return true;
    }

    public boolean visit(TypeDeclarationStatement node) {
        settree("/TypeDeclarationStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(TypeLiteral node) {
        settree(node.getType().toString() + "/TypeLiteral", node.getParent(), node);
        return true;
    }

    public boolean visit(TypeMethodReference node) {
        settree("/TypeMethodReference", node.getParent(), node);
        return true;
    }

    public boolean visit(TypeParameter node) {
        settree("/TypeParameter", node.getParent(), node);
        return true;
    }

    public boolean visit(UnionType node) {
        settree("/UnionType", node.getParent(), node);
        return true;
    }

    public boolean visit(UsesDirective node) {
        settree("/UsesDirective", node.getParent(), node);
        return true;
    }

    public boolean visit(VariableDeclarationExpression node) {
        settree("/VariableDeclarationExpression", node.getParent(), node);
        return true;
    }

    public boolean visit(VariableDeclarationFragment node) {
        settree("/VariableDeclarationFragment", node.getParent(), node);
        return true;
    }

    public boolean visit(WhileStatement node) {
        settree("/WhileStatement", node.getParent(), node);
        return true;
    }

    public boolean visit(WildcardType node) {
        settree("/WildcardType", node.getParent(), node);
        return true;
    }

    public boolean visit(YieldStatement node) {
        settree("/YieldStatement", node.getParent(), node);
        return true;
    }
}
