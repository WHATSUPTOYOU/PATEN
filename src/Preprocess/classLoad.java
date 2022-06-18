package Preprocess;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class classLoad extends ClassLoader{
    public Class loadCls(File classFile){
        byte[] classbytes = null;
        Path path = null;
        try {
            path = Paths.get(classFile.getPath());
//            path = Paths.get(new URI(classFile.getPath()));
            classbytes = Files.readAllBytes(path);
        } catch(IOException e){
            e.printStackTrace();
        }
        Class c = defineClass(classbytes, 0, classbytes.length);
        return c;
    }
}
