package Workstation.Util;

import Workstation.IO.Write;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Singleton Class for logging the Process.
 * Produces a log file at termination of the program.
 */
public class WorkLogger {
    private static WorkLogger log;

    private final static SimpleDateFormat timstampPattern = new SimpleDateFormat( "HH:mm:ss");
    private final ArrayList<String> records;
    private int indent;
    private WorkLogger(){
        indent = 0;
        records = new ArrayList<>();
    }
    private void logmessage(String message){
        records.add( messageTolog( message!=null ? message : "Something went wrong here" ) );
    }
    private void decrementIndent(){
        if(indent<=0) indent = 0; else indent--;
    }
    private void incrementIndent(){
        indent++;
    }
    private String messageTolog(String message){
        StringBuilder s = new StringBuilder();
        s
                .append( '[' )
                .append( timstampPattern.format( System.currentTimeMillis() ) )
                .append( "]: " );
        if (indent != 0) {
            for (int i = 0; i < indent - 1; i++) {
                s.append( '|' ).append( ' ' ).append( ' ' ).append( ' ' );
            }
            s.append( '|' ).append( '-' ).append( '-' ).append( ' ' );
        }
        s.append( message );
        return s.toString();
    }
    private void LogToFile(){
        StringBuilder text = new StringBuilder();
        for (String s:records) {
            text.append( s ).append( '\n' );
        }
        Write.WriteToFile("WorkLog.txt", text.toString());
    }
    private static void setIndent(int ind){
        log.indent = ind;
    }
    private static void InitializeLogger(){
        if(log==null)log = new WorkLogger();
    }
    public static void log(String message){
        InitializeLogger();
        log.logmessage( message );
    }
    public static void AddIndent(){
        InitializeLogger();
        log.incrementIndent();
    }
    public static void DecIndent(){
        InitializeLogger();
        log.decrementIndent();
    }
    public static void ToFile(){
        setIndent( 0 );
        log( "Closing Down!" );
        log.LogToFile();
    }
}
