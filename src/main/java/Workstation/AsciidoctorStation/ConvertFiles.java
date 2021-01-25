package Workstation.AsciidoctorStation;

import Workstation.Util.WorkLogger;
import org.asciidoctor.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ConvertFiles implements ConvertRequest{

    private final int index;
    private final String TargetFormat;
    private final ArrayList<File> files;
    private final File OutputFilepath;
    private final SafeMode Safemode;
    private final Attributes attributes;
    private final stylesheet Stylesheet;

    public static ConvertFiles getRequestFromJSONData(JSONObject o, int index){
        WorkLogger.log( "Reading Request "+index );
        WorkLogger.AddIndent();
        //Reading Properties
        String TF = o.has( "format" ) ? o.getString( "format" ) : "pdf";
        WorkLogger.log( "Format: "+TF );
        SafeMode s = o.has( "safemode" ) ? ConvertRequest.getSafeModeFromString( o.getString( "safemode" ) ) : SafeMode.SECURE;
        WorkLogger.log( "Safemode: "+ s.toString() );
        String outputpath = o.has( "output" ) ? o.getString( "output" ) : "output/";
        WorkLogger.log( "Output: "+outputpath );
        //Stylesheet
        stylesheet style = stylesheet.getStylesheetFromJSON( o.getJSONObject( "stylesheets" ) );
        //Attributes
        Attributes attri = null;
        if(o.has( "attributes" )){
            WorkLogger.log( "Attributes" );
            WorkLogger.AddIndent();
            attri = ConvertRequest.GetAttributesFromJson( o.getJSONArray( "attributes" ) );
            WorkLogger.DecIndent();
        }
        //Reading Targets
        String inputfilepath = o.has("input") ? o.getString( "input" ) : "";
        WorkLogger.log( "Reading Targets from "+inputfilepath );
        WorkLogger.AddIndent();
        ArrayList<File> filelist = new ArrayList<>();
        JSONArray target = o.getJSONArray( "targets" );
        for (int i = 0; i < target.length(); i++) {
            String targetstring = target.getString(i);
            File f = new File(inputfilepath+targetstring);
            if(f.isFile()&&f.canRead()){
                WorkLogger.log( "File "+f.getName() );
                filelist.add( f );
            }else {
                WorkLogger.log( "Could not find file "+targetstring );
            }
        }
        WorkLogger.DecIndent();

        WorkLogger.DecIndent();

        return new ConvertFiles( TF, filelist, new File( outputpath ), style, attri, s, index );
    }

    public ConvertFiles(String Target, ArrayList<File> files, File Outputpath, stylesheet stylesheet, Attributes attr, SafeMode Safemode, int idx){
        this.TargetFormat = Target;
        this.files = files;
        this.OutputFilepath = Outputpath;
        this.Stylesheet = stylesheet;
        this.Safemode = Safemode;
        this.attributes = attr;
        this.index=idx;
    }
    @Override
    public void convert() {
        WorkLogger.log( "Starting Conversion "+index );
        WorkLogger.AddIndent();
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        WorkLogger.log( "Adding Library: asciidoctor-diagram" );
        asciidoctor.requireLibrary( "asciidoctor-diagram" );

        WorkLogger.log( "Fetching Attributes..." );
        AttributesBuilder ab = AttributesBuilder.attributes();
        if(attributes!=null)ab.attributes( attributes.map() );

        WorkLogger.log( "Fetching Styles..." );
        WorkLogger.AddIndent();
        if(Stylesheet.getStylesheetExistance()){
            WorkLogger.log( "Adding "+Stylesheet.getAbsoluteStylesheet()+" as Stylesheet" );
            ab.attribute( "pdf-style", Stylesheet.getStylesheetName() );
            ab.attribute( "pdf-themesdir", Stylesheet.getAbsoluteToStylesheet() );
            ab.styleSheetName( Stylesheet.getStylesheetName() );
            ab.stylesDir( Stylesheet.getAbsoluteToStylesheet() );
        }
        if(Stylesheet.getUMLStylesheetExistance()){
            WorkLogger.log( "Adding "+Stylesheet.getAbsoluteUMLStylesheet()+" as Stylesheet" );
            ab.attribute( "plantuml-uml-config", Stylesheet.getAbsoluteUMLStylesheet() );
        }
        if(Stylesheet.getJSONStylesheetExistance()){
            WorkLogger.log( "Adding "+Stylesheet.getAbsoluteJSONStylesheet()+" as Stylesheet" );
            ab.attribute( "plantuml-json-config", Stylesheet.getAbsoluteJSONStylesheet() );
        }
        WorkLogger.DecIndent();
        WorkLogger.log( "Converting Files..." );
        WorkLogger.log( "Loglevel Info" );
        WorkLogger.AddIndent();

        asciidoctor.registerLogHandler( logRecord -> WorkLogger.log(
                logRecord.getSeverity().toString()+": "
                        +logRecord.getCursor().getFile().substring( logRecord.getCursor().getFile().lastIndexOf( '/' )+1 )+" Line: "
                        +logRecord.getCursor().getLineNumber()+" "
                        +logRecord.getMessage()
        ) );

        String[] s = asciidoctor.convertFiles( files,
                                  OptionsBuilder.options()
                                          .safe( Safemode )
                                          .attributes( ab.get() )
                                          .backend( TargetFormat )
                                          .mkDirs( true )
                                          .toDir( OutputFilepath )
                                          .get()
        );
        asciidoctor.shutdown();
        for (String name:s) {
            WorkLogger.log( name );
        }
        WorkLogger.DecIndent();
        WorkLogger.DecIndent();
    }
}
