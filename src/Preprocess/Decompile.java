package Preprocess;

import Configure.RuntimeConfig;

import java.io.IOException;

public class Decompile {
    public static void decmp(String classFile) throws IOException, InterruptedException {
        String cmd = "java -jar ./lib/procyon-decompiler-0.5.36.jar " + classFile + " > " + RuntimeConfig.runtimeTfile;
        Process process;
        process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        try {
            process.waitFor();
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println(process);
    }

    public static void unzip(String tPath) throws IOException {
        String cmd = "unzip -d " + RuntimeConfig.runtimeProj + " " + tPath;
        Runtime.getRuntime().exec(cmd);
    }

}
