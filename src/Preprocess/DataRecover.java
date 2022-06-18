package Preprocess;

import Configure.RuntimeConfig;
import Gumtree.DataObj.List_Structure;
import Configure.DataBaseconfig;
import com.github.gumtreediff.io.TreeIoUtils;
import com.github.gumtreediff.tree.Tree;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataRecover {
    public Tree vTree;
    public Tree pTree;
    public List<List_Structure> del_list = new ArrayList<>();
    public List<List_Structure> add_list = new ArrayList<>();


    public DataRecover(String filename, String commitid, String method_longname) throws Exception {
//        long start = System.currentTimeMillis();
        String sql = "select vfile,pfile,vastxml,pastxml from " + DataBaseconfig.Table_fileast + " where filename=? and commitid=? and method_longname=?";
        PreparedStatement preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
        preparedStatement.setString(1,filename);
        preparedStatement.setString(2,commitid);
        preparedStatement.setString(3,method_longname);
        ResultSet rs = preparedStatement.executeQuery();
        if(rs.next()){
            String vTotalXml = rs.getString("vastxml");
            vTree = TreeIoUtils.fromXml().generateFrom().string(vTotalXml).getRoot();
            String pTotalXml = rs.getString("pastxml");
            pTree = TreeIoUtils.fromXml().generateFrom().string(pTotalXml).getRoot();
            String vfilecontent = rs.getString("vfile");
            String pfilecontent = rs.getString("pfile");
            Gumtree.function.tools.FileReaders.writetoFile(vfilecontent, RuntimeConfig.runtimeVfile);
            Gumtree.function.tools.FileReaders.writetoFile(pfilecontent, RuntimeConfig.runtimePfile);
        }
        else {
            throw new Exception("Cannot find trees");
        }
//        long end = System.currentTimeMillis();
//        System.out.println(end-start);

//        start = System.currentTimeMillis();
        sql = "select type,Optype,subtreeSeq from " + DataBaseconfig.Table_changelist + " where filename=? and commitid=? and method_longname=?";
        preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
        preparedStatement.setString(1,filename);
        preparedStatement.setString(2,commitid);
        preparedStatement.setString(3,method_longname);
        rs = preparedStatement.executeQuery();
//        ResultSet rs = DataBaseconfig.stmt.executeQuery(sql);
        while(rs.next()){
            String type = rs.getString("type");
            String Optype = rs.getString("Optype");
            String subtreeSeq = rs.getString("subtreeSeq");
            Tree t;
//            Tree t = TreeIoUtils.fromXml().generateFrom().string(vXML).getRoot();
            if(Optype.equals("Delete")) {
                t = vTree;
                String[] seqs = subtreeSeq.split(",");
                for(int i = seqs.length-1;i>=0;i--){
                    if(!seqs[i].equals(""))
                        t = t.getChild(Integer.parseInt(seqs[i]));
                }
                del_list.add(new List_Structure(t, type, Optype));
            }
            else {
                t = pTree;
                String[] seqs = subtreeSeq.split(",");
                for(int i = seqs.length-1;i>=0;i--){
                    if(!seqs[i].equals(""))
                        t = t.getChild(Integer.parseInt(seqs[i]));
                }
                add_list.add(new List_Structure(t, type, Optype));
            }
        }
//        end = System.currentTimeMillis();
//        System.out.println(end-start);
    }


}
