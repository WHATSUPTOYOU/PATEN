package Preprocess;

import Configure.RuntimeConfig;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class removeMethod {
//    String method;
    static ArrayList<int []> position;//记录不匹配方法的起始结束
    static ArrayList<int []> position2;//记录匹配方法的起始结束
    public static String contentVR = "";
    public static String contentPR = "";

//    public static void main(String[] args)throws IOException,ClassNotFoundException, SQLException {
//        List<String> MethodList = new ArrayList();
//        MethodList.add("SpelView");
//        removeMethod.getPosition(args, MethodList);
//        removeMethod.getPositionForPV(readFileToString("./runtimeFile/vfile.java"), readFileToString("./runtimeFile/pfile.java"), "createProperties");
//    }

    public static void getPosition(String[] filepaths, List<String> MethodList) throws IOException {
        for(String filepath: filepaths){
            doDelete(filepath, MethodList);
        }
    }


    public static void getPositionForT(String tcontent, String Method) throws IOException {
        if(get_method_position(tcontent, Method)){
            updatePosition();
            updatePosition2();
            contentVR = delete_method(tcontent, RuntimeConfig.runtimeTfile);
        }
    }

    public static void getPositionForPV(String Vfilecontent, String Pfilecontent, String Method) throws IOException {
        if(get_method_position(Vfilecontent, Method)){
            updatePosition();
            updatePosition2();
            contentVR = delete_method(Vfilecontent, RuntimeConfig.runtimeVfile);
        }
        if(get_method_position(Pfilecontent, Method)){
            updatePosition();
            updatePosition2();
            contentPR = delete_method(Pfilecontent, RuntimeConfig.runtimePfile);
        }
    }


    public static void doDelete(String filepath,List<String> MethodList) throws IOException {
        if(get_method_position(filepath, MethodList)){
            updatePosition();
            updatePosition2();
            delete_method(filepath);
        }
    }

    private static boolean get_method_position(String path, List<String> MethodList) throws IOException {
        position = new ArrayList<>();
        position2 = new ArrayList<>();
        ASTParser astParser = ASTParser.newParser(AST.JLS14);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(readFileToString(path).toCharArray());
        CompilationUnit unit = (CompilationUnit) astParser.createAST(null);
        if(unit.types().size()==0){
            return false;
        }
        List<Object> class_names = unit.types();
        TypeDeclaration type = (TypeDeclaration)class_names.get(0);

        unit.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
                SimpleName name = node.getName();
//                String parameter = getparameters(node.parameters());
                int start = unit.getLineNumber(node.getStartPosition());
                int end  = unit.getLineNumber(node.getStartPosition()+node.getLength());
                int a[] = new int[2];

                if(!MethodList.contains(name.toString())){
                    a[0] = start;
                    a[1] = end;
                    position.add(a);
                }
                else{
                    a[0] = start;
                    a[1] = end;
                    position2.add(a);
                }
                return true;
            }
        });
        return true;
    }

    private static boolean get_method_position(String content, String Method) throws IOException {
        position = new ArrayList<>();
        position2 = new ArrayList<>();
        ASTParser astParser = ASTParser.newParser(AST.JLS14);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(content.toCharArray());
        CompilationUnit unit = (CompilationUnit) astParser.createAST(null);
        if(unit.types().size()==0){
            return false;
        }
        List<Object> class_names = unit.types();
        TypeDeclaration type = (TypeDeclaration)class_names.get(0);

        unit.accept(new ASTVisitor() {
            public boolean visit(MethodDeclaration node) {
                SimpleName name = node.getName();
//                String parameter = getparameters(node.parameters());
                int start = unit.getLineNumber(node.getStartPosition());
                int end  = unit.getLineNumber(node.getStartPosition()+node.getLength());
                int a[] = new int[2];


                if(!Method.equals(name.toString())){
                    a[0] = start;
                    a[1] = end;
                    position.add(a);
                }
                else{
                    a[0] = start;
                    a[1] = end;
                    position2.add(a);
                }
                return true;
            }
        });
        return true;
    }


    private static String readFileToString(String filePath) throws IOException {
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



    private static void updatePosition(){
        int start1,start2;
        int end1,end2;
        for(int i=0;i<position2.size();i++){
            start2 = position2.get(i)[0];
            end2 = position2.get(i)[1];
            for(int j=0;j<position.size();j++){
                start1 = position.get(j)[0];
                end1 = position.get(j)[1];
                if(start1 >= start2 && end1 <= end2){
                    position.remove(j);
                    j--;
                }
                if(start1 <= start2 && end1 >= end2){
                    position.remove(j);
                    j--;
                }
            }
        }
    }

    private static void updatePosition2(){
        for(int i=0;i<position.size()-1;i++){
            int start1 = position.get(i)[0];
            int end1 = position.get(i)[1];
            int start2 = position.get(i+1)[0];
            int end2 = position.get(i+1)[1];
            if(start2 >= start1 && end2 <= end1){
                position.remove(i+1);
                i--;
            }
        }
    }

    private static void delete_method(String path){
        File file = new File(path);
        File temp = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        int line_number = 0;
        int count_position = 0;
        if(position.size()==0){
            return;
        }
        int start = position.get(count_position)[0];
        int end = position.get(count_position)[1];
        try {
            temp = File.createTempFile("temp", "temp");
            pw = new PrintWriter(temp);
            br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                String line = br.readLine();

                line_number++;
                if(line_number == 2200){
                    int a = 1;
                }
                if(line_number >= start && line_number < end){
                    continue;
                }
                else if(line_number == end && count_position < position.size()-1){
                    count_position++;
                    start = position.get(count_position)[0];
                    end = position.get(count_position)[1];
                    continue;
                }
                else if (line_number == end){
                    continue;
                }
                pw.println(line);
            }
            pw.flush();
            br.close();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            file.delete();
            temp.renameTo(file);
        }
    }

    private static void delete_method_paths(String oripath, String tgtpath){
        File file = new File(oripath);
        File temp = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        int line_number = 0;
        int count_position = 0;
        if(position.size()==0){
            return;
        }
        int start = position.get(count_position)[0];
        int end = position.get(count_position)[1];
        try {
            temp = File.createTempFile("temp", "temp");
            pw = new PrintWriter(temp);
            br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                String line = br.readLine();
                line_number++;
                if(line_number == 2200){
                    int a = 1;
                }
                if(line_number >= start && line_number < end){
                    continue;
                }
                else if(line_number == end && count_position < position.size()-1){
                    count_position++;
                    start = position.get(count_position)[0];
                    end = position.get(count_position)[1];
                    continue;
                }
                else if (line_number == end){
                    continue;
                }
                pw.println(line);
            }
            pw.flush();
            br.close();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            temp.renameTo(new File(tgtpath));
        }
    }

    private static String delete_method(String content, String filepath) throws IOException {
        String[] contentLines = content.split("\n");
        File temp = null;
        PrintWriter pw = null;
        int line_number = 0;
        int count_position = 0;
        String contentAfter = "";
        if(position.size()==0){
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            bw.write(content);
            bw.close();
            return content;
        }
        int start = position.get(count_position)[0];
        int end = position.get(count_position)[1];
        try {
            temp = File.createTempFile("temp", "temp");
            pw = new PrintWriter(temp);
//            br = new BufferedReader(new FileReader(file));
            while (line_number < contentLines.length) {
                String line = contentLines[line_number];
                line_number++;
                if(line_number == 2200){
                    int a = 1;
                }
                if(line_number >= start && line_number < end){
                    continue;
                }
                else if(line_number == end && count_position < position.size()-1){
                    count_position++;
                    start = position.get(count_position)[0];
                    end = position.get(count_position)[1];
                    continue;
                }
                else if (line_number == end){
                    continue;
                }
                contentAfter = contentAfter + line + "\n";
                pw.println(line);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            boolean b = temp.renameTo(new File(filepath));
        }
        return contentAfter;
    }


}
