package Workstation.AsciidoctorStation;

import Workstation.Util.WorkLogger;
import org.json.JSONObject;

import java.io.File;

public class stylesheet {

    private final File NormalStylesheet;
    private final File UMLStylesheet;

    public static stylesheet getStylesheetFromJSON(JSONObject o){
        String s1,s2,s3;
        s1 = o.has( "style" ) ? o.getString( "style" ): "style";
        s2 = o.has("uml") ? o.getString( "uml" ):"uml";
        return new stylesheet( s1, s2 );
    }

    public stylesheet(String NormalStylesheet, String UMLStylesheet){
        WorkLogger.log( "Creating Stylesheets" );
        WorkLogger.AddIndent();

        WorkLogger.log( "Reading Stylesheet: "+NormalStylesheet );
        WorkLogger.AddIndent();
        File f = new File( NormalStylesheet );
        WorkLogger.log( "Exists: "+f.exists() );
        WorkLogger.log( "Is a File: "+f.isFile() );
        this.NormalStylesheet = f;
        WorkLogger.DecIndent();

        WorkLogger.log( "Reading UML Stylesheet: "+UMLStylesheet );
        WorkLogger.AddIndent();
        f = new File( UMLStylesheet );
        WorkLogger.log( "Exists: "+f.exists() );
        WorkLogger.log( "Is a File: "+f.isFile() );
        this.UMLStylesheet = f;
        WorkLogger.DecIndent();

        WorkLogger.DecIndent();
    }
    public String getAbsoluteStylesheet(){
        return NormalStylesheet.getAbsolutePath();
    }
    public String getAbsoluteToStylesheet(){
        return NormalStylesheet.getParentFile().getAbsolutePath();
    }
    public String getStylesheetName(){
        return NormalStylesheet.getName();
    }
    public boolean getStylesheetExistance(){
        return NormalStylesheet.isFile()&&NormalStylesheet.canRead();
    }
    public String getAbsoluteUMLStylesheet(){
        return UMLStylesheet.getAbsolutePath();
    }
    public boolean getUMLStylesheetExistance(){
        return UMLStylesheet.isFile()&&UMLStylesheet.canRead();
    }
}
