package DataConstructure;

import Configure.DataBaseconfig;
import Configure.RuntimeConfig;
import Gumtree.DataObj.List_Structure;
import Preprocess.removeMethod;
import com.github.gumtreediff.io.TreeIoUtils;
import Gumtree.function.similaritycalc.GraphBasedSim.GumtreeAna4Graph;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaveIntoDB {


    private static ResultSet selectcontent(String filename, String commit) throws SQLException {
        String sql = "select distinct vfile,pfile,methodname,method_longname from " + DataBaseconfig.Table_vulapis + " where filename=? and commitid=?;";
        PreparedStatement preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
        preparedStatement.setString(1,filename);
        preparedStatement.setString(2,commit);
        return preparedStatement.executeQuery();
    }

    private static void insertData(String filename, String commitid, String method_longname,String vfilecontent, String pfilecontent, String vAST, String pAST) throws SQLException {
        String sql = "insert ignore into " + DataBaseconfig.Table_fileast + " values(?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
        preparedStatement.setString(1,filename);
        preparedStatement.setString(2,commitid);
        preparedStatement.setString(3,vfilecontent);
        preparedStatement.setString(4,pfilecontent);
        preparedStatement.setString(5,vAST);
        preparedStatement.setString(6,pAST);
        preparedStatement.setString(7,method_longname);
        preparedStatement.execute();

    }

    private static void insertSubtreeData(String filename, String commitid, String method_longname, GumtreeAna4Graph gumtreeAna4Graph) throws SQLException {
        String sql = "insert ignore into changelist values(?,?,?,?,?,?)";
        for(List_Structure l:gumtreeAna4Graph.get_delete_list()){
            String subtreeSeq = l.treePosition;
//            String subtree = TreeIoUtils.toXml(gumtreeAna4Graph.srcTree, l.rev_tree).toString();
            PreparedStatement preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
            preparedStatement.setString(1,filename);
            preparedStatement.setString(2,commitid);
            preparedStatement.setString(3,l.type);
            preparedStatement.setString(4,l.OpType);
            preparedStatement.setString(5,subtreeSeq);
            preparedStatement.setString(6,method_longname);
            preparedStatement.execute();
        }
        for(List_Structure l:gumtreeAna4Graph.get_insert_list()){
            String subtreeSeq = l.treePosition;
//            String subtree = TreeIoUtils.toXml(gumtreeAna4Graph.dstTree, l.rev_tree).toString();
            PreparedStatement preparedStatement = DataBaseconfig.conn.prepareStatement(sql);
            preparedStatement.setString(1,filename);
            preparedStatement.setString(2,commitid);
            preparedStatement.setString(3,l.type);
            preparedStatement.setString(4,l.OpType);
            preparedStatement.setString(5,subtreeSeq);
            preparedStatement.setString(6,method_longname);
            preparedStatement.execute();
        }
    }


    public static void save(String filename, String commitid) throws Exception {
        ResultSet rs = selectcontent(filename, commitid);
        while(rs.next()){
            String methodname = rs.getString("methodname");
            String method_longname = rs.getString("method_longname");
            String vfilecontent = rs.getString("vfile");
            String pfilecontent = rs.getString("pfile");
//            writetoTempFile(vfilecontent, runtimeFileV);
//            writetoTempFile(pfilecontent, runtimeFileP);
            removeMethod.getPositionForPV(vfilecontent, pfilecontent, methodname);
            GumtreeAna4Graph gumtreeAna4Graph = new GumtreeAna4Graph(RuntimeConfig.runtimeVfile, RuntimeConfig.runtimePfile);
            String srcTree = TreeIoUtils.toXml(gumtreeAna4Graph.srcTree).toString();
            String dstTree = TreeIoUtils.toXml(gumtreeAna4Graph.dstTree).toString();
            insertData(filename, commitid, method_longname,removeMethod.contentVR, removeMethod.contentPR, srcTree, dstTree);
            insertSubtreeData(filename, commitid, method_longname, gumtreeAna4Graph);
            new File(RuntimeConfig.runtimeVfile).delete();
            new File(RuntimeConfig.runtimePfile).delete();
        }
    }


    public static void main(String[] args) throws Exception {
        save(args[0], args[1]);
//        DataRecover dr = new DataRecover("ContentDisposition.java", "41f40c", "org.springframework.http.ContentDisposition.BuilderImpl.escapeQuotationMarks");
//        System.out.println(1);
    }
}
