package Workstation.AsciidoctorStation;

import Workstation.IO.Reader;
import Workstation.Util.WorkLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WorkstationFactory {

    private String inputfile;

    public WorkstationFactory(String file){
        this.inputfile = file;
    }

    public Workstation getWorkstation(){
        WorkLogger.log( "Reading "+inputfile );
        try {
            JSONObject o = new JSONObject( Reader.getFileString( inputfile ) );
            WorkLogger.AddIndent();
            JSONArray a = o.getJSONArray( "requests" );
            ArrayList<ConvertRequest> cr = new ArrayList<>();
            for (int i = 0; i < a.length(); i++) {
                JSONObject request = a.getJSONObject( i );
                if (request.has( "targets" )) {
                    cr.add( ConvertFiles.getRequestFromJSONData( request, i ) );
                } else {
                    cr.add( ConvertDirectory.getRequestFromJSONData( request, i ) );
                }
            }
            WorkLogger.DecIndent();
            return new Workstation( cr );
        }catch (Exception e){
            WorkLogger.log( e.getMessage() );
        }
        return null;
    }
    public void createResources(){
        /*
        if(Write.createDir( "style/")){
            try {

                ClassLoader c1 = this.getClass().getClassLoader();
                String in = new BufferedReader(
                        new InputStreamReader(
                                c1.getResourceAsStream( "resources/main/Styles/dark-theme.yml" )
                        )
                ).lines().collect( Collectors.joining( "\n" )
                );
                Write.WriteToFile( "style/dark-theme.yml", in );
            }catch (Exception e){
                e.printStackTrace();
            }
        }

         */
        //TODO Auspacken der Standard Styles
    }
}
