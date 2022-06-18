package Gumtree.main;

import Configure.RuntimeConfig;
import Gumtree.DataObj.List_Structure;
import Gumtree.function.tools.*;
import com.github.gumtreediff.client.Run;
import com.github.gumtreediff.gen.TreeGenerators;
import com.github.gumtreediff.tree.Tree;
import Gumtree.function.similaritycalc.GraphBasedSim.GraphSimilarity;
import Gumtree.function.similaritycalc.GraphBasedSim.getGraph;
import Preprocess.DataRecover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphBasedMain {


//    public static void main(String[] args) throws Exception {
//        String filename = args[0].split("/")[9];
//        String commitid = args[0].split("/")[8];
//        String sql = "select distinct method_longname from vul_apis where filename=? and commitid=?";
//        PreparedStatement p = DataBaseconfig.conn.prepareStatement(sql);
//        p.setString(1,filename);
//        p.setString(2,commitid);
//        ResultSet rs = p.executeQuery();
//        while(rs.next()){
//            String method_longname = rs.getString("method_longname");
//            System.out.println(method_longname);
//            run_gumtree_ana(args[0]); //args[4] is temp arg
//            System.out.println("Done");
//        }
//    }

    public static Map<String, String> run_gumtree_ana(String target_file, DataRecover astInfo) throws Exception {
        Map<String, String> res = new HashMap<>();  // analysis result
        if(!getMethod.hasMethod(target_file)) {
            res.put("Analysis Res","Unknown(No target Method)");
            return res;
        }
        String[] Del_Add = getDiff(FileReaders.readFileToString(RuntimeConfig.runtimeVfile), FileReaders.readFileToString(RuntimeConfig.runtimePfile)); // get diff of code change
        Map<String, Integer> tokenChange = TokenAnalysis.tokenAna(Del_Add[0], Del_Add[1]); //  get changed tokens

        List<List_Structure> in_list = astInfo.add_list;
        List<List_Structure> del_list = astInfo.del_list;
        if(in_list.size()==0 && del_list.size()==0){  //if all lists are empty, nothing changed
            res.put("Analysis Res","NoChange");
            return res;
        }


        Run.initGenerators();
        Tree t = TreeGenerators.getInstance().getTree(target_file).getRoot(); // get target tree
//        gumtree_res.getDst().setParent(null);
        getGraph g = new getGraph(in_list, del_list, t, astInfo.vTree, astInfo.pTree);  // get graphs of VFile,PFile,TFile
//        getGraph g = new getGraph(in_list, del_list, t, gumtree_res.getSrc(), gumtree_res.getDst());

//        File file = new File(RuntimeFiles.runtimeResFile);
//        FileWriter fw = new FileWriter(file);
        if(g.no_target_flag) { // if no target subtree found
            doSlicing s = new doSlicing(RuntimeConfig.runtimeVfile, RuntimeConfig.runtimePfile, RuntimeConfig.runtimeTfile); // do slicing
//            if(s.oriStatements.size()==0){
//                res.put("Step","3");
//                res.put("Analysis Res", "false");
//                return res;
//            }
            SlicingGraph slicingGraph; //calculate similarity of origin and target sliced features
            if(s.addFlag)
                slicingGraph = new SlicingGraph(s.slicedLine, s.def_use, astInfo.pTree, t, tokenChange);
            else
                slicingGraph = new SlicingGraph(s.slicedLine, s.def_use, astInfo.vTree, t, tokenChange);
//            getStatmentAST getst = new getStatmentAST(s.oriStatements, s.tgtStatements, tokenChange);
//            res.put("OriSt","");
//            for(String tmp: s.def_use.keySet()){
//                res.put("OriSt",res.get("OriSt")+"-"+tmp);
//            }
//            res.put("TgtSt","");
//            for(String tmp: s.tgtStatements){
//                res.put("TgtSt",res.get("TgtSt")+"-"+tmp);
//            }
            res.put("Tokens","");
            for(String tmp: s.def_use.keySet()){
                res.put("Tokens",res.get("Tokens") +"-"+tmp);
            }
            res.put("Step","3");
            res.put("Analysis Res", "NormalizedDistance is " + slicingGraph.NormDistance);  // distance of sliced part, less than threshold means vulnerable
//            fw.write("(Step3)\n");
//            fw.write("Analysis Res: NormalizedDistance is " + getst.disNorm + "/");
//            fw.write("NO MAPPING! \n");
        }
        else {
            boolean AnaRes;
            GraphSimilarity graphSimilarity = new GraphSimilarity(g.VGraph, g.PGraph, g.TGraph, g.VTmap, g.PTmap, tokenChange); // calculate graph similarity
            res.put("Step","2");
            if(graphSimilarity.Ttotal != 0){
                double defaultTh = RuntimeConfig.defaultTh;
                if(graphSimilarity.Vtotal == 0 && graphSimilarity.Ptotal != 0){
                    if(graphSimilarity.PTsim <= defaultTh){
//                        fw.write("Analysis Res:false with PT Edit distance " + graphSimilarity.PTsim + "\n");
                        res.put("Analysis Res","false");
                        res.put("PT Edit distance",String.valueOf(graphSimilarity.PTsim));
                    }
                    else {
//                        fw.write("Analysis Res:true with PT Edit distance " + graphSimilarity.PTsim + "\n");
                        res.put("Analysis Res","true");
                        res.put("PT Edit distance",String.valueOf(graphSimilarity.PTsim));
                    }
                }
                else if(graphSimilarity.Vtotal != 0 && graphSimilarity.Ptotal == 0){
                    if(graphSimilarity.VTsim <= defaultTh){
                        res.put("Analysis Res","true");
                        res.put("VT Edit distance",String.valueOf(graphSimilarity.VTsim));
//                        fw.write("Analysis Res:true with VT Edit distance " + graphSimilarity.VTsim + "\n");
                    }
                    else {
                        res.put("Analysis Res","false");
                        res.put("VT Edit distance",String.valueOf(graphSimilarity.VTsim));
//                        fw.write("Analysis Res:false with VT Edit distance " + graphSimilarity.VTsim + "\n");
                    }
                }
                else {
//                    fw.write("Analysis Res:" + AnaRes +" with VT Distance/PT Distance" +graphSimilarity.VTsim+" and "+graphSimilarity.PTsim+ "\n");
                    if(graphSimilarity.VTsim < graphSimilarity.PTsim)
                        res.put("Analysis Res", "true");
                    else
                        res.put("Analysis Res", "false");
                    res.put("VT Edit distance",String.valueOf(graphSimilarity.VTsim));
                    res.put("PT Edit distance",String.valueOf(graphSimilarity.PTsim));
                }
            }
            else{
                if(graphSimilarity.Vtotal == 0)
//                    fw.write("Analysis Res:true\n");
                    res.put("Analysis Res","true");
                else if(graphSimilarity.Ptotal == 0)
//                    fw.write("Analysis Res:false\n");
                    res.put("Analysis Res","false");
                else {
//                    AnaRes = graphSimilarity.Vtotal < graphSimilarity.Ptotal;
                    if(graphSimilarity.Vtotal < graphSimilarity.Ptotal)
                        res.put("Analysis Res", "true");
                    else
                        res.put("Analysis Res", "false");
                }
            }
        }
        return res;
//        long endTime=System.currentTimeMillis();
//        res.put("IOCost",)
//        fw.write("IOCost:" + (end-start) +"ms TimeCost:"+(endTime-startTime)+"ms\n");
//        System.out.println(endTime-startTime);
//        fw.close();

    }

    private static String[] getDiff(String vulcontent, String patcontent){
        String[] DelAdd = new String[2];
//        ArrayList<String>[] s = new ArrayList[2];
//        s[0] = new ArrayList<>();
//        s[0] = readFile(vul);
//        s[1] = new ArrayList<>();
//        s[1] = readFile(pat);
        DelAdd[0] = getDiffString(vulcontent.split("\n"), patcontent.split("\n"));
        DelAdd[1] = getDiffString(patcontent.split("\n"), vulcontent.split("\n"));
//        System.out.println(DelAdd[0]);
//        System.out.println(DelAdd[1]);
        return DelAdd;
    }

    private static String getDiffString(String[] ori, String[] tgt){
        String s = "";
        for(String a: ori){
            for(String tmp:tgt)
                if(tmp.equals(a))
                    s += a;
        }
        return s;
    }

}
