package Preprocess;

import Configure.RuntimeConfig;

import java.io.IOException;

public class Decompile {
    public static void decmp(String classFile) throws IOException {
        String cmd = "java -jar ./lib/procyon-decompiler-0.5.36.jar " + classFile + " > " + RuntimeConfig.runtimeTfile;
        Runtime.getRuntime().exec(cmd);
    }

    public static void unzip(String tPath) throws IOException {
        String cmd = "unzip -d " + RuntimeConfig.runtimeProj + " " + tPath;
        Runtime.getRuntime().exec(cmd);
    }

}
