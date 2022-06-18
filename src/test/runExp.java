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
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            Method[] ms;
            Class c;
            try {   //  注：有些获取不到，这部分方法可能有问题
                classLoad clsld = new classLoad();
                c = clsld.loadCls(f);
                ms = c.getMethods();
            }
            catch (Error e){
                continue;
            }
            for(Method m :ms){  // for each method in class file
                String method_longname = c.getName() + "." + m.getName();
//                Parameter[] params = m.getParameters();
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
        new File(RuntimeConfig.runtimeProj).delete();
        new File(RuntimeConfig.resDir).delete();
//        new File(RuntimeFiles.runtimeTfile).delete();
    }
}
