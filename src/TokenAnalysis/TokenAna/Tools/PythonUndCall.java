package TokenAnalysis.TokenAna.Tools;

import TokenAnalysis.main.newDecCheck;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class PythonUndCall {

    static String pypath = "/home/usr1/Vulnerable-Apis-Searcher/CallGraghAna/UndAna.py";
    public String content = "";

    public PythonUndCall(String tfile) throws SQLException, IOException, InterruptedException {
        getItemandWrite(tfile);
        callPythonUnd(tfile);
    }

    private void callPythonUnd(String tfile) throws IOException, InterruptedException {
        String command = "python3 " + pypath + " " + tfile;
//        System.out.println(command);
        Process p = Runtime.getRuntime().exec(command);
        InputStream is = p.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        p.waitFor();
        if (p.exitValue() != 0) {
            System.out.println("Wroooooooooooooong");
        }
//        String s;
//        while ((s = reader.readLine()) != null) {
//            System.out.println(s);
//        }
        BufferedReader f = new BufferedReader(new FileReader(newDecCheck.jsonInputPath));
        String s = f.readLine();
        if(s.contains("Error"))
            System.out.println("Error");
        content = JsonRW.parse(s);
    }

//    private void loadProperties() throws IOException {
//        // load properties
//        Properties props = new Properties();
//        InputStream in = new BufferedInputStream(new FileInputStream("./src/Path.properties"));
//        props.load(in);
//        jsonpath = props.getProperty("jsonMethod");
//    }

    private void getItemandWrite(String tfile) throws SQLException, IOException {
        String snykid = tfile.split("/")[6];
        String commitid = tfile.split("/")[8];
        String filename = tfile.split("/")[9];
        // get info in from mysql
        ItemsFromsql ite = new ItemsFromsql(snykid, commitid, filename);
        JsonRW.write(newDecCheck.jsonpath, ite.methodList);
    }

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        System.out.println(new PythonUndCall("/home/usr1/Experiment_tools/JarTestData/dataset_noparam/SNYK-JAVA-ORGAPACHETOMCAT-174534/influenced_package/fb3569/CGIServlet.java/tomcat-catalina-8.0.44.jar/tfile.java").content);
    }
}
