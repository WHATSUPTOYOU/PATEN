// Token analysis in target file
package TokenAnalysis.TokenAna;

import TokenAnalysis.TokenAna.Tools.getMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileTokenAna {
    public ArrayList<Map<String,Integer>> methodTokenList;
    public List<String> TotalTokenList;

    public FileTokenAna(String tgtfile) throws IOException {
        TotalTokenList = TokenRev.getTokenListTotal(getMethod.readFileToString(tgtfile));
        methodTokenList = getTokens(tgtfile);
    }

    public FileTokenAna(String tgtfile, String addtionContent) throws IOException {
        TotalTokenList = TokenRev.getTokenListTotal(getMethod.readFileToString(tgtfile) + addtionContent);
        methodTokenList = getTokens(tgtfile);
    }

    private ArrayList<Map<String,Integer>> getTokens(String tgtfile) throws IOException {
        getMethod gt = new getMethod(tgtfile);
        ArrayList<String> methodContent = gt.methods;
        ArrayList<Map<String,Integer>> Tokens = new ArrayList<>();
        for(String content: methodContent){
            Tokens.add(TokenRev.getTokenNm(content, "P"));
        }
        return Tokens;
    }

    public static void main(String[] args) throws IOException {
        FileTokenAna a = new FileTokenAna("/home/usr1/Experiment_tools/test_DataSet2/SNYK-JAVA-ORGNEO4JPROCEDURE-451684/uninfluenced_package/45bc09/Xml.java/apoc-3.5.0.12.jar/tfile.java");
        for(String aa: a.TotalTokenList)
            System.out.println(aa);
    }
}
