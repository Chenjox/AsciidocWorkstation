package Workstation.IO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Reader {
    public static String getFileString(String pfilepath){
        StringBuilder contentBuilder = new StringBuilder();
        //UTF-8 Encoding sollte gelernt sein... Die Normale FileReader Klasse hat ein Problem damit
        try (Stream<String> stream = Files.lines( Paths.get( pfilepath), StandardCharsets.UTF_8 )){
            stream.forEach(
                    s -> contentBuilder.append( s ).append( '\n' )
            );
        }catch (IOException e){
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
