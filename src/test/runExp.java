package test;

import Configure.DataBaseconfig;
import Configure.RuntimeConfig;
import Preprocess.*;
import TokenAnalysis.main.newDecCheck;
import com.github.gumtreediff.client.Run;
import Gumtree.function.tools.getMethod;
import com.github.javaparser.Providers;

import java.io.File;
//import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import org.objectweb.asm.*;
import org.springframework.asm.Opcodes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Gumtree.main.GraphBasedMain.run_gumtree_ana;

public class runExp extends ClassLoader{


    static void getFileAll(File file, ArrayList<File> fileList) { // get all class files in project
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFileAll(files[i],fileList);
            } else {
                if(files[i].getName().contains(".class"))
                    fileList.add(files[i]);
            }
        }
        return;
    }

    static List<String[]> searchDatabase(String method_longname) throws SQLException {
        String sql = "select distinct filename,commitid from vul_apis where method_longname=?";
        PreparedStatement p = DataBaseconfig.conn.prepareStatement(sql);
        p.setString(1, method_longname);
        ResultSet rs = p.executeQuery();
        List<String[]> resl = new ArrayList<>();
        while(rs.next()){
            String[] strs = new String[2];
            strs[1] = rs.getString("commitid");
            strs[0] = rs.getString("filename");
            resl.add(strs);
        }
        if(resl.size() == 0) return null;
        else return resl;
    }

    static void runMatch(String filename, String commitid, String method_longname) throws IOException { // Match Phase
        new File(RuntimeConfig.resDir).delete(); // remove prior res.json
        List<String> methodList = new ArrayList<>();
        methodList.add(method_longname.split("\\.")[method_longname.split("\\.").length-1]);
        removeMethod.doDelete(RuntimeConfig.runtimeTfile, methodList);

        try {
            Map<String, String> resMap;
            long start = System.currentTimeMillis();
            DataRecover astInfo = new DataRecover(filename, commitid, method_longname);
            long end = System.currentTimeMillis();
            String resstep1 = newDecCheck.DecCheck(RuntimeConfig.runtimeVfile, RuntimeConfig.runtimePfile, RuntimeConfig.runtimeTfile);  //Step 1
            if (!resstep1.equals("Unknown")) {
                resMap = new HashMap<>();
                resMap.put("Analysis Res", resstep1);
                resMap.put("IOCost", String.valueOf(end - start));
                resMap.put("Step", "1");
                long endTotal = System.currentTimeMillis();
                resMap.put("method_longname", method_longname);
                resMap.put("TotalCost", String.valueOf(endTotal - start));
                JsonRW.write(resMap, RuntimeConfig.resDir);
                delete_tempFile();
                return;
            }
            resMap = run_gumtree_ana(RuntimeConfig.runtimeTfile, astInfo); //args[4] is temp arg
            long endTotal = System.currentTimeMillis();
            resMap.put("method_longname", method_longname);
            resMap.put("IOCost", String.valueOf(end - start));
            resMap.put("TotalCost", String.valueOf(endTotal - start));
            JsonRW.write(resMap, RuntimeConfig.resDir);
            delete_tempFile();
        }
        catch (Exception e){
            Map<String, String> resMap = new HashMap<>();
            resMap.put("tfile",filename);
            resMap.put("Analysis Res","Error");
            JsonRW.write(resMap, RuntimeConfig.resDir);
            delete_tempFile();
        }
    }

    public static void main(String[] args) throws Exception { // input: filename, commitid, method_longname, directory to save result, target file
        RuntimeConfig.setRuntimeTFiles("./runtimeFile/tfile.java");
//        runMatch("SingleJwt.java", "13d880", "cn.fxbin.bubble.plugin.token.SingleJwt.parseToken");
        String projectPath = args[0];
        Decompile.unzip(projectPath); // unzip jar, get project saved in ./runtimeFile/Proj
        ArrayList<File> filelist = new ArrayList<>();
        getFileAll(new File(RuntimeConfig.runtimeProj), filelist); //  get all .class file
        for(File f: filelist){  // travel class files, get all method signature
            List<String> methods = new ArrayList<>();
            try {   //  注：有些获取不到，这部分方法可能有问题
                ClassReader clr = new ClassReader(new FileInputStream(f));
                final String classname = clr.getClassName().replace("/",".").replace("$",".");
                clr.accept(new ClassVisitor() {

                    @Override
                    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
                        return;
                    }

                    @Override
                    public void visitSource(String s, String s1) {
                        return;
                    }

                    @Override
                    public void visitOuterClass(String s, String s1, String s2) {
                        return;
                    }

                    @Override
                    public AnnotationVisitor visitAnnotation(String s, boolean b) {
                        return null;
                    }

                    @Override
                    public void visitAttribute(Attribute attribute) {
                        return;
                    }

                    @Override
                    public void visitInnerClass(String s, String s1, String s2, int i) {
                        return;
                    }

                    @Override
                    public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
                        return null;
                    }

                    @Override
                    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
                        if(s.equals("<clinit>")) return null;
                        if(s.equals("<init>")){
                            s = classname.split("\\.")[classname.split("\\.").length - 1];
                        }
                        methods.add(classname + "." + s);
                        return null;
                    }

                    @Override
                    public void visitEnd() {
                        return;
                    }
                },ClassReader.SKIP_DEBUG);
//                System.out.println(methods);
//                classLoad clsld = new classLoad();
//                c = clsld.loadCls(f);
//                ms = c.getMethods();
            }
            catch (Error e){
                continue;
            }
            for(String method_longname :methods){  // for each method in class file
                List<String[]> itemlist = searchDatabase(method_longname); //  search suspect items which might match
                if(itemlist == null) continue;
                else{  // if has correspond items in database
                    Decompile.decmp(f.getPath());   // decompile .class to tfile.java
                    for(String[] strs: itemlist){  // for each item, run Match Phase
                        runMatch(strs[0], strs[1], method_longname);
                    }
                }
            }
        } // get all method signatures
    }

    private static void delete_tempFile(){
        new File(RuntimeConfig.runtimeVfile).delete();
        new File(RuntimeConfig.runtimePfile).delete();
        new File(RuntimeConfig.runtimeTfile).delete();
        new File(RuntimeConfig.runtimeProj).delete();
//        new File(RuntimeConfig.resDir).delete();
//        new File(RuntimeFiles.runtimeTfile).delete();
    }
}
