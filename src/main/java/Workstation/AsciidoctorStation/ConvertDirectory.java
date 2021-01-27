package Workstation.AsciidoctorStation;

import AsciidoctorJExtension.JLatexMath.JLatexBlockProcessor;
import AsciidoctorJExtension.JPlantUml.JPlantUmlBlockProcessor;
import Workstation.Util.WorkLogger;
import org.asciidoctor.*;
import org.asciidoctor.jruby.AsciiDocDirectoryWalker;
import org.asciidoctor.jruby.DirectoryWalker;
import org.json.JSONObject;

import java.io.File;

public class ConvertDirectory implements ConvertRequest{

    private final int index;
    private final String TargetFormat;
    private final File InputFilepath;
    private final File OutputFilepath;
    private final SafeMode Safemode;
    private final Attributes attributes;
    private final stylesheet Stylesheet;

    public static ConvertDirectory getRequestFromJSONData(JSONObject o, int index){
        WorkLogger.log( "Reading Request "+index );
        WorkLogger.AddIndent();
        //Reading Properties
        String TF = o.has( "format" ) ? o.getString( "format" ) : "pdf";
        WorkLogger.log( "Format: "+TF );
        SafeMode s = o.has( "safemode" ) ? ConvertRequest.getSafeModeFromString( o.getString( "safemode" ) ) : SafeMode.SECURE;
        WorkLogger.log( "Safemode: "+ s.toString() );
        String outputpath = o.has( "output" ) ? o.getString( "output" ) : "output/";
        File outputdir = new File( outputpath);
        WorkLogger.log( "Output: "+outputpath );
        String inputpath = o.has("input") ? o.getString( "input" ) : "";
        File inputdir = new File( inputpath );
        WorkLogger.log( "Input: "+inputpath );
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
        WorkLogger.DecIndent();

        return new ConvertDirectory( TF, inputdir, outputdir, style, attri, s, index );
    }

    public ConvertDirectory(String Target, File Inputpath, File Outputpath, stylesheet stylesheet, Attributes attr, SafeMode Safemode, int idx){
        this.TargetFormat = Target;
        this.InputFilepath = Inputpath;
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

        WorkLogger.log( "Adding Extension: asciidoctor-jLaTeX" );
        asciidoctor.javaExtensionRegistry().block( JLatexBlockProcessor.class );
        WorkLogger.log( "Adding Extension: asciidoctor-juml" );
        asciidoctor.javaExtensionRegistry().block( JPlantUmlBlockProcessor.class );

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
        };
        if(Stylesheet.getUMLStylesheetExistance()){
            WorkLogger.log( "Adding "+Stylesheet.getAbsoluteUMLStylesheet()+" as Stylesheet" );
            ab.attribute( "juml-config", Stylesheet.getAbsoluteUMLStylesheet() );
        }

        WorkLogger.DecIndent();
        WorkLogger.log( "Converting Directory..." );
        WorkLogger.log( "Loglevel Info" );
        WorkLogger.AddIndent();
        asciidoctor.registerLogHandler( logRecord -> WorkLogger.log(
                logRecord.getSeverity().toString()+": "
                        +logRecord.getCursor().getFile().substring( logRecord.getCursor().getFile().lastIndexOf( '/' ) )+" Line: "
                        +logRecord.getCursor().getLineNumber()+" "
                        +logRecord.getMessage()
        ) );
        DirectoryWalker d = new AsciiDocDirectoryWalker( InputFilepath.getAbsolutePath() );
        asciidoctor.convertDirectory( d,
                                  OptionsBuilder.options()
                                          .safe( Safemode )
                                          .attributes( ab.get() )
                                          .backend( TargetFormat )
                                          .mkDirs( true )
                                          .toDir( OutputFilepath )
                                          .get()
        );

        asciidoctor.shutdown();
        WorkLogger.DecIndent();
        WorkLogger.DecIndent();
    }
}
