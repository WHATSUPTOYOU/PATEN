// get information from database
package TokenAnalysis.TokenAna.Tools;

import java.sql.*;
import java.util.ArrayList;

public class ItemsFromsql {
    public ArrayList<String> addlines = new ArrayList<>();
    public ArrayList<String> dellines = new ArrayList<>();
    public ArrayList<String> methodList = new ArrayList<>();
    String vfile;
    String pfile;
    private static Connection conn;
    private static Statement stmt;

    static {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://localhost:3306/vulnerable_apis?useSSL=false";
        final String USER = "root";
        final String PASS = "asdasd";
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public ItemsFromsql(String snykid, String commitid, String filename) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("select vfile.java,pfile,add_lines,del_lines,method_longname,params from vul_apis where snyk_id='");
        sql.append(snykid);
        sql.append("' and commitid='");
        sql.append(commitid);
        sql.append("' and filename='");
        sql.append(filename);
        sql.append("';");
        String query = sql.toString();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            vfile = rs.getString("vfile.java");
            pfile = rs.getString("pfile");
            addlines.add(rs.getString("add_lines"));
            dellines.add(rs.getString("del_lines"));
            if(!methodList.contains(rs.getString("method_longname") + '/' + rs.getString("params")))
                methodList.add(rs.getString("method_longname") + '/' + rs.getString("params"));
        }
        addlines.remove("0");
        dellines.remove("0");
    }

//    public static void Gumtree.main(String[] args) throws SQLException {
//        ItemsFromsql i = new ItemsFromsql("SNYK-JAVA-ORGAPACHETOMCATEMBED-30957", "b7b5c6", "ContextConfig.java");
//        System.out.println(i.addlines);
//        System.out.println(i.dellines);
//    }
}
