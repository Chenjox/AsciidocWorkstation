package Workstation.AsciidoctorStation;

import Workstation.Util.WorkLogger;

import java.util.ArrayList;

public class Workstation {
    private final ArrayList<ConvertRequest> requests;

    public Workstation(ArrayList<ConvertRequest> requests){
        this.requests = requests;
    }

    public void Convert(){
        WorkLogger.log( "Starting Conversion" );
        WorkLogger.AddIndent();
        for (ConvertRequest c:requests) {
            c.convert();
        }
        WorkLogger.DecIndent();
    }
}
