package main;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DataStructure.databaseStructure;
import Preprocess.removeMethod;

public class CmdRun {


    public static Connection conn;
    public static Statement stmt;
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

    final static String helpMessage = "Please input the path of your target file(*) with --path or -p and other additional information to search for potential candidates\n" +
            "like : \n" +
            "--cveid or -c: CVENo\n" +
            "--method or -m: method name\n" +
            "--libname or -l: third-party library name, such as log4j-core\n" +
            "--filename or -f: suspicious filename...\n" +
            "e.g. java -jar ./CVEInspector.jar -p /path/to/target -f Suspicious.java";
    final static String runTimeFileV = "./runtimeFile/vfile.java";
    final static String runTimeFileP = "./runtimeFile/vfile.java";

    public static void main(String[] args) throws SQLException, IOException {
        Map<String, String> input = new HashMap<>();
        String tgtFile = null;
        //parse args
        int index = 0;
        if(args.length==0){
            System.out.println("Error Input, you can check rules with --help or -h");
            return;
        }
        while(index <= args.length-1){
            if(args[index].equals("--cveid")||args[index].equals("-c")) {
                index++;
                input.put("cve_no", args[index]);
            }
            else if(args[index].equals("--method")||args[index].equals("-m")){
                index++;
                input.put("methodname", args[index]);
            }
            else if(args[index].equals("--libname")||args[index].equals("-l")){
                index++;
                input.put("artifact_id", args[index]);
            }
            else if(args[index].equals("--filename")||args[index].equals("-f")){
                index++;
                input.put("filename", args[index]);
            }
            else if(args[index].equals("--path")||args[index].equals("-p")){
                index++;
                tgtFile = args[index];
            }
            else if(args[index].equals("--help")||args[index].equals("-h")){
                System.out.println(helpMessage);
                return;
            }
            else {
                System.out.println("Error Input, you can check rules with --help or -h");
                return;
            }
            index++;
        }
        if(tgtFile == null){
            System.out.println("Please provide your path to the target file with --path or -p");
            return;
        }
        if(input.size()==0){
            System.out.println("Please provide additional information");
        }
        String query = getQuery(input);
        System.out.println(query);
        List<databaseStructure> databaseStructures = runQuery(query);
        doAnalysis(databaseStructures, tgtFile);

//        String sql = "select methodname from vul_apis where cve_no='CVE-2021-44832'";

//        while(rs.next()){
//            System.out.println(rs.getString("methodname"));
//        }
    }

    private static List<databaseStructure> runQuery(String sql) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);
        List<String> filesigs = new ArrayList<>();
        List<databaseStructure> databaseStructures = new ArrayList<>();
        while(rs.next()){
            String filename = rs.getString("file_longname");
            String commitid = rs.getString("commitid");
            String filesig = filename+":"+commitid;
            String methodname = rs.getString("methodname");
            String vfile = rs.getString("vfile");
            String pfile = rs.getString("pfile");
            if(!filesigs.contains(filesig)){
                filesigs.add(filesig);
                databaseStructure tuple = new databaseStructure();
                databaseStructures.add(tuple);
                tuple.filename = filename;
                tuple.commitid = commitid;
                tuple.methods.add(methodname);
                tuple.vfile = vfile;
                tuple.pfile = pfile;
            }
            else {
                for(databaseStructure d:databaseStructures){
                    if(d.filename.equals(filename)&&d.commitid.equals(commitid)){
                        d.methods.add(methodname);
                        break;
                    }
                }
            }
        }
        return databaseStructures;
    }

    private static void doAnalysis(List<databaseStructure> databaseStructures, String tgtFile) throws IOException {
        for(databaseStructure d:databaseStructures){
            BufferedWriter bf = new BufferedWriter(new FileWriter(runTimeFileV));
            bf.write(d.vfile);
            bf.close();
            bf = new BufferedWriter(new FileWriter(runTimeFileP));
            bf.write(d.pfile);
            bf.close();
            removeMethod.getPosition(new String[]{runTimeFileV,runTimeFileP,tgtFile}, d.methods);

        }
    }

    private static String getQuery(Map<String, String> args){
        String sqlquery = "select file_longname, commitid, methodname, vfile, pfile from vul_apis where ";
        int index = 0;
        for (String arg : args.keySet()) {
            if (index == 0)
                sqlquery = sqlquery + arg + "='" + args.get(arg) + "'";
            else{
                sqlquery = sqlquery + " and " + arg + "='" + args.get(arg) + "'";
            }
            index++;
        }
        sqlquery += ";";
        return sqlquery;
    }

//    private static boolean getRes(String vfile, String pfile, String tfile, List<String> MethodList){
//
//    }

}
