package Configure;

public class RuntimeConfig {
    public static String runtimeVfile = "./vfile.java";
    public static String runtimePfile = "./pfile.java";
    public static String runtimeTfile;
    public static String runtimeProj = "./runtimeFile/Proj";
    public static String resDir = "./runtimeFile/res.json";
    public static double defaultTh = 0.5;
//    public static final String runtimeResFile = "./res.json";
    public static void setRuntimeTFiles(String tfile){
//        runtimeVfile = vfile;
//        runtimePfile = pfile;
        runtimeTfile = tfile;
    }
}
