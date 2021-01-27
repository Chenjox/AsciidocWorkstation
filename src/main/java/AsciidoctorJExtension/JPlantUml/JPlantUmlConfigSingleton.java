package AsciidoctorJExtension.JPlantUml;

import Workstation.IO.Reader;
import Workstation.Util.WorkLogger;

import java.io.File;

public class JPlantUmlConfigSingleton {

    private static JPlantUmlConfigSingleton config;

    private final String singletonconfig;
    private final String filepath;
    private JPlantUmlConfigSingleton(String configfile){
        filepath = configfile;
        singletonconfig = Reader.getFileString(configfile);
    }
    private JPlantUmlConfigSingleton(String s1, String s2){
        filepath = s1;
        singletonconfig = s2;
    }
    private String getSingletonconfig() {
        return singletonconfig;
    }

    public static void reinitializeIfNeeded(String filepath){
        File f = new File( filepath );
        if(f.exists()&&f.isFile()) {
            if (config == null) config = new JPlantUmlConfigSingleton( f.getAbsolutePath() );
            else if(!config.filepath.equals( filepath )) config = new JPlantUmlConfigSingleton( f.getAbsolutePath() );
        }
    }
    public static String getConfig(){
        return !(config==null) ? config.singletonconfig : "";
    }
}
