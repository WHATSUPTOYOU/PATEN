package DataConstructure;

import java.io.File;
import java.util.Objects;


public class runSavePart {
    public static void main(String[] args) throws Exception {
        String DataSetDir = "/home/usr1/Experiment_tools/JarTestData/dataSetFinalSimplify";
        File f = new File(DataSetDir);
        walkDir(f);
    }

    private static void walkDir(File f) throws Exception {
        String vfile = null;
        String commitid = null;
        String filename = null;
        for(File file: Objects.requireNonNull(f.listFiles())){
            if(file.isDirectory())
                walkDir(file);
            else if(file.getName().equals("vfile.java")){
                vfile = file.getAbsolutePath();
                commitid = vfile.split("/")[8];
                filename = vfile.split("/")[9];
            }
        }
        if(vfile != null){
            SaveIntoDB.save(filename, commitid);
            System.out.println(vfile);
        }
    }
}
