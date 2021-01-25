package Workstation.IO;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Write {
    public static void WriteToFile(String file, String text){
        Writer out;
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter( new FileOutputStream( file), StandardCharsets.UTF_8 ));
            out.write(text);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean createDir(String dir){
        File f = new File( dir);
        if(f.exists()&&f.isDirectory())return false;
        return f.mkdirs();
    }
}
