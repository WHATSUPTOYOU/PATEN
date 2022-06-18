// get content of every method
package TokenAnalysis.TokenAna.Tools;

import org.eclipse.jdt.core.dom.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class getMethod {
    public ArrayList<String> methods;

    public static void main(String[] args) throws IOException {
        getMethod a = new getMethod("/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGELASTICSEARCH-472589/uninfluenced_package/95bd04/ScriptService.java/elasticsearch-7.3.0.jar/tfile.java");
//        a.TokenAnalysis.TokenAna.Tools.getMethod("/home/usr1/Experiment_tools/JarTestData/simpdata3/SNYK-JAVA-COMTHOUGHTWORKSXSTREAM-1051966/uninfluenced_package/0bcbf5/XStream.java/xstream-1.4.17.jar/tfile.java");
        ArrayList <String> methods = a.get_methods();
        System.out.println(methods.size());
        for(String a1:methods){
            System.out.println(a1);
        }
    }


    public getMethod(String path) throws IOException {
        methods = new ArrayList<>();
        ASTParser astParser = ASTParser.newParser(AST.JLS14);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(readFileToString(path).toCharArray());
        CompilationUnit unit = (CompilationUnit) astParser.createAST(null);
        if(unit.types().size()==0){ //无法处理的文件
            return ;
        }
        List<Object> class_names = unit.types();
        TypeDeclaration type = (TypeDeclaration)class_names.get(0);

        unit.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
                if(node.getBody() == null)
                    return true;
                methods.add(node.getBody().toString());
                return true;
            }
        });
    }
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder fileData = new StringBuilder(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[10];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    public ArrayList<String> get_methods(){return methods;}

}