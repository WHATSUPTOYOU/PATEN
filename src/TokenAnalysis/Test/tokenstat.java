package TokenAnalysis.Test;

import TokenAnalysis.TokenAna.FileTokenAna;
import TokenAnalysis.TokenAna.TokenRev;
import TokenAnalysis.TokenAna.Tools.ItemsFromsql;
import TokenAnalysis.TokenAna.Tools.JsonRW;
import TokenAnalysis.TokenAna.Tools.getDiffLines;
import TokenAnalysis.TokenAna.Tools.otherTools;
import TokenAnalysis.TokenAna.newDeclToken;
import com.github.difflib.algorithm.DiffException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class tokenstat {

    public static void runtest(File f, File resf, String jsonpath) throws IOException, SQLException, DiffException{
        BufferedReader bfr = new BufferedReader(new FileReader(f));
        BufferedWriter bfw = new BufferedWriter(new FileWriter(resf));
        String tmp;
        ArrayList<String> done = new ArrayList<>();
        Map<String, Integer> tokenRev;   //generally analysis
        ArrayList<Map<String, Integer>> TokenRevAddList;
        Map<String, Integer>[] max;      // tokens changed most
        String vfilecnt = "";
        String pfilecnt = "";
        int[] addLines = null;
        int[] delLines = null;
        List<String> newAdd = null;
        List<String> newDel = null;
        ArrayList<String> methodList = null;
        String vfile = null;
        String pfile = null;

        int count = 0;
        int totalcount = 0, Ttotal=0, Ftotal=0;
        int T=0, F=0;
        int Tright=0, Fright=0;
        boolean tag;

        while((tmp = bfr.readLine()) != null){
//            if(!tmp.contains("/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGECLIPSEJETTYAGGREGATE-31119/influenced_package/4df564/HttpParser.java/jetty-all-7.0.2.v20100331.jar/tfile.java"))
//                continue;
//                System.out.println(1);
            tmp = tmp.split(":")[0];
//            if(TgtfileAna.ContainMethod(tmp))
//                continue;
            totalcount += 1;
            if(tmp.contains("uninfluence")) {
                Ftotal += 1;
                tag = false;
            }
            else {
                Ttotal += 1;
                tag = true;
            }
            String[] s = tmp.split("/");
            StringBuilder sb = new StringBuilder();
//            sb.append(tmp);
            for(int i=0; i<10;i++){
//                if(i == 4) {
//                    sb.append("JarTestData/dataSetFinal/");
//                    continue;
//                }
//                if (i == 5)
//                    continue;
                sb.append(s[i]);
                sb.append("/");
            }
            if(!done.contains(sb.toString())){
//                bfw.write(sb.toString() + "\n");
                done.add(sb.toString());
                vfile = sb.toString() + "vfile.java.java";
                pfile = sb.toString() + "pfile.java";
                String snykid = tmp.split("/")[6];
                String commitid = tmp.split("/")[8];
                String filename = tmp.split("/")[9];
                // get info in from mysql
                ItemsFromsql ite = new ItemsFromsql(snykid, commitid, filename);
                methodList = ite.methodList;
                ArrayList<String> addlines = ite.addlines;
                ArrayList<String> dellines = ite.dellines;
                TokenRev rev = new TokenRev(vfile, pfile, addlines, dellines);
                JsonRW.write(jsonpath, methodList);
                vfilecnt = otherTools.readFile(vfile);
                pfilecnt = otherTools.readFile(pfile);
//                String[] diff = getDiffFromPV.getDiff(vfile.java, pfile);
                tokenRev = rev.TokenTotalRev;
//                bfw.write("Total:" + tokenRev.toString() + "\n");
                max = TokenRev.getMaxToken(tokenRev);
//                bfw.write("MostAdditionToken:" + max[0].toString() + "\n");
//                bfw.write("MostDeletionToken:" + max[1].toString() + "\n");
                List<List<Integer>> res= getDiffLines.compare(vfile, pfile);
                addLines = otherTools.ListtoArray(res.get(0));
                delLines = otherTools.ListtoArray(res.get(1));
                newDeclToken newDeclToken = new newDeclToken(pfilecnt, vfilecnt,addLines,delLines);
                newAdd = newDeclToken.newAddtk;
                newDel = newDeclToken.newDeltk;
            }
            String tfile = tmp;
//            Runtime runtime = Runtime.getRuntime();
            if(newAdd.size() != 0){
                FileTokenAna tgtana = new FileTokenAna(tfile);
                List<String> tokenTotal = tgtana.TotalTokenList;
                FileTokenAna vfana = new FileTokenAna(vfile);
                List<String> vtokenTotal = vfana.TotalTokenList;
                for(String token:newAdd){
                    if(tokenTotal.contains(token) && !vtokenTotal.contains(token)){
                        F+=1;
                        if(!tag)
                            Fright+=1;
//                        else {
//                            bfw.write(tmp + "\n");
//                            bfw.write("Same Token Found:" + token + "\n");
////                            count += 1;
//                        }
                        break;

                    }
                }
            }
            else if(newDel.size() != 0){
                FileTokenAna tgtana = new FileTokenAna(tfile);
                List<String> tokenTotal = tgtana.TotalTokenList;
                FileTokenAna pfana = new FileTokenAna(pfile);
                List<String> ptokenTotal = pfana.TotalTokenList;
                for(String token:newDel){
                    if(tokenTotal.contains(token) && !ptokenTotal.contains(token)){
                        T += 1;
                        if(tag)
                            Tright+=1;
                        else {
                            bfw.write(tfile);
                            bfw.write("Same Token Found:" + token + "\n");
                        }
//                        System.out.println("True");
                        break;
                    }
                }
            }
            System.out.println("Done:" + tfile);
            System.out.println(totalcount + " " + Ttotal + " "+ T + " " + Tright +" " + Ftotal + " " + F + " " +Fright);
        }
//        bfw.write(count +" " + totalcount);
//        bfw.close();
//        bfr.close();
        System.out.println(totalcount + " " + Ttotal + " "+ T + " " + Ftotal + " " + F);
//        System.out.println(count +" " + totalcount);
//        String vfile.java = "/home/usr1/Experiment_tools/test_DataSet2/SNYK-JAVA-COMMONSFILEUPLOAD-30401/uninfluenced_package/388e82/DiskFileItem.java/vfile.java.java";
//        String pfile = "/home/usr1/Experiment_tools/test_DataSet2/SNYK-JAVA-COMMONSFILEUPLOAD-30401/uninfluenced_package/388e82/DiskFileItem.java/pfile.java";
////        String[] diff = getDiffFromPV.getDiff(vfile.java, pfile);
//        String vfilecnt = readFile(vfile.java);
//        String pfilecnt = readFile(pfile);
//        Map tokenRev = TokenRev.tokenAna(vfilecnt, pfilecnt);
//        System.out.println("Total:" + tokenRev.toString() + "\n");
//        Map[] max = TokenRev.getMaxToken(tokenRev);
//        System.out.println("MostAdditionToken:" + max[0].toString() + "\n");
//        System.out.println("MostDeletionToken:" + max[1].toString() + "\n");
    }

    public static void main(String[] args) throws IOException, SQLException, DiffException {
        // load properties
        Properties props = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream("./src/Path.properties"));
        props.load(in);
        String jsonpath = props.getProperty("jsonMethod");

//        File f = new File("./DataandRes/FP");
//        File resf = new File("./DataandRes/res_FP");
//        runtest(f, resf, jsonpath);
        File f2 = new File("./DataandRes/resTotal");
        File resf2 = new File("./DataandRes/FP");
        runtest(f2, resf2, jsonpath);
    }


}
