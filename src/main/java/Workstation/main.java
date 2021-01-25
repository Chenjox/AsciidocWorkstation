package Workstation;

import Workstation.AsciidoctorStation.Workstation;
import Workstation.AsciidoctorStation.WorkstationFactory;
import Workstation.Util.WorkLogger;

import java.io.File;

public class main {
    public static void main(String[] args) {
        WorkLogger.log( "Starting" );
        if(new File( "input.json" ).isFile()) {
            WorkstationFactory f = new WorkstationFactory( "input.json" );
            f.createResources();
            Workstation w = f.getWorkstation();
            w.Convert();
        }else {
            WorkLogger.log( "\"input.json\" not found!" );
        }
        WorkLogger.ToFile();
    }
}
