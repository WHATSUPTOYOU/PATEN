package TokenAnalysis.main;

import TokenAnalysis.TokenAna.FileTokenAna;
import TokenAnalysis.TokenAna.Tools.getDiffLines;
import TokenAnalysis.TokenAna.Tools.otherTools;
import TokenAnalysis.TokenAna.newDeclToken;
import com.github.difflib.algorithm.DiffException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class newDecCheck {
    public static String jsonpath;
    public static String jsonInputPath;

//    public newDecCheck(){
//        DecCheck(RuntimeFiles.runtimeVfile, RuntimeFiles.runtimePfile, RuntimeFiles.runtimeTfile, )
//    }

    public static String DecCheck(String vfile,String pfile,String tfile) throws IOException, DiffException, SQLException, InterruptedException {
//        URL url = newDecCheck.class.getResource("../src/Path.properties");
//        url.getContent();
//        System.out.println(url);
        List<List<Integer>> res= getDiffLines.compare(vfile, pfile);
        int[] addLines = otherTools.ListtoArray(res.get(0));
        int[] delLines = otherTools.ListtoArray(res.get(1));
        String vfilecnt = otherTools.readFile(vfile);
        String pfilecnt = otherTools.readFile(pfile);

        newDeclToken newDeclToken = new newDeclToken(pfilecnt, vfilecnt,addLines,delLines);
        List<String> newAdd = newDeclToken.newAddtk;
        List<String> newDel = newDeclToken.newDeltk;
        if(newAdd.size() != 0){
//            FileTokenAna tgtana = new FileTokenAna(tfile, new PythonUndCall(tfile).content);
            FileTokenAna tgtana = new FileTokenAna(tfile);
            List<String> tokenTotal = tgtana.TotalTokenList;
            FileTokenAna vfana = new FileTokenAna(vfile);
            List<String> vtokenTotal = vfana.TotalTokenList;
            for(String token:newAdd){
                if(tokenTotal.contains(token) && !vtokenTotal.contains(token)){
                    return "false";
                }
            }
        }

        if(newDel.size() != 0){
            FileTokenAna tgtana = new FileTokenAna(tfile);
//            FileTokenAna tgtana = new FileTokenAna(tfile, new PythonUndCall(tfile).content);
            List<String> tokenTotal = tgtana.TotalTokenList;
            FileTokenAna pfana = new FileTokenAna(pfile);
            List<String> ptokenTotal = pfana.TotalTokenList;
            for(String token:newDel){
                if(tokenTotal.contains(token) && !ptokenTotal.contains(token)){
                    return "true";
                }
            }
        }
        return "Unknown";
    }

//    private String write(){
//        ItemsFromsql ite = new ItemsFromsql(snykid, commitid, filename);
//        ArrayList<String> methodList = ite.methodList;
//    }
}
